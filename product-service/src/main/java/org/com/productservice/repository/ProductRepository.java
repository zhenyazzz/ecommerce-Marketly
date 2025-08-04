package org.com.productservice.repository;

import org.com.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {


    List<Product> findByNameContainingIgnoreCase(String name); // Поиск по названию (без учета регистра)

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId); // Товары по ID категории

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice); // Фильтр по цене
    
    List<Product> findByCategoryName(String categoryName);

    boolean existsByCategoryId(Long id);

    int countByCategoryId(Long id);
}
