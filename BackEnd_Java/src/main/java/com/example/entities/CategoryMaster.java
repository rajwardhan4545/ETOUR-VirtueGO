package com.example.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "category_master")
public class CategoryMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Integer id;

    @Column(name = "cat_code", nullable = false, length = 3)
    private String catCode;

    @Column(name = "subcat_code", length = 3)
    private String subcatCode;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(name = "image_path")
    private String imagePath;

    @ColumnDefault("0")
    @Column(name = "jump_flag")
    private Boolean jumpFlag;

    @OneToMany(mappedBy = "category")
    private Set<CostMaster> costMasters = new LinkedHashSet<>();

    @OneToMany(mappedBy = "category")
    private Set<DepartureMaster> departureMasters = new LinkedHashSet<>();

    @OneToMany(mappedBy = "category")
    private Set<ItineraryMaster> itineraryMasters = new LinkedHashSet<>();

    @OneToMany(mappedBy = "category")
    private Set<TourMaster> tourMasters = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCatCode() {
        return catCode;
    }

    public void setCatCode(String catCode) {
        this.catCode = catCode;
    }

    public String getSubcatCode() {
        return subcatCode;
    }

    public void setSubcatCode(String subcatCode) {
        this.subcatCode = subcatCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Boolean getJumpFlag() {
        return jumpFlag;
    }

    public void setJumpFlag(Boolean jumpFlag) {
        this.jumpFlag = jumpFlag;
    }

    public Set<CostMaster> getCostMasters() {
        return costMasters;
    }

    public void setCostMasters(Set<CostMaster> costMasters) {
        this.costMasters = costMasters;
    }

    public Set<DepartureMaster> getDepartureMasters() {
        return departureMasters;
    }

    public void setDepartureMasters(Set<DepartureMaster> departureMasters) {
        this.departureMasters = departureMasters;
    }

    public Set<ItineraryMaster> getItineraryMasters() {
        return itineraryMasters;
    }

    public void setItineraryMasters(Set<ItineraryMaster> itineraryMasters) {
        this.itineraryMasters = itineraryMasters;
    }

    public Set<TourMaster> getTourMasters() {
        return tourMasters;
    }

    public void setTourMasters(Set<TourMaster> tourMasters) {
        this.tourMasters = tourMasters;
    }

}