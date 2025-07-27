package org.com.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.productservice.dto.category.CategoryRequest;
import org.com.productservice.dto.category.CategoryResponse;
import org.com.productservice.dto.category.CategoryTreeResponse;
import org.com.productservice.dto.mapper.CategoryMapper;
import org.com.productservice.exception.AlreadyExistsException;
import org.com.productservice.exception.CategoryNotFoundException;
import org.com.productservice.exception.InvalidCategoryHierarchyException;
import org.com.productservice.model.Category;
import org.com.productservice.repository.CategoryRepository;
import org.com.productservice.repository.ProductRepository;
import org.com.productservice.service.ICategoryService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;
    private final CacheManager cacheManager;

    //--- Основные CRUD операции ---//
    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse createCategory(CategoryRequest request) {
        validateCategoryUniqueness(request.getName(), null);

        Category category = categoryMapper.toEntity(request);
        setParentCategory(request.getParentId(), category);

        Category savedCategory = categoryRepository.save(category);
        log.info("Created category: {}", savedCategory);
        return enrichWithAdditionalData(categoryMapper.toResponse(savedCategory));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#id", unless = "#result == null")
    public CategoryResponse getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    log.debug("Fetching category: {}", id);
                    return enrichWithAdditionalData(categoryMapper.toResponse(category));
                })
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "categories", key = "#id"),
            @CacheEvict(value = "categoryTree", allEntries = true)
    })
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        Category category = getCategoryEntity(id);
        validateCategoryUniqueness(request.getName(), id);

        category.setName(request.getName());
        setParentCategory(request.getParentId(), category);

        return enrichWithAdditionalData(categoryMapper.toResponse(categoryRepository.save(category)));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "categories", allEntries = true),
            @CacheEvict(value = "categoryTree", allEntries = true)
    })
    public void deleteCategory(UUID id) {
        validateCategoryDeletion(id);
        categoryRepository.deleteById(id);
        log.info("Deleted category with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(this::convertAndEnrichCategory);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "categoryTree")
    @Override
    public List<CategoryTreeResponse> getCategoryTree() {
        return categoryRepository.findRootCategories()
                .stream()
                .map(root -> new CategoryTreeResponse(
                        convertAndEnrichCategory(root),
                        getChildCategoriesRecursive(root.getId())
                ))
                .toList();
    }

    //--- Внутренние методы ---//
    private CategoryResponse convertAndEnrichCategory(Category category) {
        CategoryResponse response = categoryMapper.toResponse(category);
        return enrichWithAdditionalData(response);
    }

    private CategoryResponse enrichWithAdditionalData(CategoryResponse response) {
        response.setProductCount(productRepository.countByCategoryId(response.getId()));
        return response;
    }

    private List<CategoryTreeResponse> getChildCategoriesRecursive(UUID parentId) {
        return categoryRepository.findByParentId(parentId)
                .stream()
                .map(category -> new CategoryTreeResponse(
                        convertAndEnrichCategory(category),
                        getChildCategoriesRecursive(category.getId())
                ))
                .toList();
    }

    //--- Валидация ---//
    private void validateCategoryUniqueness(String name, UUID excludeId) {
        categoryRepository.findByNameIgnoreCase(name)
                .ifPresent(category -> {
                    if (excludeId == null || !category.getId().equals(excludeId)) {
                        throw new AlreadyExistsException("Category with name " + name + " already exists");
                    }
                });
    }

    private void setParentCategory(UUID parentId, Category category) {
        if (parentId != null) {
            Category parent = getCategoryEntity(parentId);
            validateCategoryHierarchy(category, parent);
            category.setParent(parent);
        } else {
            category.setParent(null);
        }
    }

    private void validateCategoryHierarchy(Category child, Category parent) {
        if (child.getId() != null && child.getId().equals(parent.getId())) {
            throw new InvalidCategoryHierarchyException("Category cannot be parent of itself");
        }

        // Проверка циклических зависимостей
        if (isCircularDependency(child, parent)) {
            throw new InvalidCategoryHierarchyException("Circular dependency detected");
        }
    }

    private boolean isCircularDependency(Category child, Category parent) {
        Category current = parent;
        while (current != null) {
            if (current.getId().equals(child.getId())) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    private void validateCategoryDeletion(UUID id) {
        if (categoryRepository.existsByParentId(id)) {
            throw new IllegalStateException("Cannot delete category with children");
        }
        if (productRepository.existsByCategoryId(id)) {
            throw new IllegalStateException("Cannot delete category with products");
        }
    }

    //--- Дополнительные методы ---//
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return categoryRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryEntity(UUID id) {
        return categoryRepository.findWithParentById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id " + id + " not found"));
    }

    @Override
    public void rebuildCategoryCache() {
        cacheManager.getCache("categories").clear();
        cacheManager.getCache("categoryTree").clear();
        log.info("Category caches rebuilt");
    }
}
