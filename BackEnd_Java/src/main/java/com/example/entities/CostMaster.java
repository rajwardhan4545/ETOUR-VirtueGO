package com.example.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cost_master")
public class CostMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cost_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryMaster category;

    @Column(name = "single_person_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal singlePersonCost;

    @Column(name = "extra_person_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal extraPersonCost;

    @Column(name = "child_with_bed_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal childWithBedCost;

    @Column(name = "child_without_bed_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal childWithoutBedCost;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to", nullable = false)
    private LocalDate validTo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CategoryMaster getCategory() {
        return category;
    }

    public void setCategory(CategoryMaster category) {
        this.category = category;
    }

    public BigDecimal getSinglePersonCost() {
        return singlePersonCost;
    }

    public void setSinglePersonCost(BigDecimal singlePersonCost) {
        this.singlePersonCost = singlePersonCost;
    }

    public BigDecimal getExtraPersonCost() {
        return extraPersonCost;
    }

    public void setExtraPersonCost(BigDecimal extraPersonCost) {
        this.extraPersonCost = extraPersonCost;
    }

    public BigDecimal getChildWithBedCost() {
        return childWithBedCost;
    }

    public void setChildWithBedCost(BigDecimal childWithBedCost) {
        this.childWithBedCost = childWithBedCost;
    }

    public BigDecimal getChildWithoutBedCost() {
        return childWithoutBedCost;
    }

    public void setChildWithoutBedCost(BigDecimal childWithoutBedCost) {
        this.childWithoutBedCost = childWithoutBedCost;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

}