package com.example.ai.service;

import com.example.ai.dto.CustomerCareChatRequest;
import com.example.ai.dto.CustomerCareChatResponse;
import com.example.ai.dto.TourRecommendationRequest;
import com.example.ai.dto.TourRecommendationResponse;

public interface CustomerCareAiService {

    /**
     * Primary conversational customer care AI for travelers.
     * Answers booking questions, package inquiries, policy details, and travel advice.
     */
    CustomerCareChatResponse handleCustomerInquiry(CustomerCareChatRequest request);

    /**
     * Provides AI-curated tour recommendations tailored to budget, days, and destination preferences.
     */
    TourRecommendationResponse recommendTours(TourRecommendationRequest request);

    /**
     * Quick FAQ resolution for policies, refunds, cancellations, and travel requirements.
     */
    String getPolicyOrFaqAnswer(String topic);
}
