package org.com.productservice.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.com.productservice.dto.product.ProductRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validProductRequest_ShouldPassValidation() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Valid Product")
                .description("Valid description")
                .price(new BigDecimal("99.99"))
                .stock(10)
                .categoryId(1L)
                .active(true)
                .build();

        // When
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void productRequestWithEmptyName_ShouldFailValidation() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("")
                .price(new BigDecimal("99.99"))
                .stock(10)
                .categoryId(1L)
                .build();

        // When
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(2); // @NotBlank + @Size
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Name is required"));
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Name must be 3-100 characters"));
    }

    @Test
    void productRequestWithNullName_ShouldFailValidation() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name(null)
                .price(new BigDecimal("99.99"))
                .stock(10)
                .categoryId(1L)
                .build();

        // When
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(2); // @NotBlank + @Size
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Name is required"));
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Name must be 3-100 characters"));
    }

    @Test
    void productRequestWithShortName_ShouldFailValidation() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("AB")
                .price(new BigDecimal("99.99"))
                .stock(10)
                .categoryId(1L)
                .build();

        // When
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Name must be 3-100 characters");
    }

    @Test
    void productRequestWithLongName_ShouldFailValidation() {
        // Given
        String longName = "A".repeat(101);
        ProductRequest request = ProductRequest.builder()
                .name(longName)
                .price(new BigDecimal("99.99"))
                .stock(10)
                .categoryId(1L)
                .build();

        // When
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Name must be 3-100 characters");
    }

    @Test
    void productRequestWithLongDescription_ShouldFailValidation() {
        // Given
        String longDescription = "A".repeat(1001);
        ProductRequest request = ProductRequest.builder()
                .name("Valid Product")
                .description(longDescription)
                .price(new BigDecimal("99.99"))
                .stock(10)
                .categoryId(1L)
                .build();

        // When
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Description too long");
    }

    @Test
    void productRequestWithNullPrice_ShouldFailValidation() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Valid Product")
                .price(null)
                .stock(10)
                .categoryId(1L)
                .build();

        // When
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Price is required");
    }

    @Test
    void productRequestWithNegativePrice_ShouldFailValidation() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Valid Product")
                .price(new BigDecimal("-10.00"))
                .stock(10)
                .categoryId(1L)
                .build();

        // When
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Price must be positive");
    }

    @Test
    void productRequestWithZeroPrice_ShouldPassValidation() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Free Product")
                .price(BigDecimal.ZERO)
                .stock(10)
                .categoryId(1L)
                .build();

        // When
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void productRequestWithNullStock_ShouldFailValidation() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Valid Product")
                .price(new BigDecimal("99.99"))
                .stock(null)
                .categoryId(1L)
                .build();

        // When
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Stock is required");
    }

    @Test
    void productRequestWithNegativeStock_ShouldFailValidation() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Valid Product")
                .price(new BigDecimal("99.99"))
                .stock(-5)
                .categoryId(1L)
                .build();

        // When
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Stock cannot be negative");
    }

    @Test
    void productRequestWithZeroStock_ShouldPassValidation() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Valid Product")
                .price(new BigDecimal("99.99"))
                .stock(0)
                .categoryId(1L)
                .build();

        // When
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void productRequestWithNullCategoryId_ShouldFailValidation() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Valid Product")
                .price(new BigDecimal("99.99"))
                .stock(10)
                .categoryId(null)
                .build();

        // When
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Category ID is required");
    }

    @Test
    void productRequestWithMultipleViolations_ShouldReturnAllViolations() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("") // Empty name - 2 violations (@NotBlank + @Size)
                .price(new BigDecimal("-10.00")) // Negative price - 1 violation
                .stock(-5) // Negative stock - 1 violation
                .categoryId(null) // Null category ID - 1 violation
                .build();

        // When
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(5); // 2 + 1 + 1 + 1 = 5
    }

    @Test
    void productRequestWithExactMinNameLength_ShouldPassValidation() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("ABC") // Exactly 3 characters
                .price(new BigDecimal("99.99"))
                .stock(10)
                .categoryId(1L)
                .build();

        // When
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void productRequestWithExactMaxNameLength_ShouldPassValidation() {
        // Given
        String maxName = "A".repeat(100); // Exactly 100 characters
        ProductRequest request = ProductRequest.builder()
                .name(maxName)
                .price(new BigDecimal("99.99"))
                .stock(10)
                .categoryId(1L)
                .build();

        // When
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void productRequestWithExactMaxDescriptionLength_ShouldPassValidation() {
        // Given
        String maxDescription = "A".repeat(1000); // Exactly 1000 characters
        ProductRequest request = ProductRequest.builder()
                .name("Valid Product")
                .description(maxDescription)
                .price(new BigDecimal("99.99"))
                .stock(10)
                .categoryId(1L)
                .build();

        // When
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void productRequestWithVeryHighPrice_ShouldPassValidation() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Expensive Product")
                .price(new BigDecimal("999999.99"))
                .stock(10)
                .categoryId(1L)
                .build();

        // When
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void productRequestWithVeryHighStock_ShouldPassValidation() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("High Stock Product")
                .price(new BigDecimal("99.99"))
                .stock(Integer.MAX_VALUE)
                .categoryId(1L)
                .build();

        // When
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }
} 