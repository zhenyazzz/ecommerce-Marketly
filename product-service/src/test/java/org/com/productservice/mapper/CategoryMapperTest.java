package org.com.productservice.mapper;

import org.com.productservice.dto.category.CategoryRequest;
import org.com.productservice.dto.category.CategoryResponse;
import org.com.productservice.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest

class CategoryMapperTest {

    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    void toCategory_ShouldMapRequestToCategory() {
        // Given
        CategoryRequest request = CategoryRequest.builder()
                .name("Electronics")
                .build();

        // When
        Category result = categoryMapper.toCategory(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Electronics");
        // ID should be null for new entities
        assertThat(result.getId()).isNull();
    }

    @Test
    void toCategory_WithNullRequest_ShouldReturnNull() {
        // When
        Category result = categoryMapper.toCategory(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toCategory_WithEmptyName_ShouldMapCorrectly() {
        // Given
        CategoryRequest emptyNameRequest = CategoryRequest.builder()
                .name("")
                .build();

        // When
        Category result = categoryMapper.toCategory(emptyNameRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("");
    }

    @Test
    void toCategory_WithNullName_ShouldMapCorrectly() {
        // Given
        CategoryRequest nullNameRequest = CategoryRequest.builder()
                .name(null)
                .build();

        // When
        Category result = categoryMapper.toCategory(nullNameRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isNull();
    }

    @Test
    void toCategoryResponse_ShouldMapCategoryToResponse() {
        // Given
        Category category = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();

        // When
        CategoryResponse result = categoryMapper.toCategoryResponse(category);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Electronics");
    }

    @Test
    void toCategoryResponse_WithNullCategory_ShouldReturnNull() {
        // When
        CategoryResponse result = categoryMapper.toCategoryResponse(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toCategoryResponse_WithNullId_ShouldMapCorrectly() {
        // Given
        Category categoryWithNullId = Category.builder()
                .id(null)
                .name("Electronics")
                .build();

        // When
        CategoryResponse result = categoryMapper.toCategoryResponse(categoryWithNullId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isEqualTo("Electronics");
    }

    @Test
    void toCategoryResponse_WithNullName_ShouldMapCorrectly() {
        // Given
        Category categoryWithNullName = Category.builder()
                .id(1L)
                .name(null)
                .build();

        // When
        CategoryResponse result = categoryMapper.toCategoryResponse(categoryWithNullName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isNull();
    }

    @Test
    void toCategoryResponse_WithAllNullFields_ShouldMapCorrectly() {
        // Given
        Category categoryWithNullFields = Category.builder()
                .id(null)
                .name(null)
                .build();

        // When
        CategoryResponse result = categoryMapper.toCategoryResponse(categoryWithNullFields);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isNull();
    }

    @Test
    void toCategory_WithSpecialCharacters_ShouldMapCorrectly() {
        // Given
        CategoryRequest specialCharRequest = CategoryRequest.builder()
                .name("Electronics & Gadgets")
                .build();

        // When
        Category result = categoryMapper.toCategory(specialCharRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Electronics & Gadgets");
    }

    @Test
    void toCategory_WithLongName_ShouldMapCorrectly() {
        // Given
        String longName = "A".repeat(100);
        CategoryRequest longNameRequest = CategoryRequest.builder()
                .name(longName)
                .build();

        // When
        Category result = categoryMapper.toCategory(longNameRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(longName);
    }

    @Test
    void toCategoryResponse_WithLongName_ShouldMapCorrectly() {
        // Given
        String longName = "A".repeat(100);
        Category categoryWithLongName = Category.builder()
                .id(1L)
                .name(longName)
                .build();

        // When
        CategoryResponse result = categoryMapper.toCategoryResponse(categoryWithLongName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo(longName);
    }
} 