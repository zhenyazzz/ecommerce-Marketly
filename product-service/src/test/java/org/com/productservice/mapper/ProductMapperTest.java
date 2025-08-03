package org.com.productservice.mapper;

import org.com.productservice.dto.product.ProductDto;
import org.com.productservice.dto.product.ProductRequest;
import org.com.productservice.dto.product.ProductResponse;
import org.com.productservice.model.Category;
import org.com.productservice.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = {"/mapper/product/clear.sql", "/mapper/product/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    @Test
    void toProduct_ShouldMapProductRequestToProduct() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stock(10)
                .categoryId(1L)
                .mainImage("main.jpg")
                .images(Arrays.asList("img1.jpg", "img2.jpg"))
                .active(true)
                .build();

        // When
        Product product = productMapper.toProduct(request);

        // Then
        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo("Test Product");
        assertThat(product.getDescription()).isEqualTo("Test Description");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("99.99"));
        assertThat(product.getStock()).isEqualTo(10);
        assertThat(product.getCategory().getId()).isEqualTo(1L); // Проверка category.id
        assertThat(product.getMainImage()).isEqualTo("main.jpg");
        assertThat(product.getImages()).containsExactly("img1.jpg", "img2.jpg");
        assertThat(product.isActive()).isTrue();
    }

    @Test
    void toProductResponse_ShouldMapProductToProductResponse() {
        // Given
        Category category = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stock(10)
                .category(category)
                .mainImage("main.jpg")
                .images(Arrays.asList("img1.jpg", "img2.jpg"))
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        ProductResponse response = productMapper.toProductResponse(product);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Test Product");
        assertThat(response.getDescription()).isEqualTo("Test Description");
        assertThat(response.getPrice()).isEqualTo(new BigDecimal("99.99"));
        assertThat(response.getStock()).isEqualTo(10);
        assertThat(response.getCategoryId()).isEqualTo(1L);
        assertThat(response.getMainImage()).isEqualTo("main.jpg");
        assertThat(response.getImages()).containsExactly("img1.jpg", "img2.jpg");
        assertThat(response.isActive()).isTrue();
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();
    }

    @Test
    void toProductDto_ShouldMapProductToProductDto() {
        // Given
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .stock(10)
                .build();

        // When
        ProductDto dto = productMapper.toProductDto(product);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test Product");
        assertThat(dto.getPrice()).isEqualTo(new BigDecimal("99.99"));
        assertThat(dto.getStock()).isEqualTo(10);
    }

    @Test
    void updateProductFromProductRequest_ShouldUpdateProductFields() {
        // Given
        Product product = Product.builder()
                .id(1L)
                .name("Old Name")
                .description("Old Description")
                .price(new BigDecimal("50.00"))
                .stock(5)
                .mainImage("old.jpg")
                .images(Arrays.asList("old1.jpg"))
                .active(false)
                .build();

        ProductRequest request = ProductRequest.builder()
                .name("New Name")
                .description("New Description")
                .price(new BigDecimal("100.00"))
                .stock(15)
                .mainImage("new.jpg")
                .images(Arrays.asList("new1.jpg", "new2.jpg"))
                .active(true)
                .build();

        // When
        productMapper.updateProductFromProductRequest(request, product);

        // Then
        assertThat(product.getName()).isEqualTo("New Name");
        assertThat(product.getDescription()).isEqualTo("New Description");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("100.00"));
        assertThat(product.getStock()).isEqualTo(15);
        assertThat(product.getMainImage()).isEqualTo("new.jpg");
        assertThat(product.getImages()).containsExactly("new1.jpg", "new2.jpg");
        assertThat(product.isActive()).isTrue();
        assertThat(product.getId()).isEqualTo(1L); // ID не должен измениться
    }

    @Test
    void mapListOfProducts_ShouldWorkCorrectly() {
        // Given
        Category category = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();

        List<Product> products = Arrays.asList(
                Product.builder()
                        .id(1L)
                        .name("Product 1")
                        .price(new BigDecimal("99.99"))
                        .stock(10)
                        .category(category)
                        .build(),
                Product.builder()
                        .id(2L)
                        .name("Product 2")
                        .price(new BigDecimal("199.99"))
                        .stock(20)
                        .category(category)
                        .build()
        );

        // When
        List<ProductResponse> responses = products.stream()
                .map(productMapper::toProductResponse)
                .toList();
        List<ProductDto> dtos = products.stream()
                .map(productMapper::toProductDto)
                .toList();

        // Then
        assertThat(responses).hasSize(2);
        assertThat(dtos).hasSize(2);
        assertThat(responses.get(0).getName()).isEqualTo("Product 1");
        assertThat(responses.get(1).getName()).isEqualTo("Product 2");
        assertThat(dtos.get(0).getName()).isEqualTo("Product 1");
        assertThat(dtos.get(1).getName()).isEqualTo("Product 2");
    }
}