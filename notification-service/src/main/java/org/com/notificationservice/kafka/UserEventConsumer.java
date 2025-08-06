package org.com.notificationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.notificationservice.dto.UserEvent;
import org.com.notificationservice.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${kafka.topics.user-events}",
            groupId = "${spring.application.name}-user-events",
            containerFactory = "userEventKafkaListenerContainerFactory"
    )
    public void handleUserEvent(
            @Payload UserEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
            @Header(KafkaHeaders.OFFSET) Long offset
    ) {
        log.info("Received user event from topic: {}, partition: {}, offset: {}", topic, partition, offset);
        log.info("User event: userId={}, eventType={}", event.getUserId(), event.getEventType());

        try {
            notificationService.processUserEvent(event);
            log.info("Successfully processed user event for userId: {}", event.getUserId());
        } catch (Exception e) {
            log.error("Error processing user event for userId: {}", event.getUserId(), e);
            // TODO: Implement dead letter queue or retry mechanism
            throw e;
        }
    }
} 