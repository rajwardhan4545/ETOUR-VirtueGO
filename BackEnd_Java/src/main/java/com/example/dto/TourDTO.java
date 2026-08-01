package com.example.dto;

import java.util.List;

public class TourDTO {

    private Integer id;
 // ✅ ADD THIS
    private Boolean jumpFlag;
    // Category
    private Integer categoryId;
    private String CategoryCode;
    // getters & setters
    public Boolean getJumpFlag() {
        return jumpFlag;
    }

    public void setJumpFlag(Boolean jumpFlag) {
        this.jumpFlag = jumpFlag;
    }
    public String getCategoryCode() {
		return CategoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		CategoryCode = categoryCode;
	}
	private String categoryName;
    private String subCategoryCode;

   public String getImagePath() {
      return imagePath;
   }

   public void setImagePath(String imagePath) {
      this.imagePath = imagePath;
   }

   private String imagePath;
    // Departure
    private Integer departureId;

    private List<ItineraryDTO> itineraries;
    private List<CostDTO> costs;
    private List<DepartureDTO> departures;
    private List<TourGuideDTO> guides;

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getSubCategoryCode() { return subCategoryCode; }
    public void setSubCategoryCode(String subCategoryCode) { this.subCategoryCode = subCategoryCode; }

    public Integer getDepartureId() { return departureId; }
    public void setDepartureId(Integer departureId) { this.departureId = departureId; }

    public List<ItineraryDTO> getItineraries() { return itineraries; }
    public void setItineraries(List<ItineraryDTO> itineraries) { this.itineraries = itineraries; }

    public List<CostDTO> getCosts() { return costs; }
    public void setCosts(List<CostDTO> costs) { this.costs = costs; }

    public List<DepartureDTO> getDepartures() { return departures; }
    public void setDepartures(List<DepartureDTO> departures) { this.departures = departures; }

    public List<TourGuideDTO> getGuides() { return guides; }
    public void setGuides(List<TourGuideDTO> guides) { this.guides = guides; }
}