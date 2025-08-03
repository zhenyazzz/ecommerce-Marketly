package org.com.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.com.productservice.dto.category.CategoryRequest;
import org.com.productservice.dto.category.CategoryResponse;
import org.com.productservice.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private CategoryRequest testCategoryRequest;
    private CategoryResponse testCategoryResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
        objectMapper = new ObjectMapper();

        testCategoryRequest = CategoryRequest.builder()
                .name("Electronics")
                .build();

        testCategoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("Electronics")
                .build();
    }

    @Test
    void createCategory_ShouldReturnCreatedCategory() throws Exception {
        // Given
        when(categoryService.createCategory(any(CategoryRequest.class)))
                .thenReturn(testCategoryResponse);

        // When & Then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCategoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"));

        verify(categoryService).createCategory(any(CategoryRequest.class));
    }

    @Test
    void getCategoryById_ShouldReturnCategory() throws Exception {
        // Given
        when(categoryService.getCategoryById(1L)).thenReturn(testCategoryResponse);

        // When & Then
        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"));

        verify(categoryService).getCategoryById(1L);
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategory() throws Exception {
        // Given
        CategoryRequest updateRequest = CategoryRequest.builder()
                .name("Updated Electronics")
                .build();

        CategoryResponse updatedResponse = CategoryResponse.builder()
                .id(1L)
                .name("Updated Electronics")
                .build();

        when(categoryService.updateCategory(anyLong(), any(CategoryRequest.class)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Electronics"));

        verify(categoryService).updateCategory(1L, updateRequest);
    }

    @Test
    void deleteCategory_ShouldReturn204() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(1L);
    }

    @Test
    void getAllCategories_ShouldReturnPageOfCategories() throws Exception {
        // Given
        List<CategoryResponse> categories = Arrays.asList(
                CategoryResponse.builder().id(1L).name("Electronics").build(),
                CategoryResponse.builder().id(2L).name("Clothing").build()
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<CategoryResponse> categoryPage = new PageImpl<>(categories, pageable, 2);

        when(categoryService.getAllCategories(any(Pageable.class))).thenReturn(categoryPage);

        // When & Then
        mockMvc.perform(get("/api/categories")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Electronics"))
                .andExpect(jsonPath("$.content[1].name").value("Clothing"));

        verify(categoryService).getAllCategories(any(Pageable.class));
    }

    @Test
    void rebuildCache_ShouldReturn200() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/categories/cache/rebuild"))
                .andExpect(status().isOk());

        verify(categoryService).rebuildCategoryCache();
    }

    @Test
    void createCategory_WithInvalidData_ShouldReturn400() throws Exception {
        // Given
        CategoryRequest invalidRequest = CategoryRequest.builder()
                .name("A") // Too short - violates @Size(min = 2)
                .build();

        // When & Then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
} 