package org.com.productservice.service;

import org.com.productservice.dto.category.CategoryRequest;
import org.com.productservice.dto.category.CategoryResponse;
import org.com.productservice.dto.category.CategoryTreeResponse;
import org.com.productservice.model.Category;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ICategoryService {

    // Основные CRUD операции
    @Transactional
    CategoryResponse createCategory(CategoryRequest request);

    @Transactional(readOnly = true)
    CategoryResponse getCategoryById(UUID id);

    @Transactional
    CategoryResponse updateCategory(UUID id, CategoryRequest request);

    @Transactional
    void deleteCategory(UUID id);

    // Методы с пагинацией
    @Transactional(readOnly = true)
    Page<CategoryResponse> getAllCategories(Pageable pageable);

    @Transactional(readOnly = true)
    @Cacheable(value = "categoryTree")
    List<CategoryTreeResponse> getCategoryTree();

    // Вспомогательные методы
    @Transactional(readOnly = true)
    boolean existsById(UUID id);

    @Transactional(readOnly = true)
    boolean existsByName(String name);

    Category getCategoryEntity(UUID id);

    void rebuildCategoryCache();
}
