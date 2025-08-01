package org.com.apigateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/health")
    public Map<String, Object> health() {
        log.info("Проверка здоровья Gateway");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "api-gateway");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Gateway работает!");
        
        return response;
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "API Gateway");
        response.put("version", "1.0.0");
        response.put("description", "Простой API Gateway для микросервисов");
        response.put("routes", new String[]{
            "/api/auth/** → auth-service",
            "/api/users/** → user-service"
        });
        
        return response;
    }
} 