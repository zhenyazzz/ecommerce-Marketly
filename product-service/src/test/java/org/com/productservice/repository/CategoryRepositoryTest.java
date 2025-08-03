package org.com.productservice.repository;

import org.com.productservice.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Category electronics;
    private Category clothing;
    private Category books;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();

        electronics = Category.builder()
                .name("Electronics")
                .build();

        clothing = Category.builder()
                .name("Clothing")
                .build();

        books = Category.builder()
                .name("Books")
                .build();
    }

    @Test
    void save_ShouldSaveCategory() {
        // When
        Category savedCategory = categoryRepository.save(electronics);

        // Then
        assertNotNull(savedCategory.getId());
        assertEquals("Electronics", savedCategory.getName());
        assertTrue(categoryRepository.existsById(savedCategory.getId()));
    }

    @Test
    void findById_ShouldReturnCategory() {
        // Given
        Category savedCategory = categoryRepository.save(electronics);

        // When
        Optional<Category> foundCategory = categoryRepository.findById(savedCategory.getId());

        // Then
        assertTrue(foundCategory.isPresent());
        assertEquals("Electronics", foundCategory.get().getName());
        assertEquals(savedCategory.getId(), foundCategory.get().getId());
    }

    @Test
    void findById_WithNonExistentId_ShouldReturnEmpty() {
        // When
        Optional<Category> foundCategory = categoryRepository.findById(999L);

        // Then
        assertFalse(foundCategory.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllCategories() {
        // Given
        categoryRepository.save(electronics);
        categoryRepository.save(clothing);
        categoryRepository.save(books);

        // When
        List<Category> categories = categoryRepository.findAll();

        // Then
        assertEquals(3, categories.size());
        assertTrue(categories.stream().anyMatch(c -> c.getName().equals("Electronics")));
        assertTrue(categories.stream().anyMatch(c -> c.getName().equals("Clothing")));
        assertTrue(categories.stream().anyMatch(c -> c.getName().equals("Books")));
    }

    @Test
    void deleteById_ShouldDeleteCategory() {
        // Given
        Category savedCategory = categoryRepository.save(electronics);

        // When
        categoryRepository.deleteById(savedCategory.getId());

        // Then
        assertFalse(categoryRepository.existsById(savedCategory.getId()));
    }

    @Test
    void existsByNameIgnoreCase_WithExistingName_ShouldReturnTrue() {
        // Given
        categoryRepository.save(electronics);

        // When
        boolean exists = categoryRepository.existsByNameIgnoreCase("Electronics");

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByNameIgnoreCase_WithDifferentCase_ShouldReturnTrue() {
        // Given
        categoryRepository.save(electronics);

        // When
        boolean exists = categoryRepository.existsByNameIgnoreCase("electronics");

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByNameIgnoreCase_WithNonExistentName_ShouldReturnFalse() {
        // When
        boolean exists = categoryRepository.existsByNameIgnoreCase("NonExistent");

        // Then
        assertFalse(exists);
    }

    @Test
    void findByNameIgnoreCase_WithExistingName_ShouldReturnCategory() {
        // Given
        Category savedCategory = categoryRepository.save(electronics);

        // When
        Optional<Category> foundCategory = categoryRepository.findByNameIgnoreCase("Electronics");

        // Then
        assertTrue(foundCategory.isPresent());
        assertEquals(savedCategory.getId(), foundCategory.get().getId());
        assertEquals("Electronics", foundCategory.get().getName());
    }

    @Test
    void findByNameIgnoreCase_WithDifferentCase_ShouldReturnCategory() {
        // Given
        Category savedCategory = categoryRepository.save(electronics);

        // When
        Optional<Category> foundCategory = categoryRepository.findByNameIgnoreCase("electronics");

        // Then
        assertTrue(foundCategory.isPresent());
        assertEquals(savedCategory.getId(), foundCategory.get().getId());
        assertEquals("Electronics", foundCategory.get().getName());
    }

    @Test
    void findByNameIgnoreCase_WithNonExistentName_ShouldReturnEmpty() {
        // When
        Optional<Category> foundCategory = categoryRepository.findByNameIgnoreCase("NonExistent");

        // Then
        assertFalse(foundCategory.isPresent());
    }

    @Test
    void getCategoryById_ShouldReturnCategory() {
        // Given
        Category savedCategory = categoryRepository.save(electronics);

        // When
        Category foundCategory = categoryRepository.getCategoryById(savedCategory.getId());

        // Then
        assertNotNull(foundCategory);
        assertEquals(savedCategory.getId(), foundCategory.getId());
        assertEquals("Electronics", foundCategory.getName());
    }

    @Test
    void getCategoryById_WithNonExistentId_ShouldReturnNull() {
        // When
        Category foundCategory = categoryRepository.getCategoryById(999L);

        // Then
        assertNull(foundCategory);
    }

    @Test
    void count_ShouldReturnCorrectCount() {
        // Given
        categoryRepository.save(electronics);
        categoryRepository.save(clothing);

        // When
        long count = categoryRepository.count();

        // Then
        assertEquals(2, count);
    }

    @Test
    void existsById_WithExistingId_ShouldReturnTrue() {
        // Given
        Category savedCategory = categoryRepository.save(electronics);

        // When
        boolean exists = categoryRepository.existsById(savedCategory.getId());

        // Then
        assertTrue(exists);
    }

    @Test
    void existsById_WithNonExistentId_ShouldReturnFalse() {
        // When
        boolean exists = categoryRepository.existsById(999L);

        // Then
        assertFalse(exists);
    }
} 