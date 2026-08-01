package com.example.controller;

import com.example.dto.CategoryDTO;
import com.example.services.CategoryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

 // HOME PAGE → IDS ONLY (NEW)
    @GetMapping("/home/ids")
    public List<Integer> homeCategoryIds() {
        return categoryService.getHomeCategoryIds();
    }

    // HOME PAGE
    @GetMapping("/home")
    public List<CategoryDTO> home() {
        return categoryService.getHomeCategories();
    }

    // CATEGORY CLICK
    @GetMapping("/{catCode}")
    public List<CategoryDTO> onClick(@PathVariable String catCode) {
        return categoryService.onCategoryClick(catCode);
    }
    
 // CATEGORY IDS ONLY
    @GetMapping("/{subcatCode}/ids")
    public List<Integer> getCategoryIdsBySubcatCode(@PathVariable String subcatCode) {
        return categoryService.getCategoryIdsBySubcatCode(subcatCode);
    }


    
    
}
