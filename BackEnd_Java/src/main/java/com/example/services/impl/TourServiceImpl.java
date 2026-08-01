package com.example.services.impl;

import com.example.dto.*;
import com.example.entities.*;
import com.example.repositories.*;
import com.example.services.CategoryService;
import com.example.services.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourServiceImpl implements TourService {

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Autowired
    private CostRepository costRepository;

    @Autowired
    private DepartureRepository departureRepository;

    @Autowired
    private TourGuideRepository tourGuideRepository;
    @Autowired
    private CategoryService category;

    // 🔹 /api/tours
    @Override
    public List<TourDTO> getHomePageTours() {

        List<Integer> categoryIds = category.getHomeCategoryIds();

        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }

        return fetchTours(categoryIds);
    }

    // 🔹 /api/tours/{subcat}
    @Override
    public List<TourDTO> getToursBySubCategory(String subCategoryCode) {

        List<Integer> categoryIds = category.getCategoryIdsBySubcatCode(subCategoryCode);

        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }

        return fetchTours(categoryIds);
    }

    // 🔹 COMMON METHOD
    @Override
    public List<TourDTO> getToursByCategoryIds(List<Integer> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }
        return fetchTours(categoryIds);
    }

    private List<TourDTO> fetchTours(List<Integer> categoryIds) {
        return tourRepository.findByCategory_IdIn(categoryIds)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toMap(
                        TourDTO::getCategoryId, // Key: Category ID
                        tour -> tour, // Value: The whole Tour object
                        (existing, replacement) -> existing // Merge function: Keep the first found
                ))
                .values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<TourDTO> getAllTours() {
        return tourRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // In TourServiceImpl.java
    @Override
    public List<TourDTO> getToursByIds(List<Integer> tourIds) {
        return tourRepository.findAllById(tourIds)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ✅ NEW METHOD
    @Override
    public List<TourDTO> getToursByCategoryId(Integer categoryId) {
        return tourRepository.findByCategory_Id(categoryId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TourDTO getTourById(Integer id) {
        return tourRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Tour not found with id: " + id));
    }

    @Override
    public Integer getTourIdByCategoryAndDeparture(
            Integer categoryId,
            Integer departureId) {
        return tourRepository
                .findByCategory_IdAndDeparture_Id(categoryId, departureId)
                .map(TourMaster::getId)
                .orElseThrow(() -> new RuntimeException(
                        "Tour not found for categoryId="
                                + categoryId + " and departureId=" + departureId));
    }

    private TourDTO convertToDTO(TourMaster tour) {

        TourDTO dto = new TourDTO();
        dto.setId(tour.getId());

        // ✅ FIX HERE
        dto.setCategoryId(tour.getCategory().getId());
        dto.setCategoryName(tour.getCategory().getCategoryName());
        dto.setSubCategoryCode(tour.getCategory().getSubcatCode());
        dto.setCategoryCode(tour.getCategory().getCatCode());
        dto.setImagePath(tour.getCategory().getImagePath());
        dto.setJumpFlag(tour.getCategory().getJumpFlag());

        dto.setDepartureId(tour.getDeparture().getId());
        Integer categoryId = tour.getCategory().getId();

        // Itineraries
        List<ItineraryDTO> itineraries = itineraryRepository.findByCategoryId(tour.getCategory().getId())
                .stream().map(it -> {
                    ItineraryDTO iDto = new ItineraryDTO();
                    iDto.setId(it.getId());
                    iDto.setDayNo(it.getDayNo());
                    iDto.setItineraryDetail(it.getItineraryDetail());
                    iDto.setDayWiseImage(it.getDayWiseImage());
                    return iDto;
                }).collect(Collectors.toList());
        dto.setItineraries(itineraries);

        // Costs
        List<CostDTO> costs = costRepository.findByCategoryId(tour.getCategory().getId())
                .stream().map(c -> {
                    CostDTO cDto = new CostDTO();
                    cDto.setId(c.getId());
                    cDto.setSinglePersonCost(c.getSinglePersonCost());
                    cDto.setExtraPersonCost(c.getExtraPersonCost());
                    cDto.setChildWithBedCost(c.getChildWithBedCost());
                    cDto.setChildWithoutBedCost(c.getChildWithoutBedCost());
                    cDto.setValidFrom(c.getValidFrom());
                    cDto.setValidTo(c.getValidTo());
                    return cDto;
                }).collect(Collectors.toList());
        dto.setCosts(costs);

        // Departure Dates
        List<DepartureDTO> departures = departureRepository.findByCategoryId(tour.getCategory().getId())
                .stream().map(d -> {
                    DepartureDTO dDto = new DepartureDTO();
                    dDto.setId(d.getId());
                    dDto.setDepartDate(d.getDepartDate());
                    dDto.setEndDate(d.getEndDate());
                    dDto.setNoOfDays(d.getNoOfDays());
                    return dDto;
                }).collect(Collectors.toList());
        dto.setDepartures(departures);

        // Tour Guides
        List<TourGuideDTO> guides = tourGuideRepository.findByTourId(tour.getId())
                .stream().map(g -> {
                    TourGuideDTO gDto = new TourGuideDTO();
                    gDto.setId(g.getId());
                    gDto.setName(g.getName());
                    gDto.setEmail(g.getEmail());
                    gDto.setPhone(g.getPhone());
                    return gDto;
                }).collect(Collectors.toList());
        dto.setGuides(guides);

        return dto;
    }
}