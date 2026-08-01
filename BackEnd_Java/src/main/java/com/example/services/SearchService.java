package com.example.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SearchService {

    List<Integer> getCategoryIdsByName(String query);

    List<Integer> getCategoryIdsByMaxCost(BigDecimal maxCost);

    List<Integer> getCategoryIdsByDateRange(LocalDate fromDate, LocalDate toDate);

}
