package com.example.services;

import com.example.repositories.SearchRepository;
import com.example.services.impl.SearchServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private SearchRepository searchRepository;

    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    @DisplayName("getCategoryIdsByMaxCost: Returns matching IDs")
    void testGetCategoryIdsByMaxCost_Success() {
        when(searchRepository.getCategoryIdsByMaxCost(new BigDecimal("20000"))).thenReturn(List.of(1, 2, 3));

        List<Integer> result = searchService.getCategoryIdsByMaxCost(new BigDecimal("20000"));

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("getCategoryIdsByDateRange: Returns matching IDs")
    void testGetCategoryIdsByDateRange_Success() {
        LocalDate from = LocalDate.of(2026, 11, 1);
        LocalDate to = LocalDate.of(2026, 11, 30);
        when(searchRepository.getCategoryIdsByDateRange(from, to)).thenReturn(List.of(10, 20));

        List<Integer> result = searchService.getCategoryIdsByDateRange(from, to);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("getCategoryIdsByName: Returns matching IDs")
    void testGetCategoryIdsByName_Success() {
        when(searchRepository.getCategoryIdsByName("Goa")).thenReturn(List.of(5));

        List<Integer> result = searchService.getCategoryIdsByName("Goa");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5, result.get(0));
    }
}
