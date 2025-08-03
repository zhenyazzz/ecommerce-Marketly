package org.com.productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.productservice.dto.category.CategoryRequest;
import org.com.productservice.dto.category.CategoryResponse;

import org.com.productservice.mapper.CategoryMapper;
import org.com.productservice.exception.AlreadyExistsException;
import org.com.productservice.exception.CategoryNotFoundException;
import org.com.productservice.exception.InvalidCategoryHierarchyException;
import org.com.productservice.model.Category;
import org.com.productservice.repository.CategoryRepository;
import org.com.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
public class CategoryService{

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;
    private final CacheManager cacheManager;


    @Autowired
    public CategoryService(CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           CategoryMapper categoryMapper,
                           CacheManager cacheManager)
    {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.categoryMapper = categoryMapper;
        this.cacheManager = cacheManager;
    }

    //--- Основные CRUD операции ---//
  
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        validateCategoryUniqueness(request.getName(), null);
        Category category = categoryMapper.toCategory(request);
        Category savedCategory = categoryRepository.save(category);
        log.info("Created category: {}", savedCategory);
        return categoryMapper.toCategoryResponse(savedCategory);
    }



    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#id", unless = "#result == null")
    public CategoryResponse getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    log.debug("Fetching category: {}", id);
                    return categoryMapper.toCategoryResponse(category);
                })
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }



    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "categories", key = "#id"),
            @CacheEvict(value = "categoryTree", allEntries = true)
    })
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.getCategoryById(id);
        validateCategoryUniqueness(request.getName(), id);
        category.setName(request.getName());
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }



    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "categories", allEntries = true),
            @CacheEvict(value = "categoryTree", allEntries = true)
    })
    public void deleteCategory(Long id) {
        validateCategoryDeletion(id);
        categoryRepository.deleteById(id);
        log.info("Deleted category with ID: {}", id);
    }



    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategories(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(this::convertAndEnrichCategory);
    }



    //--- Валидация ---//
    private void validateCategoryUniqueness(String name, Long excludeId) {
        categoryRepository.findByNameIgnoreCase(name)
                .ifPresent(category -> {
                    if (excludeId == null || !category.getId().equals(excludeId)) {
                        throw new AlreadyExistsException("Category with name " + name + " already exists");
                    }
                });
    }



    private void validateCategoryDeletion(Long id) {

        if (productRepository.existsByCategoryId(id)) {
            throw new IllegalStateException("Cannot delete category with products");
        }
    }



    //--- Дополнительные методы ---//

    private CategoryResponse convertAndEnrichCategory(Category category) {
        return categoryMapper.toCategoryResponse(category);
    }



    public void rebuildCategoryCache() {
        cacheManager.getCache("categories").clear();
        cacheManager.getCache("categoryTree").clear();
        log.info("Category caches rebuilt");
    }
}
