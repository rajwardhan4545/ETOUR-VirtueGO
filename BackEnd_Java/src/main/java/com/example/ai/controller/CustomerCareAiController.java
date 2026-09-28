package com.example.ai.controller;

import com.example.ai.dto.CustomerCareChatRequest;
import com.example.ai.dto.CustomerCareChatResponse;
import com.example.ai.dto.TourRecommendationRequest;
import com.example.ai.dto.TourRecommendationResponse;
import com.example.ai.service.CustomerCareAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/ai/customer-care")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", allowCredentials = "true")
public class CustomerCareAiController {

    private final CustomerCareAiService customerCareAiService;

    @Autowired
    public CustomerCareAiController(CustomerCareAiService customerCareAiService) {
        this.customerCareAiService = customerCareAiService;
    }

    /**
     * Interactive AI Customer Care Chat
     * POST /api/ai/customer-care/chat
     * Body: { "message": "Can you recommend a 5-day tour to Kerala for family?", "sessionId": "optional-uuid" }
     */
    @PostMapping("/chat")
    public ResponseEntity<CustomerCareChatResponse> chatWithCustomerCare(
            @RequestBody CustomerCareChatRequest request) {
        log.info("Received AI Customer Care chat request: {}", request != null ? request.getMessage() : "null");
        CustomerCareChatResponse response = customerCareAiService.handleCustomerInquiry(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Personalized AI Tour & Package Recommendations
     * POST /api/ai/customer-care/recommend-tours
     * Body: { "destination": "Rajasthan", "maxBudgetInINR": 40000, "durationInDays": 6, "travelStyle": "Heritage" }
     */
    @PostMapping("/recommend-tours")
    public ResponseEntity<TourRecommendationResponse> recommendTours(
            @RequestBody TourRecommendationRequest request) {
        log.info("Received AI Tour recommendation request for destination: {}", request != null ? request.getDestination() : "general");
        TourRecommendationResponse response = customerCareAiService.recommendTours(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Instant Policy & FAQ Resolution
     * GET /api/ai/customer-care/faq?topic=refund
     */
    @GetMapping("/faq")
    public ResponseEntity<Map<String, Object>> getFaqAnswer(
            @RequestParam(defaultValue = "cancellation") String topic) {
        String answer = customerCareAiService.getPolicyOrFaqAnswer(topic);
        return ResponseEntity.ok(Map.of(
                "topic", topic,
                "answer", answer,
                "timestamp", LocalDateTime.now()
        ));
    }

    /**
     * Service Status & Healthcheck
     * GET /api/ai/customer-care/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "ETour AI Customer Care (Spring AI + Gemini)",
                "assistant", "Aarya — ETour Travel Concierge",
                "features", new String[]{"chat", "tour-recommendations", "faq-resolver", "policy-guidance"},
                "timestamp", LocalDateTime.now()
        ));
    }
}
