package com.example.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SearchResultDTO {

    private Integer tourId;
    private Integer categoryId;
    private Integer noOfDays;
    private LocalDate departDate;
    private LocalDate endDate;
    private BigDecimal startingCost;

    public SearchResultDTO(
            Integer tourId,
            Integer categoryId,
            Integer noOfDays,
            LocalDate departDate,
            LocalDate endDate,
            BigDecimal startingCost
    ) {
        this.tourId = tourId;
        this.categoryId = categoryId;
        this.noOfDays = noOfDays;
        this.departDate = departDate;
        this.endDate = endDate;
        this.startingCost = startingCost;
    }

    public Integer getTourId() { return tourId; }
    public Integer getCategoryId() { return categoryId; }
    public Integer getNoOfDays() { return noOfDays; }
    public LocalDate getDepartDate() { return departDate; }
    public LocalDate getEndDate() { return endDate; }
    public BigDecimal getStartingCost() { return startingCost; }
}
