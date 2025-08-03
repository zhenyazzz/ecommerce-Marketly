package org.com.productservice.service;

import lombok.RequiredArgsConstructor;
import org.com.productservice.mapper.ProductMapper;
import org.com.productservice.dto.product.ProductDto;
import org.com.productservice.dto.product.ProductRequest;
import org.com.productservice.dto.product.ProductResponse;
import org.com.productservice.exception.AlreadyExistsException;
import org.com.productservice.exception.ProductNotFoundException;
import org.com.productservice.model.Product;
import org.com.productservice.repository.CategoryRepository;
import org.com.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service

public class ProductService{


    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final CategoryService categoryService;


    @Autowired
    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          ProductMapper productMapper,
                          CategoryService categoryService)
    {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
        this.categoryService = categoryService;
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {

        Product product = productMapper.toProduct(request);
        product.setCategory(categoryRepository.getCategoryById(request.getCategoryId()));
        Product savedProduct = productRepository.save(product);
        return productMapper.toProductResponse(savedProduct);
    }



    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return productMapper.toProductResponse(product);
    }



    @Transactional(readOnly = true)
    public ProductDto getProductByIdForCart(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return productMapper.toProductDto(product);
    }



    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toProductResponse);
    }



    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        productMapper.updateProductFromProductRequest(request, product);
        product.setCategory(categoryRepository.getCategoryById(request.getCategoryId()));

        return productMapper.toProductResponse(productRepository.save(product));
    }



    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }



    public List<ProductResponse> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream().map(productMapper::toProductResponse).toList();
    }



    public List<ProductResponse> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId)
                .stream().map(productMapper::toProductResponse).toList();
    }



    public List<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceRange(minPrice,maxPrice)
                .stream().map(productMapper::toProductResponse).toList();
    }



    public List<ProductResponse> getProductsByCategoryName(String categoryName) {
        return productRepository.findByCategoryName(categoryName)
                .stream().map(productMapper::toProductResponse).toList();
    }



    public void updateProductStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStock() + ", Requested: " + quantity);
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}
