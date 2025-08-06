package org.com.notificationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.notificationservice.dto.OrderEvent;
import org.com.notificationservice.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${kafka.topics.order-events}",
            groupId = "${spring.application.name}-order-events",
            containerFactory = "orderEventKafkaListenerContainerFactory"
    )
    public void handleOrderEvent(
            @Payload OrderEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
            @Header(KafkaHeaders.OFFSET) Long offset
    ) {
        log.info("Received order event from topic: {}, partition: {}, offset: {}", topic, partition, offset);
        log.info("Order event: orderId={}, userId={}, status={}", 
                event.getOrderId(), event.getUserId(), event.getOrderStatus());

        try {
            notificationService.processOrderEvent(event);
            log.info("Successfully processed order event for orderId: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Error processing order event for orderId: {}", event.getOrderId(), e);
            // TODO: Implement dead letter queue or retry mechanism
            throw e;
        }
    }
} 