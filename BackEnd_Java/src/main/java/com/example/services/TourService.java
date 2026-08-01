package com.example.services;

import com.example.dto.TourDTO;
import com.example.entities.TourMaster;

import java.util.List;
import java.util.Optional;

public interface TourService {

    List<TourDTO> getAllTours();

    TourDTO getTourById(Integer id);

    List<TourDTO> getToursByCategoryId(Integer categoryId);

    List<TourDTO> getToursByIds(List<Integer> tourIds);

    List<TourDTO> getHomePageTours();

    List<TourDTO> getToursBySubCategory(String subCategoryCode);

    // ✅ REQUIRED FOR BOOKING

    Integer getTourIdByCategoryAndDeparture(
            Integer categoryId,
            Integer departureId);

    List<TourDTO> getToursByCategoryIds(List<Integer> categoryIds);

}
