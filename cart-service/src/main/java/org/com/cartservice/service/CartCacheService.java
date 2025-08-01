package org.com.cartservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.cartservice.dto.response.CartResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_PREFIX = "cart:";
    private static final Duration CACHE_TTL = Duration.ofHours(24); // 24 часа

    public void cacheCart(Long userId, CartResponse cartResponse) {
        String key = CACHE_PREFIX + userId;
        try {
            redisTemplate.opsForValue().set(key, cartResponse, CACHE_TTL);
            log.debug("Cached cart for user: {}", userId);
        } catch (Exception e) {
            log.warn("Failed to cache cart for user {}: {}", userId, e.getMessage());
        }
    }

    public Optional<CartResponse> getCachedCart(Long userId) {
        String key = CACHE_PREFIX + userId;
        try {
            CartResponse cartResponse = (CartResponse) redisTemplate.opsForValue().get(key);
            if (cartResponse != null) {
                log.debug("Cache hit for user: {}", userId);
                return Optional.of(cartResponse);
            }
        } catch (Exception e) {
            log.warn("Failed to get cached cart for user {}: {}", userId, e.getMessage());
        }
        return Optional.empty();
    }

    public void evictCart(Long userId) {
        String key = CACHE_PREFIX + userId;
        try {
            redisTemplate.delete(key);
            log.debug("Evicted cart cache for user: {}", userId);
        } catch (Exception e) {
            log.warn("Failed to evict cart cache for user {}: {}", userId, e.getMessage());
        }
    }

    public void updateCachedCart(Long userId, CartResponse cartResponse) {
        evictCart(userId);
        cacheCart(userId, cartResponse);
    }

    public boolean isCached(Long userId) {
        String key = CACHE_PREFIX + userId;
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.warn("Failed to check cache for user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    public void clearAllCarts() {
        try {
            redisTemplate.delete(redisTemplate.keys(CACHE_PREFIX + "*"));
            log.info("Cleared all cart cache");
        } catch (Exception e) {
            log.warn("Failed to clear cart cache: {}", e.getMessage());
        }
    }
} 