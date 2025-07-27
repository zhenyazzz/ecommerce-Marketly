package org.com.productservice.repository;

import org.com.productservice.model.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByParentId(UUID parentId);

    boolean existsByNameIgnoreCase(String name);

    Optional<Category> findByNameIgnoreCase(String name);

    @EntityGraph(attributePaths = {"children"}) // Жадная загрузка дочерних категорий 1-го уровня
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    List<Category> findRootCategories();
    boolean existsByParentId(UUID parentId);
    @EntityGraph(attributePaths = {"parent"})
    Optional<Category> findWithParentById(UUID id);
}
