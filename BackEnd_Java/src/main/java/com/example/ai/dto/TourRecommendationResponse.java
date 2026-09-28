package com.example.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourRecommendationResponse {
    private String destination;
    private String aiSummaryRecommendation;
    private List<TourSuggestion> recommendedTours;
    private String bestTimeToVisit;
    private String budgetAdvice;
    private List<String> travelTips;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TourSuggestion {
        private String tourTitle;
        private String highlight;
        private String estimatedDuration;
        private String estimatedPriceRange;
    }
}
