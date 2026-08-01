package com.example.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CostDTO {
    private Integer id;
    private BigDecimal singlePersonCost;
    private BigDecimal extraPersonCost;
    private BigDecimal childWithBedCost;
    private BigDecimal childWithoutBedCost;
    private LocalDate validFrom;
    private LocalDate validTo;

    // getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public BigDecimal getSinglePersonCost() { return singlePersonCost; }
    public void setSinglePersonCost(BigDecimal singlePersonCost) { this.singlePersonCost = singlePersonCost; }

    public BigDecimal getExtraPersonCost() { return extraPersonCost; }
    public void setExtraPersonCost(BigDecimal extraPersonCost) { this.extraPersonCost = extraPersonCost; }

    public BigDecimal getChildWithBedCost() { return childWithBedCost; }
    public void setChildWithBedCost(BigDecimal childWithBedCost) { this.childWithBedCost = childWithBedCost; }

    public BigDecimal getChildWithoutBedCost() { return childWithoutBedCost; }
    public void setChildWithoutBedCost(BigDecimal childWithoutBedCost) { this.childWithoutBedCost = childWithoutBedCost; }

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }
}
