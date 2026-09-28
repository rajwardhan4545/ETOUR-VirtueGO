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

    @Override
    public CustomerCareChatResponse handleCustomerInquiry(CustomerCareChatRequest request) {
        String userQuery = (request != null && request.getMessage() != null) ? request.getMessage().trim() : "Hello";
        String sessionId = (request != null && request.getSessionId() != null) ? request.getSessionId() : UUID.randomUUID().toString();

        log.info("Processing ETour AI Customer Care inquiry. Session: {}, Query: {}", sessionId, userQuery);

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

        // 3. Tertiary path: Domain-grounded resilient fallback (guarantees <10ms response)
        if (reply == null || reply.isBlank()) {
            reply = generateDomainFallbackReply(userQuery);
            modelProvider = "ETour Knowledge Base Engine";
        }

        List<String> followUps = generateSmartFollowUps(userQuery);

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

    private String generateDomainFallbackReply(String query) {
        String lower = (query != null) ? query.toLowerCase() : "";

        if (lower.contains("recommend") || lower.contains("package") || lower.contains("holiday") || lower.contains("tour") || lower.contains("suggest")) {
            return "Here are our most popular curated ETour holiday packages for you:\n\n" +
                   "• Kerala Serenity (Alleppey & Munnar) — 5 Days / 4 Nights from ₹25,000/person\n" +
                   "• Royal Rajasthan (Jaipur & Udaipur) — 6 Days / 5 Nights from ₹32,000/person\n" +
                   "• Himachal Alpine Delight (Shimla & Manali) — 6 Days / 5 Nights from ₹24,000/person\n" +
                   "• Goa Coastal Bliss (Beaches & Water Sports) — 4 Days / 3 Nights from ₹15,000/person\n" +
                   "• European Extravaganza (Switzerland, France, Italy) — 10 Days from ₹1,60,000/person\n" +
                   "• Dubai & Desert Safari (Burj Khalifa & Marina) — 5 Days from ₹65,000/person\n\n" +
                   "All packages include verified deluxe stays, daily breakfast, and private transport. Which destination would you like to explore?";
        }

        if (lower.contains("kerala") || lower.contains("munnar") || lower.contains("alleppey")) {
            return "Greetings from ETour! Kerala is one of our most loved domestic destinations.\n\n" +
                   "Our 6-Day Kerala Serenity package covers Cochin, Munnar tea hills, and an overnight houseboat cruise in Alleppey. " +
                   "Prices start from ₹28,500 per person including 4-star hotels, breakfast & dinner, and private AC transfers. Would you like to check available departure dates?";
        }

        if (lower.contains("rajasthan") || lower.contains("jaipur") || lower.contains("udaipur") || lower.contains("jodhpur")) {
            return "Khamma Ghani! Explore our Royal Rajasthan Tour featuring Jaipur's Amber Fort, Jodhpur's blue streets, and Udaipur's Lake Pichola.\n\n" +
                   "Packages range from ₹32,000 to ₹54,000 per person with heritage palace stays and guided excursions. How many travelers are planning to join?";
        }

        if (lower.contains("himachal") || lower.contains("manali") || lower.contains("shimla")) {
            return "Himachal Alpine Delight is an unforgettable mountain getaway!\n\n" +
                   "Enjoy the scenic Mall Road in Shimla, snow points in Solang Valley, and adventure sports in Manali. " +
                   "Packages start from ₹22,000 per person including cozy mountain resort stays and private vehicle transfers.";
        }

        if (lower.contains("goa") || lower.contains("beach")) {
            return "Goa Coastal Bliss offers the perfect blend of relaxation and thrill!\n\n" +
                   "Enjoy water sports at Baga and Calangute, followed by serene South Goa sunset cruises. Packages start at ₹15,000 per person with beachside resort stays.";
        }

        if (lower.contains("europe") || lower.contains("switzerland") || lower.contains("paris")) {
            return "Our European Extravaganza covers Switzerland, France, and Italy across 10-12 unforgettable days.\n\n" +
                   "Includes panoramic train journeys, Eiffel Tower access, Venetian gondola rides, and 4-star accommodations from ₹1,60,000 per person.";
        }

        if (lower.contains("dubai")) {
            return "Discover Dubai with ETour: Burj Khalifa 124th-floor observation deck, thrilling 4x4 Desert Safari with BBQ dinner, and a luxury Marina Dhow Cruise. Packages from ₹65,000 per person.";
        }

        if (lower.contains("cancel") || lower.contains("refund")) {
            return "Here is ETour's Official Cancellation & Refund Policy:\n\n" +
                   "• 15+ Days before departure: 100% Full Refund\n" +
                   "• 7-14 Days before departure: 50% Refund\n" +
                   "• Less than 7 Days: Non-refundable (due to airline & hotel pre-commitments)\n\n" +
                   "Refunds are credited back to your original payment method in 5-7 working days. You can also contact support@etour.com or call 1800-ETOUR-CARE.";
        }

        if (lower.contains("payment") || lower.contains("razorpay") || lower.contains("upi") || lower.contains("card")) {
            return "ETour supports 100% secure, encrypted online payments via Razorpay.\n\n" +
                   "You can pay using UPI (GPay, PhonePe, Paytm), all Credit/Debit cards, and Net Banking. " +
                   "Your confirmed ticket and GST tax invoice PDF will be generated immediately upon successful transaction.";
        }

        if (lower.contains("booking") || lower.contains("status") || lower.contains("ticket") || lower.contains("invoice")) {
            return "Welcome to ETour Customer Care! You can easily track your booking status, download passenger tickets, and view tax invoices " +
                   "by logging into your account and clicking 'My Bookings' in the top navigation bar.";
        }

        if (lower.contains("contact") || lower.contains("call") || lower.contains("support") || lower.contains("phone")) {
            return "ETour Customer Support is available 24/7:\n\n" +
                   "• Email: support@etour.com\n" +
                   "• Toll-Free Helpline: 1800-ETOUR-CARE (1800-386-8722)\n" +
                   "• Live Concierge: Right here in this chat window!\n\n" +
                   "Feel free to ask any travel question, and I will assist you instantly.";
        }

        return "Namaste! Welcome to ETour (VirtueGO). I am Aarya, your 24/7 AI Travel Concierge.\n\n" +
               "Whether you are planning a domestic escape to Kerala, Rajasthan, or Himachal, or dreaming of Dubai or Europe, " +
               "I can help with personalized itineraries, budget recommendations, and booking assistance. Where would you like to travel next?";
    }

    private List<String> generateSmartFollowUps(String query) {
        String lower = (query != null) ? query.toLowerCase() : "";
        if (lower.contains("cancel") || lower.contains("refund") || lower.contains("policy")) {
            return List.of("How do I cancel my booking?", "What is the refund timeline?", "Can I reschedule my tour dates?");
        }
        if (lower.contains("kerala") || lower.contains("rajasthan") || lower.contains("himachal") || lower.contains("goa")) {
            return List.of("What is included in the package?", "What are the departure dates?", "Are family discounts available?");
        }
        return List.of(
                "Show popular holiday packages",
                "How does the cancellation policy work?",
                "Recommend a budget trip under ₹35,000"
        );
    }
}
