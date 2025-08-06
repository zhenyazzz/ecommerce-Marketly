package org.com.orderservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, OrderPaymentEvent> kafkaTemplate;

    public void sendOrderCreatedEvent(OrderPaymentEvent event) {
        kafkaTemplate.send("order-events", event.orderId().toString(), event);
    }
}
