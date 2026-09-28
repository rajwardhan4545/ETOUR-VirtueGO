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
public class TourRecommendationRequest {
    private String destination;
    private Double maxBudgetInINR;
    private Integer durationInDays;
    private Integer numberOfTravelers;
    private String travelStyle; // e.g. "Family", "Adventure", "Honeymoon", "Solo", "Pilgrimage"
    private List<String> interests; // e.g. ["Beaches", "Temples", "Trekking", "Food"]
}
