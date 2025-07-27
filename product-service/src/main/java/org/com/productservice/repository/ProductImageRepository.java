package org.com.productservice.repository;

import org.com.productservice.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {
    List<ProductImage> findByProductId(UUID productId); // Все изображения товара

    Optional<ProductImage> findByProductIdAndIsMainTrue(UUID productId);

    boolean existsByProductIdAndIsMainTrue(UUID id);
}
