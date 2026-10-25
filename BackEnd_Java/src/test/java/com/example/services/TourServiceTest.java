package com.example.services;

import com.example.dto.TourDTO;
import com.example.entities.CategoryMaster;
import com.example.entities.DepartureMaster;
import com.example.entities.TourMaster;
import com.example.repositories.*;
import com.example.services.impl.TourServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourServiceTest {

    @Mock
    private TourRepository tourRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ItineraryRepository itineraryRepository;

    @Mock
    private CostRepository costRepository;

    @Mock
    private DepartureRepository departureRepository;

    @Mock
    private TourGuideRepository tourGuideRepository;

    @InjectMocks
    private TourServiceImpl tourService;

    private TourMaster tourMaster;
    private CategoryMaster categoryMaster;
    private DepartureMaster departureMaster;

    @BeforeEach
    void setUp() {
        categoryMaster = new CategoryMaster();
        categoryMaster.setId(10);
        categoryMaster.setCategoryName("Heritage");
        categoryMaster.setCatCode("HER");
        categoryMaster.setSubcatCode("^");
        categoryMaster.setJumpFlag(false);

        departureMaster = new DepartureMaster();
        departureMaster.setId(901);

        tourMaster = new TourMaster();
        tourMaster.setId(501);
        tourMaster.setCategory(categoryMaster);
        tourMaster.setDeparture(departureMaster);
    }

    @Test
    @DisplayName("getTourById: Existing tour ID returns TourDTO")
    void testGetTourById_Success() {
        when(tourRepository.findById(501)).thenReturn(Optional.of(tourMaster));

        TourDTO result = tourService.getTourById(501);

        assertNotNull(result);
        assertEquals(501, result.getId());
    }

    @Test
    @DisplayName("getTourById: Non-existent tour ID throws RuntimeException")
    void testGetTourById_NotFound_ThrowsException() {
        when(tourRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> tourService.getTourById(999));
        assertTrue(ex.getMessage().contains("Tour not found with id: 999"));
    }

    @Test
    @DisplayName("getAllTours: Returns all tours in repository")
    void testGetAllTours_Success() {
        when(tourRepository.findAll()).thenReturn(List.of(tourMaster));

        List<TourDTO> result = tourService.getAllTours();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(501, result.get(0).getId());
    }

    @Test
    @DisplayName("getHomePageTours: Empty category IDs returns empty list")
    void testGetHomePageTours_EmptyCategories_ReturnsEmptyList() {
        when(categoryService.getHomeCategoryIds()).thenReturn(List.of());

        List<TourDTO> result = tourService.getHomePageTours();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tourRepository, never()).findByCategory_IdIn(any());
    }
}
