package org.com.productservice.service;

import org.com.productservice.dto.product.ProductDto;
import org.com.productservice.dto.product.ProductRequest;
import org.com.productservice.dto.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse getProductById(UUID id);

    @Transactional(readOnly = true)
    ProductDto getProductByIdForCart(UUID id);

    Page<ProductResponse> getAllProducts(Pageable pageable);
    ProductResponse updateProduct(UUID id, ProductRequest request);
    void deleteProduct(UUID id);
    boolean existsById(UUID id);

    // Дополнительные методы поиска
    ProductResponse getProductBySku(String sku);
    List<ProductResponse> searchProductsByName(String name);
    List<ProductResponse> getProductsByCategoryId(UUID categoryId);
    List<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    List<ProductResponse> getProductsByCategoryName(String categoryName);

    boolean existsBySku(String sku);

    void updateProductStock(UUID productId, int quantity);
}
