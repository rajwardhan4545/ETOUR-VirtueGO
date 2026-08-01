package com.example.services;

import com.example.dto.CategoryDTO;


import java.util.List;

public interface CategoryService {

    // Home page
    List<CategoryDTO> getHomeCategories();

    List<CategoryDTO> onCategoryClick(String catCode);
    
    List<Integer> getCategoryIdsBySubcatCode(String subcatCode);
    
    List<Integer> getHomeCategoryIds();


}
