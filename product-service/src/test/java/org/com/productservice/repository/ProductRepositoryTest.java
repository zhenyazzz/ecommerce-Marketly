package org.com.productservice.repository;

import org.com.productservice.model.Category;
import org.com.productservice.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;
    private Product testProduct1;
    private Product testProduct2;

    @BeforeEach
    void setUp() {
        // Создаем тестовую категорию
        testCategory = Category.builder()
                .name("Electronics")
                .build();
        testCategory = entityManager.persistAndFlush(testCategory);

        // Создаем тестовые продукты
        testProduct1 = Product.builder()
                .name("iPhone 15")
                .description("Latest iPhone model")
                .price(new BigDecimal("999.99"))
                .stock(10)
                .category(testCategory)
                .active(true)
                .build();

        testProduct2 = Product.builder()
                .name("Samsung Galaxy")
                .description("Android smartphone")
                .price(new BigDecimal("799.99"))
                .stock(5)
                .category(testCategory)
                .active(true)
                .build();

        entityManager.persistAndFlush(testProduct1);
        entityManager.persistAndFlush(testProduct2);
    }

    @Test
    void findById_ShouldReturnProduct_WhenProductExists() {
        // When
        Optional<Product> found = productRepository.findById(testProduct1.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("iPhone 15");
        assertThat(found.get().getPrice()).isEqualTo(new BigDecimal("999.99"));
    }

    @Test
    void findById_ShouldReturnEmpty_WhenProductDoesNotExist() {
        // When
        Optional<Product> found = productRepository.findById(999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void save_ShouldPersistProduct() {
        // Given
        Product newProduct = Product.builder()
                .name("New Product")
                .description("A new product")
                .price(new BigDecimal("299.99"))
                .stock(15)
                .category(testCategory)
                .active(true)
                .build();

        // When
        Product saved = productRepository.save(newProduct);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("New Product");
        
        // Проверяем, что продукт действительно сохранен в БД
        Product found = entityManager.find(Product.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("New Product");
    }

    @Test
    void deleteById_ShouldRemoveProduct() {
        // Given
        Long productId = testProduct1.getId();

        // When
        productRepository.deleteById(productId);

        // Then
        Product found = entityManager.find(Product.class, productId);
        assertThat(found).isNull();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenProductExists() {
        // When
        boolean exists = productRepository.existsById(testProduct1.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenProductDoesNotExist() {
        // When
        boolean exists = productRepository.existsById(999L);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void findByNameContainingIgnoreCase_ShouldReturnMatchingProducts() {
        // When
        List<Product> found = productRepository.findByNameContainingIgnoreCase("iPhone");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("iPhone 15");
    }

    @Test
    void findByNameContainingIgnoreCase_ShouldReturnEmpty_WhenNoMatches() {
        // When
        List<Product> found = productRepository.findByNameContainingIgnoreCase("NonExistent");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_ShouldBeCaseInsensitive() {
        // When
        List<Product> found = productRepository.findByNameContainingIgnoreCase("iphone");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("iPhone 15");
    }

    @Test
    void findByCategoryId_ShouldReturnProducts() {
        // When
        List<Product> found = productRepository.findByCategoryId(testCategory.getId());

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting("name")
                .containsExactlyInAnyOrder("iPhone 15", "Samsung Galaxy");
    }

    @Test
    void findByCategoryId_ShouldReturnEmpty_WhenCategoryDoesNotExist() {
        // When
        List<Product> found = productRepository.findByCategoryId(999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findByPriceRange_ShouldReturnProductsInRange() {
        // Given
        BigDecimal minPrice = new BigDecimal("800");
        BigDecimal maxPrice = new BigDecimal("1000");

        // When
        List<Product> found = productRepository.findByPriceRange(minPrice, maxPrice);

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("iPhone 15");
    }

    @Test
    void findByPriceRange_ShouldReturnEmpty_WhenNoProductsInRange() {
        // Given
        BigDecimal minPrice = new BigDecimal("2000");
        BigDecimal maxPrice = new BigDecimal("3000");

        // When
        List<Product> found = productRepository.findByPriceRange(minPrice, maxPrice);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findByCategoryName_ShouldReturnProducts() {
        // When
        List<Product> found = productRepository.findByCategoryName("Electronics");

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting("name")
                .containsExactlyInAnyOrder("iPhone 15", "Samsung Galaxy");
    }

    @Test
    void findByCategoryName_ShouldReturnEmpty_WhenCategoryDoesNotExist() {
        // When
        List<Product> found = productRepository.findByCategoryName("NonExistent");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllProducts() {
        // When
        List<Product> allProducts = productRepository.findAll();

        // Then
        assertThat(allProducts).hasSize(2);
        assertThat(allProducts).extracting("name")
                .containsExactlyInAnyOrder("iPhone 15", "Samsung Galaxy");
    }
} 