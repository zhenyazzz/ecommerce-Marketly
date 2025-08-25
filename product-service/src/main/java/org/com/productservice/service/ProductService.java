package org.com.productservice.service;

import org.com.productservice.mapper.ProductMapper;
import org.com.productservice.dto.product.ProductDto;
import org.com.productservice.dto.product.ProductRequest;
import org.com.productservice.dto.product.ProductResponse;
import org.com.productservice.exception.ProductNotFoundException;
import org.com.productservice.model.Product;
import org.com.productservice.repository.jpa.CategoryJpaRepository;
import org.com.productservice.repository.jpa.ProductJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Service

public class ProductService{


    private final ProductJpaRepository productJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final ProductMapper productMapper;
    private final CategoryService categoryService;


    @Autowired
    public ProductService(ProductJpaRepository productJpaRepository,
                          CategoryJpaRepository categoryJpaRepository,
                          ProductMapper productMapper,
                          CategoryService categoryService)
    {
        this.productJpaRepository = productJpaRepository;
        this.categoryJpaRepository = categoryJpaRepository;
        this.productMapper = productMapper;
        this.categoryService = categoryService;
    }

    // ✅ ИНВАЛИДАЦИЯ КЭША: При создании нового товара
    @Caching(evict = {
            @CacheEvict(value = "search", allEntries = true),
            @CacheEvict(value = {"category_products", "category_name_products"}, allEntries = true)
    })
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = productMapper.toProduct(request);
        Product savedProduct = productJpaRepository.save(product);
        return productMapper.toProductResponse(savedProduct);
    }

    // ✅ ГОРЯЧИЕ ДАННЫЕ: Детали товаров (чаще всего просматривают)
    @Cacheable(value = "products", key = "#id", unless = "#result == null")
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product = productJpaRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return productMapper.toProductResponse(product);
    }



    /*// ✅ ГОРЯЧИЕ ДАННЫЕ: Для корзины (быстрый доступ)
    @Cacheable(value = "products_cart", key = "#id", unless = "#result == null")
    @Transactional(readOnly = true)
    public ProductDto getProductByIdForCart(UUID id) {
        Product product = productJpaRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return productMapper.toProductDto(product);
    }*/



    // ✅ ГОРЯЧИЕ ДАННЫЕ: Для корзины (быстрый доступ)
    @Cacheable(value = "products_cart", key = "#id", unless = "#result == null")
    @Transactional(readOnly = true)
    public ProductResponse getProductByIdForCart(UUID id) {
        Product product = productJpaRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return productMapper.toProductResponse(product);
    }


    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productJpaRepository.findAll(pageable).map(productMapper::toProductResponse);
    }




    // ✅ ИНВАЛИДАЦИЯ КЭША: При обновлении товара
    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "products_cart", key = "#id"),
            @CacheEvict(value = "search", allEntries = true), // Очищаем поиск
            @CacheEvict(value = {"category_products", "category_name_products"}, allEntries = true)
    })
    @Transactional
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        Product product = productJpaRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        productMapper.updateProductFromProductRequest(request, product);
        return productMapper.toProductResponse(productJpaRepository.save(product));
    }


    // ✅ ИНВАЛИДАЦИЯ КЭША: При изменении остатков
    @CacheEvict(value = {"products", "products_cart"}, key = "#productId")
    @Transactional
    public void updateProductStock(UUID productId, int quantity) {
        Product product = productJpaRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

       /* if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStock() + ", Requested: " + quantity);
        }*/

        product.setStock(quantity);
        productJpaRepository.save(product);
    }


    // ✅ ИНВАЛИДАЦИЯ КЭША: При удалении товара
    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "products_cart", key = "#id"),
            @CacheEvict(value = "search", allEntries = true),
            @CacheEvict(value = {"category_products", "category_name_products"}, allEntries = true)
    })
    @Transactional
    public void deleteProduct(UUID id) {
        if (!productJpaRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productJpaRepository.deleteById(id);
    }





    // ✅ ПОПУЛЯРНЫЕ ПОИСКИ: Только первая страница
    @Cacheable(value = "search", key = "#name + ':page0'", condition = "#name.length() >= 3")
    public List<ProductResponse> searchProductsByName(String name) {
        return productJpaRepository.findByNameContainingIgnoreCase(name)
                .stream().map(productMapper::toProductResponse).toList();
    }


    // ✅ ПОПУЛЯРНЫЕ КАТЕГОРИИ: Часто просматриваемые
    @Cacheable(value = "category_products", key = "#categoryId", unless = "#result.isEmpty()")
    public List<ProductResponse> getProductsByCategoryId(Long categoryId) {
        return productJpaRepository.findByCategoryId(categoryId)
                .stream().map(productMapper::toProductResponse).toList();
    }




    public List<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productJpaRepository.findByPriceRange(minPrice,maxPrice)
                .stream().map(productMapper::toProductResponse).toList();
    }



    // ✅ ПОПУЛЯРНЫЕ КАТЕГОРИИ: По имени категории
    @Cacheable(value = "category_name_products", key = "#categoryName", unless = "#result.isEmpty()")
    public List<ProductResponse> getProductsByCategoryName(String categoryName) {
        return productJpaRepository.findByCategoryName(categoryName)
                .stream().map(productMapper::toProductResponse).toList();
    }







}
