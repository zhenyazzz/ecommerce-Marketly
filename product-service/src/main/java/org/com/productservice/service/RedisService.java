package org.com.productservice.service;


import lombok.RequiredArgsConstructor;
import org.com.productservice.model.Product;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
 /*   private final RedisTemplate<String, Object> redisTemplate; // Измените на Object для совместимости

    private static final String PRODUCT_KEY_PREFIX = "product:";

    public void saveProduct(Product product) {
        redisTemplate.opsForValue().set(PRODUCT_KEY_PREFIX + product.getId(), product, 24, TimeUnit.HOURS);
    }

    public Product getProduct(Long id) {
        Object value = redisTemplate.opsForValue().get(PRODUCT_KEY_PREFIX + id);
        return value instanceof Product ? (Product) value : null; // Простая проверка, лучше добавить десериализацию
    }

    public void deleteProduct(Long id) {
        redisTemplate.delete(PRODUCT_KEY_PREFIX + id);
    }*/
}
