package com.example.dto;

import java.math.BigDecimal;

public class BookingCreateRequestDTO {

    private Integer customerId;
    private Integer tourId;
    private Integer noOfPax;
    private BigDecimal tourAmount;
    private BigDecimal taxes;
    private Integer statusId;

    // getters & setters
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getTourId() {
        return tourId;
    }

    public void setTourId(Integer tourId) {
        this.tourId = tourId;
    }

    public Integer getNoOfPax() {
        return noOfPax;
    }

    public void setNoOfPax(Integer noOfPax) {
        this.noOfPax = noOfPax;
    }

    public BigDecimal getTourAmount() {
        return tourAmount;
    }

    public void setTourAmount(BigDecimal tourAmount) {
        this.tourAmount = tourAmount;
    }

    public BigDecimal getTaxes() {
        return taxes;
    }

    public void setTaxes(BigDecimal taxes) {
        this.taxes = taxes;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }
}
