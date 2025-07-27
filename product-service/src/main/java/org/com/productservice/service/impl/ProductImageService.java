package org.com.productservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.productservice.dto.mapper.ProductImageMapper;
import org.com.productservice.dto.productImage.ProductImageRequest;
import org.com.productservice.dto.productImage.ProductImageResponse;
import org.com.productservice.exception.ProductNotFoundException;
import org.com.productservice.model.Product;
import org.com.productservice.model.ProductImage;
import org.com.productservice.repository.ProductImageRepository;
import org.com.productservice.repository.ProductRepository;
import org.com.productservice.service.IProductImageService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableCaching
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final CacheManager cacheManager;
    private final ProductImageMapper productImageMapper;

    @Transactional
    @CacheEvict(value = "productImages", key = "#productId")
    public ProductImageResponse addImageToProduct(UUID productId, ProductImageRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        if (request.getIsMain() && productImageRepository.existsByProductIdAndIsMainTrue(productId)) {
            throw new IllegalStateException("Product already has a main image");
        }

        ProductImage image = productImageMapper.toEntity(request);
        image.setProduct(product);

        return productImageMapper.toResponse(productImageRepository.save(image));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "productImages", key = "#productId")
    public List<ProductImageResponse> getImagesByProductId(UUID productId) {
        return productImageRepository.findByProductId(productId)
                .stream()
                .map(productImageMapper::toResponse)
                .toList();
    }

    @Transactional
    @CacheEvict(value = "productImages", allEntries = true)
    public void deleteImage(UUID imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found with id: " + imageId));
        productImageRepository.delete(image);
        log.info("Deleted product image with id: {}", imageId);
    }

    @Transactional
    @CacheEvict(value = "productImages", key = "#image.product.id")
    public ProductImageResponse updateImage(UUID imageId, ProductImageRequest request) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found with id: " + imageId));

        if (request.getIsMain() && productImageRepository.existsByProductIdAndIsMainTrue(image.getProduct().getId())) {
            throw new IllegalStateException("Product already has a main image");
        }

        image.setUrl(request.getUrl());
        image.setIsMain(request.getIsMain());
        return productImageMapper.toResponse(productImageRepository.save(image));
    }
}


