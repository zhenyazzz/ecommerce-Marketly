package org.example.userservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.userservice.kafka.event.UserProfileUpdatedEvent;
import org.example.userservice.kafka.event.UserDeletedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendUserProfileUpdatedEvent(String topic, UserProfileUpdatedEvent event) {
        log.info("Sending UserProfileUpdatedEvent to topic {}: {}", topic, event);
        kafkaTemplate.send(topic, event);
    }

    public void sendUserDeletedEvent(String topic, UserDeletedEvent event) {
        log.info("Sending UserDeletedEvent to topic {}: {}", topic, event);
        kafkaTemplate.send(topic, event);
    }
} 