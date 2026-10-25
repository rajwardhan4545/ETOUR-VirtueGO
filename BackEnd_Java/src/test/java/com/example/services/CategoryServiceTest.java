package com.example.services;

import com.example.dto.CategoryDTO;
import com.example.entities.CategoryMaster;
import com.example.repositories.CategoryRepository;
import com.example.services.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private CategoryMaster parentCat;
    private CategoryMaster childCat;

    @BeforeEach
    void setUp() {
        parentCat = new CategoryMaster();
        parentCat.setId(10);
        parentCat.setCatCode("IND");
        parentCat.setCategoryName("India");
        parentCat.setSubcatCode("^");

        childCat = new CategoryMaster();
        childCat.setId(20);
        childCat.setCatCode("MAH");
        childCat.setCategoryName("Maharashtra");
        childCat.setSubcatCode("IND");
    }

    @Test
    @DisplayName("getHomeCategories: Returns list of root categories")
    void testGetHomeCategories_Success() {
        when(categoryRepository.findBySubcatCode("^")).thenReturn(List.of(parentCat));

        List<CategoryDTO> result = categoryService.getHomeCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("IND", result.get(0).getCatCode());
        assertEquals("India", result.get(0).getCategoryName());
    }

    @Test
    @DisplayName("getHomeCategoryIds: Returns integer IDs of home categories")
    void testGetHomeCategoryIds_Success() {
        when(categoryRepository.findBySubcatCode("^")).thenReturn(List.of(parentCat));

        List<Integer> result = categoryService.getHomeCategoryIds();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10, result.get(0));
    }

    @Test
    @DisplayName("onCategoryClick: Returns sub-categories for parent code")
    void testOnCategoryClick_Success() {
        when(categoryRepository.findBySubcatCode("IND")).thenReturn(List.of(childCat));

        List<CategoryDTO> result = categoryService.onCategoryClick("IND");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("MAH", result.get(0).getCatCode());
    }

    @Test
    @DisplayName("getCategoryIdsBySubcatCode: Returns matching IDs")
    void testGetCategoryIdsBySubcatCode_Success() {
        when(categoryRepository.findCategoryIdsBySubcatCode("IND")).thenReturn(List.of(20));

        List<Integer> result = categoryService.getCategoryIdsBySubcatCode("IND");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(20, result.get(0));
    }
}
