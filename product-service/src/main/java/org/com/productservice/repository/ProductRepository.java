package org.com.productservice.repository;

import org.com.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findBySku(String sku); // Поиск по SKU

    List<Product> findByNameContainingIgnoreCase(String name); // Поиск по названию (без учета регистра)

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") UUID categoryId); // Товары по ID категории

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice); // Фильтр по цене

    boolean existsBySku(String sku);
    List<Product> findByCategoryName(String categoryName);

    boolean existsByCategoryId(UUID id);

    int countByCategoryId(UUID id);
}
