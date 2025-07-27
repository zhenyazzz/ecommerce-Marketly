package org.com.cartservice.controller;

import org.com.cartservice.dto.response.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "product-service")  // ← Имя из spring.application.name
public interface ProductServiceClient {
    @GetMapping("/api/products//{productId}/forCart")
    ProductDto getProduct(@PathVariable UUID productId);
}
