package com.example.ai.service;

import com.example.ai.dto.CustomerCareChatRequest;
import com.example.ai.dto.CustomerCareChatResponse;
import com.example.ai.dto.TourRecommendationRequest;
import com.example.ai.dto.TourRecommendationResponse;
import com.example.entities.CategoryMaster;
import com.example.repositories.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class CustomerCareAiServiceImpl implements CustomerCareAiService {

    private final ChatClient chatClient;
    private final ChatModel chatModel;
    private final CategoryRepository categoryRepository;
    private final RestTemplate restTemplate;

    @Value("${spring.ai.google.genai.api-key:${GEMINI_API_KEY:}}")
    private String geminiApiKey;

    @Value("${spring.ai.google.genai.chat.options.model:gemini-3-flash-preview}")
    private String geminiModel;

    private static final String ETOUR_SYSTEM_PROMPT = """
        You are "Aarya", the intelligent, friendly, and knowledgeable AI Customer Care & Tour Concierge for ETour (VirtueGO).
        ETour is a premier travel booking portal that offers unforgettable domestic and international holidays,
        custom tour packages, flight/itinerary coordination, passenger ticket booking, and travel assistance.

        Key ETour Information & Policies:
        1. Popular Tours:
           - Kerala Serenity (Alleppey houseboats, Munnar tea hills, Kochi heritage) - 5-7 days (~Rs. 25,000 - 45,000/person)
           - Royal Rajasthan (Jaipur, Udaipur, Jodhpur forts and palaces) - 6-8 days (~Rs. 30,000 - 55,000/person)
           - Himachal Alpine Delight (Shimla, Manali, Solang Valley) - 6-7 days (~Rs. 22,000 - 40,000/person)
           - Goa Coastal Bliss (North & South Goa beaches, water sports) - 4-5 days (~Rs. 15,000 - 30,000/person)
           - European Extravaganza (Switzerland, France, Italy) - 10-12 days (~Rs. 1,50,000 - 2,50,000/person)
           - Dubai & Desert Safari (Burj Khalifa, Marina, Dunes) - 5-6 days (~Rs. 60,000 - 90,000/person)
        2. Booking & Cancellation Policy:
           - Full 100% refund for cancellations made 15 days or more before the departure date.
           - 50% refund for cancellations between 7 to 14 days before departure.
           - No refund within 7 days of departure (due to pre-booked hotels and transport commitments).
        3. Payment Options:
           - Secure payment via Razorpay, Credit/Debit cards, Net Banking, and UPI.
           - E-Invoices and passenger itinerary PDFs are generated instantly upon confirmed booking.
        4. Customer Support Contact:
           - Email: support@etour.com | Toll-Free: 1800-ETOUR-CARE | Hours: 24/7

        Guidelines for your responses:
        - Be warm, helpful, professional, and concise.
        - Give clear price ranges in Indian Rupees (INR) and suggest concrete itineraries.
        - End with an encouraging travel thought or a helpful follow-up question.
        """;

    @Autowired
    public CustomerCareAiServiceImpl(
            @Autowired(required = false) ChatModel chatModel,
            @Autowired(required = false) ChatClient.Builder chatClientBuilder,
            @Autowired(required = false) CategoryRepository categoryRepository,
            @Autowired(required = false) RestTemplate restTemplate) {
        this.chatModel = chatModel;
        this.chatClient = (chatClientBuilder != null && chatModel != null)
                ? chatClientBuilder.defaultSystem(ETOUR_SYSTEM_PROMPT).build()
                : (chatModel != null ? ChatClient.builder(chatModel).defaultSystem(ETOUR_SYSTEM_PROMPT).build() : null);
        this.categoryRepository = categoryRepository;
        this.restTemplate = createFastRestTemplate();
    }

    private static RestTemplate createFastRestTemplate() {
        org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(1800);
        factory.setReadTimeout(2200);
        return new RestTemplate(factory);
    }

    private final Map<String, String> sessionDestinations = new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public CustomerCareChatResponse handleCustomerInquiry(CustomerCareChatRequest request) {
        String userQuery = (request != null && request.getMessage() != null) ? request.getMessage().trim() : "Hello";
        String sessionId = (request != null && request.getSessionId() != null) ? request.getSessionId() : UUID.randomUUID().toString();

        log.info("Processing ETour AI Customer Care inquiry. Session: {}, Query: {}", sessionId, userQuery);

        // Update destination memory for this session
        trackSessionContext(sessionId, userQuery);

        String reply = null;
        String modelProvider = "Spring AI (Google Gemini)";

        // 1. Primary path: Use Spring AI ChatClient with strict 2.5s timeout
        if (chatClient != null) {
            try {
                reply = java.util.concurrent.CompletableFuture.supplyAsync(() ->
                        chatClient.prompt()
                                .user(userQuery)
                                .call()
                                .content()
                ).get(2500, java.util.concurrent.TimeUnit.MILLISECONDS);
            } catch (Exception ex) {
                log.warn("Spring AI ChatClient call timed out or failed: {}. Attempting fast fallback...", ex.getMessage());
            }
        }

        // 2. Secondary path: Direct Gemini REST fallback (fast timeout)
        if (reply == null || reply.isBlank()) {
            reply = callGeminiDirectRest(userQuery, ETOUR_SYSTEM_PROMPT);
            if (reply != null && !reply.isBlank()) {
                modelProvider = "Google Gemini Direct (" + geminiModel + ")";
            }
        }

        // 3. Tertiary path: Domain-grounded context-aware engine (guarantees <5ms exact answers)
        if (reply == null || reply.isBlank()) {
            reply = generateDomainFallbackReply(userQuery, sessionId);
            modelProvider = "ETour Knowledge Base Engine";
        }

        List<String> followUps = generateSmartFollowUps(userQuery, sessionId);

        return CustomerCareChatResponse.builder()
                .reply(reply)
                .sessionId(sessionId)
                .assistantName("Aarya — ETour Travel Concierge")
                .timestamp(LocalDateTime.now())
                .success(true)
                .suggestedFollowUps(followUps)
                .modelProvider(modelProvider)
                .build();
    }

    private void trackSessionContext(String sessionId, String query) {
        String lower = query.toLowerCase();
        if (lower.contains("kerala") || lower.contains("munnar") || lower.contains("alleppey")) {
            sessionDestinations.put(sessionId, "Kerala");
        } else if (lower.contains("rajasthan") || lower.contains("jaipur") || lower.contains("udaipur") || lower.contains("jodhpur")) {
            sessionDestinations.put(sessionId, "Rajasthan");
        } else if (lower.contains("himachal") || lower.contains("manali") || lower.contains("shimla")) {
            sessionDestinations.put(sessionId, "Himachal Pradesh");
        } else if (lower.contains("goa")) {
            sessionDestinations.put(sessionId, "Goa");
        } else if (lower.contains("europe") || lower.contains("switzerland") || lower.contains("paris") || lower.contains("italy")) {
            sessionDestinations.put(sessionId, "Europe");
        } else if (lower.contains("dubai")) {
            sessionDestinations.put(sessionId, "Dubai");
        } else if (lower.contains("kashmir") || lower.contains("srinagar") || lower.contains("gulmarg")) {
            sessionDestinations.put(sessionId, "Kashmir");
        } else if (lower.contains("andaman") || lower.contains("port blair") || lower.contains("havelock")) {
            sessionDestinations.put(sessionId, "Andaman");
        }
    }

    @Override
    public TourRecommendationResponse recommendTours(TourRecommendationRequest request) {
        String dest = (request != null && request.getDestination() != null) ? request.getDestination() : "Popular Destinations";
        Double budget = (request != null && request.getMaxBudgetInINR() != null) ? request.getMaxBudgetInINR() : 50000.0;
        Integer days = (request != null && request.getDurationInDays() != null) ? request.getDurationInDays() : 5;
        String style = (request != null && request.getTravelStyle() != null) ? request.getTravelStyle() : "Family Vacation";

        String prompt = String.format(
                "A traveler wants tour recommendations for '%s' with a budget of Rs. %.0f for %d days. Travel style: '%s'. " +
                "Provide a brief 2-3 sentence personalized trip recommendation, followed by key highlights, best time to visit, and travel tips.",
                dest, budget, days, style
        );

        String aiResponse = null;
        if (chatClient != null) {
            try {
                aiResponse = java.util.concurrent.CompletableFuture.supplyAsync(() ->
                        chatClient.prompt().user(prompt).call().content()
                ).get(2500, java.util.concurrent.TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.warn("Recommendation AI call timed out or failed: {}", e.getMessage());
            }
        }

        if (aiResponse == null || aiResponse.isBlank()) {
            aiResponse = callGeminiDirectRest(prompt, ETOUR_SYSTEM_PROMPT);
        }

        if (aiResponse == null || aiResponse.isBlank()) {
            aiResponse = generateDomainTourSummary(dest, days, budget, style);
        }

        List<TourRecommendationResponse.TourSuggestion> suggestions = new ArrayList<>();
        suggestions.add(TourRecommendationResponse.TourSuggestion.builder()
                .tourTitle(dest + " Classic Highlights Tour")
                .highlight("Complete sightseeing, 4-star hotel stays, breakfast, and airport transfers.")
                .estimatedDuration(days + " Days / " + (days - 1) + " Nights")
                .estimatedPriceRange("₹" + String.format("%,.0f", budget * 0.7) + " - ₹" + String.format("%,.0f", budget))
                .build());

        suggestions.add(TourRecommendationResponse.TourSuggestion.builder()
                .tourTitle(dest + " Premium Leisure Experience")
                .highlight("Luxury heritage resort stays, private chauffeur, and curated local food walks.")
                .estimatedDuration((days + 1) + " Days / " + days + " Nights")
                .estimatedPriceRange("₹" + String.format("%,.0f", budget * 0.9) + " - ₹" + String.format("%,.0f", budget * 1.25))
                .build());

        return TourRecommendationResponse.builder()
                .destination(dest)
                .aiSummaryRecommendation(aiResponse)
                .recommendedTours(suggestions)
                .bestTimeToVisit("October through March (pleasant weather, ideal for sightseeing)")
                .budgetAdvice("Book at least 3 weeks in advance on ETour to secure up to 15% early-bird flight & hotel savings.")
                .travelTips(List.of(
                        "Carry a valid government ID for all passengers during check-in.",
                        "Download your confirmed ETour booking PDF on your phone before arrival.",
                        "Check local weather forecasts to pack appropriate seasonal clothing."
                ))
                .build();
    }

    @Override
    public String getPolicyOrFaqAnswer(String topic) {
        String cleanTopic = (topic != null) ? topic.toLowerCase().trim() : "general";

        if (cleanTopic.contains("cancel") || cleanTopic.contains("refund")) {
            return "ETour Cancellation & Refund Policy:\n" +
                   "• 15+ Days before departure: 100% Full Refund.\n" +
                   "• 7 to 14 Days before departure: 50% Partial Refund.\n" +
                   "• Less than 7 Days: Non-refundable due to airline and hotel commitments.\n" +
                   "Refunds are credited back to the original payment method within 5-7 business days.";
        }

        if (cleanTopic.contains("payment") || cleanTopic.contains("razorpay") || cleanTopic.contains("upi")) {
            return "Payment Support:\n" +
                   "ETour accepts all major Credit/Debit Cards, UPI (GPay, PhonePe, Paytm), and Net Banking via Razorpay.\n" +
                   "Instant GST invoice and booking confirmation are emailed as soon as payment succeeds.";
        }

        if (cleanTopic.contains("visa") || cleanTopic.contains("passport") || cleanTopic.contains("international")) {
            return "International Travel Guidelines:\n" +
                   "Passports must be valid for at least 6 months from the departure date.\n" +
                   "ETour assists with visa invitation letters and itinerary documentation for all booked international tours.";
        }

        return handleCustomerInquiry(CustomerCareChatRequest.builder()
                .message("Explain the policy regarding: " + topic)
                .build()).getReply();
    }

    private String generateDomainTourSummary(String dest, Integer days, Double budget, String style) {
        return String.format(
                "For %s, a %d-day %s with a budget of ₹%,.0f is an ideal holiday combination! " +
                "ETour's curated package includes 4-star handpicked accommodations, private air-conditioned transport, " +
                "complimentary daily breakfast, and a dedicated local tour manager. " +
                "We recommend booking at least 2 weeks in advance to secure optimal flight connections and preferred room categories.",
                dest, days, style, budget
        );
    }

    /**
     * Direct REST fallback invoking Gemini API endpoint with model resilience.
     */
    private String callGeminiDirectRest(String userQuery, String systemPrompt) {
        try {
            // Fast attempt on active Gemini model
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + geminiModel + ":generateContent?key=" + geminiApiKey;

            Map<String, Object> userPart = Map.of("text", systemPrompt + "\n\nUser Question: " + userQuery);
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(Map.of("parts", List.of(userPart)))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List candidates = (List) response.getBody().get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map candidate = (Map) candidates.get(0);
                    Map content = (Map) candidate.get("content");
                    if (content != null) {
                        List parts = (List) content.get("parts");
                        if (parts != null && !parts.isEmpty()) {
                            Map part = (Map) parts.get(0);
                            String text = (String) part.get("text");
                            if (text != null && !text.isBlank()) {
                                return text.trim();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Direct Gemini REST call attempt returned: {}", e.getMessage());
        }
        return null;
    }

    private String generateDomainFallbackReply(String query, String sessionId) {
        String lower = (query != null) ? query.toLowerCase().trim() : "";
        String currentDest = sessionDestinations.getOrDefault(sessionId, "our holiday tours");

        // 1. Rescheduling & Date Changes
        if (lower.contains("reschedule") || lower.contains("postpone") || lower.contains("change date") || lower.contains("change my tour") || lower.contains("different date")) {
            return "Yes, you can reschedule your ETour booking!\n\n" +
                   "• **10+ Days Before Departure**: Free date rescheduling (subject only to seasonal hotel rate differences).\n" +
                   "• **3 to 9 Days Before Departure**: A nominal rescheduling fee of 15% applies to adjust partner hotel and transport reservations.\n" +
                   "• **Within 72 Hours**: Rescheduling depends on hotel discretion; please contact our travel desk immediately.\n\n" +
                   "To request a date change, simply email your Booking ID to **support@etour.com** or call **1800-ETOUR-CARE**.";
        }

        // 2. Cancellation Process Steps
        if (lower.contains("how do i cancel") || lower.contains("how to cancel") || lower.contains("steps to cancel") || lower.contains("cancel booking") || lower.contains("cancel my")) {
            return "Here is how you can cancel your ETour booking:\n\n" +
                   "1. **Log In** to your ETour account.\n" +
                   "2. Go to **'My Bookings'** in the navigation bar.\n" +
                   "3. Select your active trip and click **'Request Cancellation'**.\n" +
                   "4. Review the calculated refund based on our policy:\n" +
                   "   • 15+ Days before trip: **100% Full Refund**\n" +
                   "   • 7 to 14 Days: **50% Refund**\n" +
                   "   • Less than 7 Days: Non-refundable\n" +
                   "5. Confirm your cancellation. Your refund will be credited back to your original payment method in **5-7 business days**.";
        }

        // 3. Refund Timeline
        if (lower.contains("refund timeline") || lower.contains("how long") || lower.contains("when will i get") || lower.contains("refund time") || lower.contains("refund status")) {
            return "ETour Refund Timeline Details:\n\n" +
                   "• **Initiation**: Approved refunds are triggered immediately through our secure Razorpay gateway.\n" +
                   "• **Bank Credit Time**: 5 to 7 working days depending on your bank / UPI provider.\n" +
                   "• **Tracking**: You will receive an instant email and SMS containing the Refund Reference Number (RRN) to monitor the credit status directly with your bank.";
        }

        // 4. General Cancellation & Refund Policy
        if (lower.contains("cancel") || lower.contains("refund") || lower.contains("cancellation policy")) {
            return "Here is ETour's Official Cancellation & Refund Policy:\n\n" +
                   "• **15+ Days before departure**: **100% Full Refund**\n" +
                   "• **7 to 14 Days before departure**: **50% Partial Refund**\n" +
                   "• **Less than 7 Days before departure**: Non-refundable (due to pre-booked flights, transport, and hotel rooms)\n\n" +
                   "All refunds are credited to your original payment method in 5-7 business days. For assistance, reach us at support@etour.com or toll-free 1800-ETOUR-CARE.";
        }

        // 5. Departure Dates & Batch Schedules
        if (lower.contains("departure") || lower.contains("depart") || lower.contains("date") || lower.contains("when can i go") || lower.contains("schedule") || lower.contains("batch")) {
            if ("Kerala".equalsIgnoreCase(currentDest)) {
                return "Departure schedules for **Kerala Serenity Tours**:\n\n" +
                       "• **Fixed Group Departures**: Every **Wednesday** and **Saturday** throughout the year.\n" +
                       "• **Boarding Points**: Direct airport pickups from Cochin (COK) or Trivandrum (TRV).\n" +
                       "• **Private Customized Tours**: Depart on **any date of your choice** with private AC sedan/SUV!\n\n" +
                       "Would you like to reserve a seat for an upcoming weekend or check hotel inclusions?";
            } else if ("Rajasthan".equalsIgnoreCase(currentDest)) {
                return "Departure schedules for **Royal Rajasthan Tours**:\n\n" +
                       "• **Fixed Group Batches**: Every **Sunday** and **Thursday** from Jaipur Airport / Railway Station.\n" +
                       "• **Private Departures**: Available daily for families and couples on flexible dates.\n\n" +
                       "Would you like to book a heritage palace package or customize your itinerary?";
            } else if ("Himachal Pradesh".equalsIgnoreCase(currentDest)) {
                return "Departure schedules for **Himachal Alpine Tours**:\n\n" +
                       "• **Weekly Departures**: Every **Friday evening** from Delhi (Majnu Ka Tilla / Kashmiri Gate) & Chandigarh.\n" +
                       "• **Flight Connections**: Daily pickup available from Chandigarh (IXC) & Bhuntar/Kullu (KUU) airports.\n\n" +
                       "Would you like to review package highlights or family pricing?";
            } else {
                return "ETour departure options for " + currentDest + ":\n\n" +
                       "• **Fixed Group Departures**: Run weekly every **Wednesday** and **Saturday** across all major holiday circuits.\n" +
                       "• **Private Tailored Trips**: Depart on **any day of the year** with personalized cab and hotel reservations.\n\n" +
                       "Which month or dates are you planning your holiday for?";
            }
        }

        // 6. Inclusions & Exclusions
        if (lower.contains("included") || lower.contains("inclusion") || lower.contains("exclusion") || lower.contains("what is included") || lower.contains("hotel stay") || lower.contains("breakfast")) {
            return "Standard Inclusions across ETour Holiday Packages:\n\n" +
                   "✅ **Deluxe Hotel Stays**: Verified 3-star / 4-star properties with top hygiene ratings.\n" +
                   "✅ **Daily Breakfast**: Complimentary morning buffet at all destinations.\n" +
                   "✅ **Private AC Vehicle**: Dedicated chauffeur for all transfers, sightseeing, and intercity travel.\n" +
                   "✅ **Sightseeing & Permits**: All toll taxes, interstate permits, parking, and driver allowances.\n" +
                   "✅ **Local Guidance**: Experienced English/Hindi speaking tour managers.\n\n" +
                   "*(Optional add-ons: Flight tickets, adventure sports, and monument entry tickets).*";
        }

        // 7. Family & Group Discounts
        if (lower.contains("family discount") || lower.contains("group discount") || lower.contains("discount") || lower.contains("kid") || lower.contains("child") || lower.contains("senior") || lower.contains("concession")) {
            return "ETour Family & Group Savings Policy:\n\n" +
                   "• **Children under 5 Years**: **100% Free** (sharing bed with parents).\n" +
                   "• **Children aged 5 to 11 Years**: **50% Discount** with extra mattress provided.\n" +
                   "• **Group Savings (6+ Adults)**: Flat **5% to 10% instant discount** applied at checkout.\n" +
                   "• **Senior Citizens (60+)**: Special priority assistance and complimentary travel insurance.\n\n" +
                   "How many adults and children will be traveling in your group?";
        }

        // 8. Food & Dietary Needs
        if (lower.contains("food") || lower.contains("veg") || lower.contains("jain") || lower.contains("meal") || lower.contains("dinner") || lower.contains("lunch")) {
            return "Food & Meal Inclusions on ETour:\n\n" +
                   "• **Complimentary Daily Breakfast** is included in all hotel bookings.\n" +
                   "• **100% Pure Veg & Jain Meals** are guaranteed on all domestic family group tours.\n" +
                   "• For customized private tours, you can choose EP (Room Only), CP (Breakfast), MAP (Breakfast + Dinner), or AP (All Meals) during booking.\n\n" +
                   "Do you have any specific dietary preferences for your journey?";
        }

        // 9. Best Time to Visit & Weather
        if (lower.contains("best time") || lower.contains("season") || lower.contains("weather") || lower.contains("when to visit") || lower.contains("climate")) {
            if ("Kerala".equalsIgnoreCase(currentDest)) {
                return "The best time to visit **Kerala** is from **September to March** when the weather is pleasant and comfortable for houseboats and hill stations. Monsoon (June-August) is world-famous for Ayurvedic wellness retreats!";
            } else if ("Himachal Pradesh".equalsIgnoreCase(currentDest)) {
                return "The best time to visit **Himachal** is **October to February** for fresh snowfall in Manali and Solang, or **March to June** for pleasant summer sightseeing and flower blooms.";
            } else if ("Rajasthan".equalsIgnoreCase(currentDest)) {
                return "The ideal time for **Rajasthan** is **October to March**, offering cool breezes and clear skies perfect for exploring grand forts, desert camps, and royal palaces.";
            } else if ("Goa".equalsIgnoreCase(currentDest)) {
                return "The best time for **Goa** is **November to February** for water sports, beach shacks, and nightlife.";
            } else {
                return "For " + currentDest + ", **October through April** is the most popular holiday season with pleasant climate and full sightseeing access. Would you like recommended dates?";
            }
        }

        // 10. Destination Specific Guides
        if (lower.contains("kerala") || lower.contains("munnar") || lower.contains("alleppey")) {
            return "Greetings from ETour! **Kerala Serenity Tour** (5-7 Days):\n\n" +
                   "• **Munnar**: Misty tea plantations, Cheeyappara waterfalls, Eravikulam National Park.\n" +
                   "• **Alleppey**: Private backwater cruise with overnight stay in traditional AC Houseboat.\n" +
                   "• **Kochi**: Historic Chinese fishing nets, Fort Kochi heritage, and Jewish Synagogue.\n" +
                   "• **Package Price**: Starting from ₹25,000/person (deluxe hotels, meals, transport included).\n\n" +
                   "Would you like to check departure dates or customize this for your family?";
        }

        if (lower.contains("rajasthan") || lower.contains("jaipur") || lower.contains("udaipur") || lower.contains("jodhpur")) {
            return "Khamma Ghani! **Royal Rajasthan Tour** (6-8 Days):\n\n" +
                   "• **Jaipur**: Grand Amber Fort, Hawa Mahal, and vibrant local bazaars.\n" +
                   "• **Jodhpur**: Majestic Mehrangarh Fort and blue-painted old town.\n" +
                   "• **Udaipur**: Romantic Lake Pichola boat ride and City Palace.\n" +
                   "• **Package Price**: Starting from ₹32,000/person including heritage palace stays.\n\n" +
                   "How many travelers are planning to join this royal journey?";
        }

        if (lower.contains("himachal") || lower.contains("manali") || lower.contains("shimla")) {
            return "Experience **Himachal Alpine Delight** (6-7 Days):\n\n" +
                   "• **Shimla**: Scenic Mall Road, Christ Church, and Kufri panoramic viewpoints.\n" +
                   "• **Manali**: Solang Valley adventure sports, Atal Tunnel, and Hadimba Temple.\n" +
                   "• **Package Price**: Starting from ₹22,000/person with cozy mountain resort stays.\n\n" +
                   "Are you traveling with family or planning an adventure trip with friends?";
        }

        if (lower.contains("goa") || lower.contains("beach")) {
            return "Welcome to **Goa Coastal Bliss** (4-5 Days):\n\n" +
                   "• **North Goa**: Calangute, Baga beach water sports, and historic Aguada Fort.\n" +
                   "• **South Goa**: Peaceful Colva beach, Basilica of Bom Jesus, and sunset river cruise.\n" +
                   "• **Package Price**: Starting from ₹15,000/person with beach resort stays.\n\n" +
                   "Would you like recommendations for family resorts or nightlife spots?";
        }

        if (lower.contains("europe") || lower.contains("switzerland") || lower.contains("paris") || lower.contains("italy")) {
            return "Welcome to **European Extravaganza** (10-12 Days):\n\n" +
                   "• **France**: Paris city tour, Eiffel Tower access, and Seine River cruise.\n" +
                   "• **Switzerland**: Mount Titlis rotating cable car, Lucerne lake, and Alpine valleys.\n" +
                   "• **Italy**: Venice gondola ride, Florence Renaissance art, and Rome Colosseum.\n" +
                   "• **Package Price**: Starting from ₹1,60,000/person (includes Eurail, 4-star hotels & visa support).\n\n" +
                   "Do you hold a valid Schengen visa or would you like visa guidance?";
        }

        if (lower.contains("dubai")) {
            return "Experience **Dubai & Desert Safari** (5-6 Days):\n\n" +
                   "• **City Icons**: Burj Khalifa 124th-floor observation deck & Dubai Mall.\n" +
                   "• **Desert Thrills**: 4x4 Dune Bashing, camel riding, and BBQ dinner under the stars.\n" +
                   "• **Marina Cruise**: Luxury Dhow dinner cruise with live entertainment.\n" +
                   "• **Package Price**: Starting from ₹65,000/person.\n\n" +
                   "Would you like to include Abu Dhabi's Sheikh Zayed Grand Mosque?";
        }

        if (lower.contains("kashmir")) {
            return "Welcome to **Paradise on Earth — Kashmir** (5-6 Days):\n\n" +
                   "• **Srinagar**: Dal Lake Shikara ride, overnight stay in luxury wooden Houseboat.\n" +
                   "• **Gulmarg**: World's highest Gondola cable car ride and snow meadows.\n" +
                   "• **Pahalgam**: Valley of Shepherds, Betaab Valley, and Aru Valley pine forests.\n" +
                   "• **Package Price**: Starting from ₹26,000/person.\n\n" +
                   "Would you like to check upcoming departure dates?";
        }

        // 11. Popular Holiday Packages
        if (lower.contains("recommend") || lower.contains("package") || lower.contains("holiday") || lower.contains("tour") || lower.contains("popular") || lower.contains("suggest")) {
            return "Here are our top-rated curated ETour holiday packages:\n\n" +
                   "1. **Kerala Serenity** (Alleppey & Munnar) — 5D/4N from ₹25,000/person\n" +
                   "2. **Royal Rajasthan** (Jaipur & Udaipur) — 6D/5N from ₹32,000/person\n" +
                   "3. **Himachal Alpine Delight** (Shimla & Manali) — 6D/5N from ₹22,000/person\n" +
                   "4. **Goa Coastal Bliss** (Beaches & Watersports) — 4D/3N from ₹15,000/person\n" +
                   "5. **European Extravaganza** (Switzerland, France, Italy) — 10D from ₹1,60,000/person\n" +
                   "6. **Dubai & Desert Safari** (Burj Khalifa & Dunes) — 5D from ₹65,000/person\n\n" +
                   "All packages include verified deluxe stays, breakfast, and private transfers. Which destination would you like to explore?";
        }

        // 12. Booking, Ticket & Invoice Downloads
        if (lower.contains("booking") || lower.contains("ticket") || lower.contains("invoice") || lower.contains("receipt") || lower.contains("status")) {
            return "Managing Your Bookings on ETour:\n\n" +
                   "• **Download Ticket & Itinerary**: Log in and visit **'My Bookings'** to access your confirmed passenger voucher and detailed day-wise itinerary PDF.\n" +
                   "• **Download GST Invoice**: Click the 'Download Invoice' button next to your confirmed payment record.\n" +
                   "• **Live Tracking**: View departure timings, hotel addresses, and driver contact details 24 hours prior to travel.";
        }

        // 13. Payments & Security
        if (lower.contains("payment") || lower.contains("razorpay") || lower.contains("upi") || lower.contains("card") || lower.contains("pay")) {
            return "Payment Information & Security:\n\n" +
                   "• **Supported Modes**: UPI (GPay, PhonePe, Paytm), All Credit & Debit Cards, Net Banking, and flexible EMI options.\n" +
                   "• **Security**: Payments are encrypted with 256-bit bank-grade security through Razorpay.\n" +
                   "• **Instant Confirmation**: Your booking ID and digital invoice are generated immediately upon transaction completion.";
        }

        // 14. Customer Support Contacts
        if (lower.contains("contact") || lower.contains("support") || lower.contains("help") || lower.contains("phone") || lower.contains("call") || lower.contains("helpline") || lower.contains("agent")) {
            return "ETour Customer Support is available 24/7:\n\n" +
                   "• **Toll-Free Helpline**: 1800-ETOUR-CARE (1800-386-8722)\n" +
                   "• **Email Desk**: support@etour.com\n" +
                   "• **Live Concierge**: Available right here in this chat window!\n\n" +
                   "How can I assist your travel plans today?";
        }

        // 15. Default Warm Concierge Introduction
        return "Namaste! Welcome to ETour (VirtueGO). I am Aarya, your 24/7 AI Travel Concierge.\n\n" +
               "Whether you are planning a domestic escape to Kerala, Rajasthan, or Himachal, or dreaming of Dubai or Europe, " +
               "I can help with personalized itineraries, cancellation terms, departure dates, and booking guidance. What destination or question would you like to explore?";
    }

    private List<String> generateSmartFollowUps(String query, String sessionId) {
        String lower = (query != null) ? query.toLowerCase() : "";
        String currentDest = sessionDestinations.getOrDefault(sessionId, "");

        if (lower.contains("cancel") || lower.contains("refund") || lower.contains("policy")) {
            return List.of("How do I cancel my booking?", "What is the refund timeline?", "Can I reschedule my tour dates?");
        }
        if (lower.contains("reschedule") || lower.contains("date")) {
            return List.of("What are the departure dates?", "What is the cancellation policy?", "Are family discounts available?");
        }
        if (!currentDest.isEmpty() || lower.contains("kerala") || lower.contains("rajasthan") || lower.contains("himachal") || lower.contains("goa")) {
            return List.of("What are the departure dates?", "What is included in the package?", "Are family discounts available?");
        }
        return List.of(
                "Show popular holiday packages",
                "How does the cancellation policy work?",
                "Recommend a budget trip under ₹35,000"
        );
    }
}
