package org.com.orderservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UserCacheService {
    
    private final Map<UUID, UserCacheItem> userCache = new ConcurrentHashMap<>();
    
    public void cacheUserInfo(UUID userId, String email, String fullName) {
        UserCacheItem cacheItem = new UserCacheItem(email, fullName, true);
        userCache.put(userId, cacheItem);
        log.info("User info cached: {}", cacheItem);
    }
    
    public void updateUserCache(UUID userId, String email, String fullName) {
        UserCacheItem existingItem = userCache.get(userId);
        
        if (existingItem != null) {
            UserCacheItem updatedItem = new UserCacheItem(email, fullName, existingItem.isActive());
            userCache.put(userId, updatedItem);
            log.info("User cache updated: {}", updatedItem);
        } else {
            cacheUserInfo(userId, email, fullName);
        }
    }
    
    public void markUserInactive(UUID userId) {
        UserCacheItem existingItem = userCache.get(userId);
        
        if (existingItem != null) {
            UserCacheItem updatedItem = new UserCacheItem(
                    existingItem.getEmail(), 
                    existingItem.getFullName(), 
                    false
            );
            userCache.put(userId, updatedItem);
            log.info("User marked as inactive: {}", userId);
        } else {
            log.warn("Attempted to mark non-existent user as inactive: {}", userId);
        }
    }
    
    public void markUserActive(UUID userId) {
        UserCacheItem existingItem = userCache.get(userId);
        
        if (existingItem != null) {
            UserCacheItem updatedItem = new UserCacheItem(
                    existingItem.getEmail(), 
                    existingItem.getFullName(), 
                    true
            );
            userCache.put(userId, updatedItem);
            log.info("User marked as active: {}", userId);
        } else {
            log.warn("Attempted to mark non-existent user as active: {}", userId);
        }
    }
    
    public void removeUserFromCache(UUID userId) {
        userCache.remove(userId);
        log.info("User removed from cache: {}", userId);
    }
    
    public UserCacheItem getUserFromCache(UUID userId) {
        return userCache.get(userId);
    }
    
    public boolean isUserInCache(UUID userId) {
        return userCache.containsKey(userId);
    }
    
    public static class UserCacheItem {
        private final String email;
        private final String fullName;
        private final boolean active;
        
        public UserCacheItem(String email, String fullName, boolean active) {
            this.email = email;
            this.fullName = fullName;
            this.active = active;
        }
        
        public String getEmail() {
            return email;
        }
        
        public String getFullName() {
            return fullName;
        }
        
        public boolean isActive() {
            return active;
        }
        
        @Override
        public String toString() {
            return "UserCacheItem{" +
                    "email='" + email + '\'' +
                    ", fullName='" + fullName + '\'' +
                    ", active=" + active +
                    '}';
        }
    }
} 