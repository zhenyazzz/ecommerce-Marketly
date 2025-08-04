package org.com.productservice.service;

import org.com.productservice.dto.product.ProductDto;
import org.com.productservice.dto.product.ProductRequest;
import org.com.productservice.dto.product.ProductResponse;
import org.com.productservice.exception.ProductNotFoundException;
import org.com.productservice.mapper.ProductMapper;
import org.com.productservice.model.Category;
import org.com.productservice.model.Product;
import org.com.productservice.repository.CategoryRepository;
import org.com.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductRequest testProductRequest;
    private ProductResponse testProductResponse;
    private ProductDto testProductDto;
    private Category testCategory;
    private UUID productId;
    
    
    
    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                //.id(1)
                .name("Electronics")
                .build();

        testProduct = Product.builder()
                //.id(1)
                .name("iPhone 15")
                .description("Latest iPhone model")
                .price(new BigDecimal("999.99"))
                .stock(10)
                .category(testCategory)
                .active(true)
                .build();

        testProductRequest = ProductRequest.builder()
                .name("iPhone 15")
                .description("Latest iPhone model")
                .price(new BigDecimal("999.99"))
                .stock(10)
                .categoryId(1L)
                .active(true)
                .build();

        testProductResponse = ProductResponse.builder()
                //.id(1L)
                .name("iPhone 15")
                .description("Latest iPhone model")
                .price(new BigDecimal("999.99"))
                .stock(10)
                .categoryId(1L)
                .active(true)
                .build();

        testProductDto = ProductDto.builder()
                //.id(1L)
                .name("iPhone 15")
                .price(new BigDecimal("999.99"))
                .stock(10)
                .build();
    }

    @Test
    void createProduct_ShouldReturnProductResponse() {
        // Given
        when(productMapper.toProduct(testProductRequest)).thenReturn(testProduct);
        when(categoryRepository.getCategoryById(1L)).thenReturn(testCategory);
        when(productRepository.save(testProduct)).thenReturn(testProduct);
        when(productMapper.toProductResponse(testProduct)).thenReturn(testProductResponse);

        // When
        ProductResponse result = productService.createProduct(testProductRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("iPhone 15");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("999.99"));
        verify(productMapper).toProduct(testProductRequest);
        verify(categoryRepository).getCategoryById(1L);
        verify(productRepository).save(testProduct);
        verify(productMapper).toProductResponse(testProduct);
    }

    @Test
    void getProductById_ShouldReturnProductResponse() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productMapper.toProductResponse(testProduct)).thenReturn(testProductResponse);

        // When
        ProductResponse result = productService.getProductById(productId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("iPhone 15");
        verify(productRepository).findById(productId);
        verify(productMapper).toProductResponse(testProduct);
    }

    @Test
    void getProductById_ShouldThrowException_WhenProductNotFound() {
        // Given
        when(productRepository.findById(UUID.randomUUID())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.getProductById(UUID.randomUUID()))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found with id: 999");
        verify(productRepository).findById(UUID.randomUUID());
    }

    @Test
    void getProductByIdForCart_ShouldReturnProductDto() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productMapper.toProductDto(testProduct)).thenReturn(testProductDto);

        // When
        ProductDto result = productService.getProductByIdForCart(productId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("iPhone 15");
        verify(productRepository).findById(productId);
        verify(productMapper).toProductDto(testProduct);
    }

    @Test
    void getAllProducts_ShouldReturnPageOfProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products, pageable, 1);

        when(productRepository.findAll(pageable)).thenReturn(productPage);
        when(productMapper.toProductResponse(testProduct)).thenReturn(testProductResponse);

        // When
        Page<ProductResponse> result = productService.getAllProducts(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("iPhone 15");
        verify(productRepository).findAll(pageable);
        verify(productMapper).toProductResponse(testProduct);
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProductResponse() {
        // Given
        ProductRequest updateRequest = ProductRequest.builder()
                .name("iPhone 15 Pro")
                .description("Updated description")
                .price(new BigDecimal("1199.99"))
                .stock(15)
                .categoryId(1L)
                .active(true)
                .build();

        ProductResponse updatedResponse = ProductResponse.builder()
                .id(productId)
                .name("iPhone 15 Pro")
                .description("Updated description")
                .price(new BigDecimal("1199.99"))
                .stock(15)
                .categoryId(1L)
                .active(true)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(categoryRepository.getCategoryById(1L)).thenReturn(testCategory);
        when(productRepository.save(testProduct)).thenReturn(testProduct);
        when(productMapper.toProductResponse(testProduct)).thenReturn(updatedResponse);

        // When
        ProductResponse result = productService.updateProduct(productId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("iPhone 15 Pro");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("1199.99"));
        verify(productRepository).findById(productId);
        verify(productMapper).updateProductFromProductRequest(updateRequest, testProduct);
        verify(categoryRepository).getCategoryById(1L);
        verify(productRepository).save(testProduct);
        verify(productMapper).toProductResponse(testProduct);
    }

    @Test
    void updateProduct_ShouldThrowException_WhenProductNotFound() {
        // Given
        when(productRepository.findById(UUID.randomUUID())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.updateProduct(UUID.randomUUID(), testProductRequest))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found with id: 999");
        verify(productRepository).findById(UUID.randomUUID());
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() {
        // Given
        when(productRepository.existsById(productId)).thenReturn(true);

        // When
        productService.deleteProduct(productId);

        // Then
        verify(productRepository).existsById(productId);
        verify(productRepository).deleteById(productId);
    }

    @Test
    void deleteProduct_ShouldThrowException_WhenProductNotFound() {
        // Given
        when(productRepository.existsById(UUID.randomUUID())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> productService.deleteProduct(UUID.randomUUID()))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found with id: 999");
        verify(productRepository).existsById(UUID.randomUUID());
    }

    @Test
    void searchProductsByName_ShouldReturnMatchingProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByNameContainingIgnoreCase("iPhone")).thenReturn(products);
        when(productMapper.toProductResponse(testProduct)).thenReturn(testProductResponse);

        // When
        List<ProductResponse> result = productService.searchProductsByName("iPhone");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("iPhone 15");
        verify(productRepository).findByNameContainingIgnoreCase("iPhone");
        verify(productMapper).toProductResponse(testProduct);
    }

    @Test
    void getProductsByCategoryId_ShouldReturnProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByCategoryId(1L)).thenReturn(products);
        when(productMapper.toProductResponse(testProduct)).thenReturn(testProductResponse);

        // When
        List<ProductResponse> result = productService.getProductsByCategoryId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("iPhone 15");
        verify(productRepository).findByCategoryId(1L);
        verify(productMapper).toProductResponse(testProduct);
    }

    @Test
    void getProductsByPriceRange_ShouldReturnProducts() {
        // Given
        BigDecimal minPrice = new BigDecimal("500");
        BigDecimal maxPrice = new BigDecimal("1000");
        List<Product> products = Arrays.asList(testProduct);

        when(productRepository.findByPriceRange(minPrice, maxPrice)).thenReturn(products);
        when(productMapper.toProductResponse(testProduct)).thenReturn(testProductResponse);

        // When
        List<ProductResponse> result = productService.getProductsByPriceRange(minPrice, maxPrice);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("iPhone 15");
        verify(productRepository).findByPriceRange(minPrice, maxPrice);
        verify(productMapper).toProductResponse(testProduct);
    }

    @Test
    void getProductsByCategoryName_ShouldReturnProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByCategoryName("Electronics")).thenReturn(products);
        when(productMapper.toProductResponse(testProduct)).thenReturn(testProductResponse);

        // When
        List<ProductResponse> result = productService.getProductsByCategoryName("Electronics");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("iPhone 15");
        verify(productRepository).findByCategoryName("Electronics");
        verify(productMapper).toProductResponse(testProduct);
    }

    @Test
    void updateProductStock_ShouldUpdateStock() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        // When
        productService.updateProductStock(productId, 3);

        // Then
        assertThat(testProduct.getStock()).isEqualTo(7); // 10 - 3
        verify(productRepository).findById(productId);
        verify(productRepository).save(testProduct);
    }

    @Test
    void updateProductStock_ShouldThrowException_WhenProductNotFound() {
        // Given
        when(productRepository.findById(UUID.randomUUID())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.updateProductStock(UUID.randomUUID(), 3))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found with id: 999");
        verify(productRepository).findById(UUID.randomUUID());
    }

    @Test
    void updateProductStock_ShouldThrowException_WhenInsufficientStock() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When & Then - пытаемся заказать больше, чем есть в наличии
        assertThatThrownBy(() -> productService.updateProductStock(productId, 15))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Insufficient stock. Available: 10, Requested: 15");
        verify(productRepository).findById(productId);
        // Не должно быть вызова save, так как исключение выбрасывается раньше
    }
}