package org.com.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.orderservice.config.KafkaConfig;
import org.com.orderservice.dto.event.UserEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final UserCacheService userCacheService;

    @KafkaListener(topics = KafkaConfig.TOPIC_USER_CREATED, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserCreatedEvent(UserEvent userEvent) {
        log.info("User created event received: {}", userEvent);
        userCacheService.cacheUserInfo(userEvent.getUserId(), userEvent.getEmail(), userEvent.getFullName());
    }

    @KafkaListener(topics = KafkaConfig.TOPIC_USER_UPDATED, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserUpdatedEvent(UserEvent userEvent) {
        log.info("User updated event received: {}", userEvent);
        
        if (userEvent.getEventType() == UserEvent.UserEventType.UPDATED) {
            userCacheService.updateUserCache(userEvent.getUserId(), userEvent.getEmail(), userEvent.getFullName());
        } else if (userEvent.getEventType() == UserEvent.UserEventType.DEACTIVATED) {
            userCacheService.markUserInactive(userEvent.getUserId());
        } else if (userEvent.getEventType() == UserEvent.UserEventType.ACTIVATED) {
            userCacheService.markUserActive(userEvent.getUserId());
        }
    }

    @KafkaListener(topics = KafkaConfig.TOPIC_USER_DELETED, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserDeletedEvent(UserEvent userEvent) {
        log.info("User deleted event received: {}", userEvent);
        userCacheService.removeUserFromCache(userEvent.getUserId());
    }
} 