package org.com.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("error", "User service is temporarily unavailable");
        response.put("message", "Пользовательский сервис временно недоступен. Попробуйте позже.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> authServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("error", "Authentication service is temporarily unavailable");
        response.put("message", "Сервис аутентификации временно недоступен. Попробуйте позже.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/cart")
    public ResponseEntity<Map<String, Object>> cartServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("error", "Cart service is temporarily unavailable");
        response.put("message", "Сервис корзины временно недоступен. Попробуйте позже.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/order")
    public ResponseEntity<Map<String, Object>> orderServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("error", "Order service is temporarily unavailable");
        response.put("message", "Сервис заказов временно недоступен. Попробуйте позже.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/payment")
    public ResponseEntity<Map<String, Object>> paymentServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("error", "Payment service is temporarily unavailable");
        response.put("message", "Сервис оплаты временно недоступен. Попробуйте позже.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/notification")
    public ResponseEntity<Map<String, Object>> notificationServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("error", "Notification service is temporarily unavailable");
        response.put("message", "Сервис уведомлений временно недоступен. Попробуйте позже.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
}
