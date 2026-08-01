package com.example.services.impl;

import com.example.repositories.SearchRepository;
import com.example.services.SearchService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final SearchRepository searchRepository;

    public SearchServiceImpl(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Override
    public List<Integer> getCategoryIdsByMaxCost(BigDecimal maxCost) {
        return searchRepository.getCategoryIdsByMaxCost(maxCost);
    }

    @Override
    public List<Integer> getCategoryIdsByDateRange(LocalDate fromDate, LocalDate toDate) {
        return searchRepository.getCategoryIdsByDateRange(fromDate, toDate);
    }

    @Override
    public List<Integer> getCategoryIdsByName(String query) {
        return searchRepository.getCategoryIdsByName(query);
    }

}
