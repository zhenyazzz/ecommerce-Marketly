package org.com.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @GetMapping("/general")
    public ResponseEntity<Map<String, String>> generalFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Сервис временно недоступен. Попробуйте позже.");
        response.put("timestamp", Instant.now().toString());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    // Специфичный fallback для Cart Service
    @GetMapping("/cart-service")
    public ResponseEntity<Map<String, String>> cartServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Сервис корзин временно недоступен.");
        response.put("recommendation", "Попробуйте очистить кеш браузера.");
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    // Специфичный fallback для Product Service
    @GetMapping("/product-service")
    public ResponseEntity<Map<String, String>> productServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Сервис товаров не отвечает.");
        response.put("fallbackAction", "Показаны кэшированные данные.");
        return ResponseEntity
                .status(HttpStatus.PARTIAL_CONTENT)
                .body(response);
    }
}
