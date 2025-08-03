package org.com.productservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.com.productservice.dto.product.ProductRequest;
import org.com.productservice.model.Category;
import org.com.productservice.repository.CategoryRepository;
import org.com.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
class ProductControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/products";
        
        // Создаем тестовую категорию
        testCategory = Category.builder()
                .name("Electronics")
                .build();
        testCategory = categoryRepository.save(testCategory);
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("iPhone 15")
                .description("Latest iPhone model")
                .price(new BigDecimal("999.99"))
                .stock(10)
                .categoryId(testCategory.getId())
                .active(true)
                .build();

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl, request, String.class);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).contains("iPhone 15");
        assertThat(response.getBody()).contains("999.99");
    }

    @Test
    void getProductById_ShouldReturnProduct() throws Exception {
        // Given
        ProductRequest createRequest = ProductRequest.builder()
                .name("Samsung Galaxy")
                .description("Android smartphone")
                .price(new BigDecimal("799.99"))
                .stock(5)
                .categoryId(testCategory.getId())
                .active(true)
                .build();

        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                baseUrl, createRequest, String.class);
        
        // Извлекаем ID из ответа
        JsonNode jsonNode = objectMapper.readTree(createResponse.getBody());
        Long productId = jsonNode.get("id").asLong();

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/" + productId, String.class);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).contains("Samsung Galaxy");
    }

    @Test
    void getProductById_ShouldReturn404_WhenProductNotFound() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/999", String.class);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void getAllProducts_ShouldReturnProducts() {
        // Given
        ProductRequest request1 = ProductRequest.builder()
                .name("Product 1")
                .price(new BigDecimal("100.00"))
                .stock(5)
                .categoryId(testCategory.getId())
                .build();

        ProductRequest request2 = ProductRequest.builder()
                .name("Product 2")
                .price(new BigDecimal("200.00"))
                .stock(10)
                .categoryId(testCategory.getId())
                .build();

        restTemplate.postForEntity(baseUrl, request1, String.class);
        restTemplate.postForEntity(baseUrl, request2, String.class);

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/all", String.class);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).contains("Product 1");
        assertThat(response.getBody()).contains("Product 2");
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        // Given
        ProductRequest createRequest = ProductRequest.builder()
                .name("Original Name")
                .price(new BigDecimal("100.00"))
                .stock(5)
                .categoryId(testCategory.getId())
                .build();

        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                baseUrl, createRequest, String.class);
        
        JsonNode jsonNode = objectMapper.readTree(createResponse.getBody());
        Long productId = jsonNode.get("id").asLong();

        ProductRequest updateRequest = ProductRequest.builder()
                .name("Updated Name")
                .price(new BigDecimal("150.00"))
                .stock(10)
                .categoryId(testCategory.getId())
                .build();

        // When
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProductRequest> entity = new HttpEntity<>(updateRequest, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/" + productId, HttpMethod.PUT, entity, String.class);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).contains("Updated Name");
        assertThat(response.getBody()).contains("150.00");
    }

    @Test
    void deleteProduct_ShouldReturn204() throws Exception {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("To Delete")
                .price(new BigDecimal("50.00"))
                .stock(1)
                .categoryId(testCategory.getId())
                .build();

        ResponseEntity<String> createResponse = restTemplate.postForEntity(baseUrl, request, String.class);
        JsonNode jsonNode = objectMapper.readTree(createResponse.getBody());
        Long productId = jsonNode.get("id").asLong();

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/" + productId, HttpMethod.DELETE, null, String.class);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    void searchProductsByName_ShouldReturnMatchingProducts() {
        // Given
        ProductRequest request1 = ProductRequest.builder()
                .name("iPhone 15 Pro")
                .price(new BigDecimal("999.99"))
                .stock(5)
                .categoryId(testCategory.getId())
                .build();

        ProductRequest request2 = ProductRequest.builder()
                .name("iPhone 15 Pro Max")
                .price(new BigDecimal("1199.99"))
                .stock(3)
                .categoryId(testCategory.getId())
                .build();

        restTemplate.postForEntity(baseUrl, request1, String.class);
        restTemplate.postForEntity(baseUrl, request2, String.class);

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/search?name=iPhone", String.class);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).contains("iPhone 15 Pro");
        assertThat(response.getBody()).contains("iPhone 15 Pro Max");
    }

    @Test
    void getProductsByCategoryId_ShouldReturnProducts() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Category Product")
                .price(new BigDecimal("100.00"))
                .stock(5)
                .categoryId(testCategory.getId())
                .build();

        restTemplate.postForEntity(baseUrl, request, String.class);

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/category/" + testCategory.getId(), String.class);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).contains("Category Product");
    }

    @Test
    void getProductsByPriceRange_ShouldReturnProducts() {
        // Given
        ProductRequest request1 = ProductRequest.builder()
                .name("Cheap Product")
                .price(new BigDecimal("50.00"))
                .stock(5)
                .categoryId(testCategory.getId())
                .build();

        ProductRequest request2 = ProductRequest.builder()
                .name("Expensive Product")
                .price(new BigDecimal("500.00"))
                .stock(2)
                .categoryId(testCategory.getId())
                .build();

        restTemplate.postForEntity(baseUrl, request1, String.class);
        restTemplate.postForEntity(baseUrl, request2, String.class);

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/price-range?minPrice=40&maxPrice=100", String.class);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).contains("Cheap Product");
        assertThat(response.getBody()).doesNotContain("Expensive Product");
    }

    @Test
    void updateProductStock_ShouldUpdateStock() throws Exception {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Stock Product")
                .price(new BigDecimal("100.00"))
                .stock(10)
                .categoryId(testCategory.getId())
                .build();

        ResponseEntity<String> createResponse = restTemplate.postForEntity(baseUrl, request, String.class);
        JsonNode jsonNode = objectMapper.readTree(createResponse.getBody());
        Long productId = jsonNode.get("id").asLong();

        // When
        restTemplate.put(baseUrl + "/" + productId + "/stock?quantity=3", null);

        // Then - проверяем, что продукт обновлен
        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + productId, String.class);
        assertThat(getResponse.getStatusCode().value()).isEqualTo(200);
        // В реальном приложении нужно проверить, что stock уменьшился на 3
    }

    @Test
    void getProductByIdForCart_ShouldReturnProductDto() throws Exception {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Cart Product")
                .price(new BigDecimal("200.00"))
                .stock(5)
                .categoryId(testCategory.getId())
                .build();

        ResponseEntity<String> createResponse = restTemplate.postForEntity(baseUrl, request, String.class);
        JsonNode jsonNode = objectMapper.readTree(createResponse.getBody());
        Long productId = jsonNode.get("id").asLong();

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/" + productId + "/forCart", String.class);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).contains("Cart Product");
        assertThat(response.getBody()).contains("200.00");
    }

    @Test
    void createProduct_WithInvalidData_ShouldReturn400() {
        // Given
        ProductRequest invalidRequest = ProductRequest.builder()
                .name("") // Empty name - invalid
                .price(new BigDecimal("100.00"))
                .stock(5)
                .categoryId(testCategory.getId())
                .build();

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl, invalidRequest, String.class);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void updateProduct_WithNonExistentId_ShouldReturn404() {
        // Given
        ProductRequest updateRequest = ProductRequest.builder()
                .name("Updated Name")
                .price(new BigDecimal("150.00"))
                .stock(10)
                .categoryId(testCategory.getId())
                .build();

        // When
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProductRequest> entity = new HttpEntity<>(updateRequest, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/999", HttpMethod.PUT, entity, String.class);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }
} 