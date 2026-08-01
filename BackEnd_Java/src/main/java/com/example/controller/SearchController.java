package com.example.controller;

import com.example.dto.TourDTO;
import com.example.services.SearchService;
import com.example.services.TourService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/Search")
@CrossOrigin(origins = "http://localhost:5173")
public class SearchController {

    private final SearchService searchService;
    private final TourService tourService;

    public SearchController(SearchService searchService, TourService tourService) {
        this.searchService = searchService;
        this.tourService = tourService;
    }

    // GET: /api/search/name/{query}
    @GetMapping("/name/{query}")
    public List<TourDTO> searchByName(@PathVariable String query) {
        List<Integer> ids = searchService.getCategoryIdsByName(query);
        return tourService.getToursByCategoryIds(ids);
    }

    // GET: /api/search/date?from=yyyy-mm-dd&to=yyyy-mm-dd
    @GetMapping("/date")
    public List<TourDTO> searchByDateRange(
            @RequestParam String from,
            @RequestParam String to) {

        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);

        List<Integer> ids = searchService.getCategoryIdsByDateRange(fromDate, toDate);
        return tourService.getToursByCategoryIds(ids);
    }

    // GET: /api/search/cost/{maxCost}
    @GetMapping("/cost/{maxCost}")
    public List<TourDTO> searchByMaxCost(@PathVariable BigDecimal maxCost) {
        List<Integer> ids = searchService.getCategoryIdsByMaxCost(maxCost);
        return tourService.getToursByCategoryIds(ids);
    }
}
