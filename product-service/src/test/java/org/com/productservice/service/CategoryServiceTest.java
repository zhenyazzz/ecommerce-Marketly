package org.com.productservice.service;

import org.com.productservice.dto.category.CategoryRequest;
import org.com.productservice.dto.category.CategoryResponse;
import org.com.productservice.exception.AlreadyExistsException;
import org.com.productservice.exception.CategoryNotFoundException;
import org.com.productservice.mapper.CategoryMapper;
import org.com.productservice.model.Category;
import org.com.productservice.repository.CategoryRepository;
import org.com.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryRequest categoryRequest;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();

        categoryRequest = CategoryRequest.builder()
                .name("Electronics")
                .build();

        categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("Electronics")
                .build();
    }

    @Test
    void createCategory_ShouldCreateAndReturnCategory() {
        // Given
        when(categoryRepository.findByNameIgnoreCase("Electronics")).thenReturn(Optional.empty());
        when(categoryMapper.toCategory(categoryRequest)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toCategoryResponse(category)).thenReturn(categoryResponse);

        // When
        CategoryResponse result = categoryService.createCategory(categoryRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Electronics", result.getName());
        verify(categoryRepository).findByNameIgnoreCase("Electronics");
        verify(categoryMapper).toCategory(categoryRequest);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toCategoryResponse(category);
    }

    @Test
    void createCategory_WithExistingName_ShouldThrowException() {
        // Given
        when(categoryRepository.findByNameIgnoreCase("Electronics")).thenReturn(Optional.of(category));

        // When & Then
        assertThrows(AlreadyExistsException.class, () -> categoryService.createCategory(categoryRequest));
        verify(categoryRepository).findByNameIgnoreCase("Electronics");
        verify(categoryMapper, never()).toCategory(any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void getCategoryById_ShouldReturnCategory() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toCategoryResponse(category)).thenReturn(categoryResponse);

        // When
        CategoryResponse result = categoryService.getCategoryById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Electronics", result.getName());
        verify(categoryRepository).findById(1L);
        verify(categoryMapper).toCategoryResponse(category);
    }

    @Test
    void getCategoryById_WithNonExistentId_ShouldThrowException() {
        // Given
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryById(999L));
        verify(categoryRepository).findById(999L);
        verify(categoryMapper, never()).toCategoryResponse(any());
    }

    @Test
    void updateCategory_ShouldUpdateAndReturnCategory() {
        // Given
        CategoryRequest updateRequest = CategoryRequest.builder().name("Updated Electronics").build();
        Category updatedCategory = Category.builder().id(1L).name("Updated Electronics").build();
        CategoryResponse updatedResponse = CategoryResponse.builder().id(1L).name("Updated Electronics").build();

        when(categoryRepository.getCategoryById(1L)).thenReturn(category);
        when(categoryRepository.findByNameIgnoreCase("Updated Electronics")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(categoryMapper.toCategoryResponse(updatedCategory)).thenReturn(updatedResponse);

        // When
        CategoryResponse result = categoryService.updateCategory(1L, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Electronics", result.getName());
        verify(categoryRepository).getCategoryById(1L);
        verify(categoryRepository).findByNameIgnoreCase("Updated Electronics");
        verify(categoryRepository).save(any(Category.class));
        verify(categoryMapper).toCategoryResponse(updatedCategory);
    }

    @Test
    void updateCategory_WithExistingName_ShouldThrowException() {
        // Given
        CategoryRequest updateRequest = CategoryRequest.builder().name("Existing Category").build();
        Category existingCategory = Category.builder().id(2L).name("Existing Category").build();

        when(categoryRepository.getCategoryById(1L)).thenReturn(category);
        when(categoryRepository.findByNameIgnoreCase("Existing Category")).thenReturn(Optional.of(existingCategory));

        // When & Then
        assertThrows(AlreadyExistsException.class, () -> categoryService.updateCategory(1L, updateRequest));
        verify(categoryRepository).getCategoryById(1L);
        verify(categoryRepository).findByNameIgnoreCase("Existing Category");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_WithSameName_ShouldNotThrowException() {
        // Given
        CategoryRequest updateRequest = CategoryRequest.builder().name("Electronics").build();

        when(categoryRepository.getCategoryById(1L)).thenReturn(category);
        when(categoryRepository.findByNameIgnoreCase("Electronics")).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toCategoryResponse(category)).thenReturn(categoryResponse);

        // When
        CategoryResponse result = categoryService.updateCategory(1L, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Electronics", result.getName());
        verify(categoryRepository).getCategoryById(1L);
        verify(categoryRepository).findByNameIgnoreCase("Electronics");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void deleteCategory_ShouldDeleteCategory() {
        // Given
        when(productRepository.existsByCategoryId(1L)).thenReturn(false);

        // When
        categoryService.deleteCategory(1L);

        // Then
        verify(productRepository).existsByCategoryId(1L);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void deleteCategory_WithProducts_ShouldThrowException() {
        // Given
        when(productRepository.existsByCategoryId(1L)).thenReturn(true);

        // When & Then
        assertThrows(IllegalStateException.class, () -> categoryService.deleteCategory(1L));
        verify(productRepository).existsByCategoryId(1L);
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void getAllCategories_ShouldReturnPageOfCategories() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = Arrays.asList(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, 1);
        List<CategoryResponse> responses = Arrays.asList(categoryResponse);
        Page<CategoryResponse> expectedPage = new PageImpl<>(responses, pageable, 1);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toCategoryResponse(category)).thenReturn(categoryResponse);

        // When
        Page<CategoryResponse> result = categoryService.getAllCategories(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("Electronics", result.getContent().get(0).getName());
        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toCategoryResponse(category);
    }

    @Test
    void getAllCategories_WithEmptyPage_ShouldReturnEmptyPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(categoryRepository.findAll(pageable)).thenReturn(emptyPage);

        // When
        Page<CategoryResponse> result = categoryService.getAllCategories(pageable);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper, never()).toCategoryResponse(any());
    }

    @Test
    void rebuildCategoryCache_ShouldClearCaches() {
        // Given
        when(cacheManager.getCache("categories")).thenReturn(cache);
        when(cacheManager.getCache("categoryTree")).thenReturn(cache);

        // When
        categoryService.rebuildCategoryCache();

        // Then
        verify(cacheManager).getCache("categories");
        verify(cacheManager).getCache("categoryTree");
        verify(cache, times(2)).clear();
    }
} 