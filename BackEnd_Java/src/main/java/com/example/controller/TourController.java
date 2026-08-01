package com.example.controller;

import com.example.dto.TourDTO;
import com.example.services.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    @Autowired
    private TourService tourService;

    // 🔹 HOME PAGE
    @GetMapping
    public List<TourDTO> getHomePageTours() {
        return tourService.getHomePageTours();
    }

    @GetMapping("/tour-id")
    public Integer getTourId(
            @RequestParam Integer categoryId,
            @RequestParam Integer departureId) {
        return tourService.getTourIdByCategoryAndDeparture(categoryId, departureId);
    }

    // 🔹 DYNAMIC TOUR FETCH (Handles both ID and Subcategory)
    @GetMapping("/{identifier}")
    public List<TourDTO> getTours(@PathVariable String identifier) {
        try {
            // Try as Category ID first
            Integer id = Integer.parseInt(identifier);
            TourDTO tour = tourService.getTourById(id);
            return List.of(tour);
        } catch (NumberFormatException e) {
            // If not a number, treat as Subcategory Code
            return tourService.getToursBySubCategory(identifier);
        }
    }

    @GetMapping("/details/{catId}")
    public List<TourDTO> getToursForDetailsPage(
            @PathVariable Integer catId) {
        return tourService.getToursByCategoryId(catId);
    }

    @PostMapping("/by-category-ids")
    public List<TourDTO> getToursByCategoryIds(@RequestBody List<Integer> ids) {
        return tourService.getToursByCategoryIds(ids);
    }
}