package com.example.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "departure_master")
public class DepartureMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "departure_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryMaster category;

    @Column(name = "depart_date", nullable = false)
    private LocalDate departDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "no_of_days", nullable = false)
    private Integer noOfDays;

    @OneToMany(mappedBy = "departure")
    private Set<TourMaster> tourMasters = new LinkedHashSet<>();

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

    public LocalDate getDepartDate() {
        return departDate;
    }

    public void setDepartDate(LocalDate departDate) {
        this.departDate = departDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getNoOfDays() {
        return noOfDays;
    }

    public void setNoOfDays(Integer noOfDays) {
        this.noOfDays = noOfDays;
    }

    public Set<TourMaster> getTourMasters() {
        return tourMasters;
    }

    public void setTourMasters(Set<TourMaster> tourMasters) {
        this.tourMasters = tourMasters;
    }

}