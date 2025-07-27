package org.example.authservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.authservice.kafka.event.UserRegistrationEvent;
import org.example.authservice.kafka.event.UserLoginEvent;
import org.example.authservice.kafka.event.UserRoleUpdateEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendUserRegistrationEvent(String topic, UserRegistrationEvent event) {
        log.info("Sending UserRegistrationEvent to topic {}: {}", topic, event);
        kafkaTemplate.send(topic, event);
    }

    public void sendUserLoginEvent(String topic, UserLoginEvent event) {
        log.info("Sending UserLoginEvent to topic {}: {}", topic, event);
        kafkaTemplate.send(topic, event);
    }

    public void sendUserRoleUpdateEvent(String topic, UserRoleUpdateEvent event) {
        log.info("Sending UserRoleUpdateEvent to topic {}: {}", topic, event);
        kafkaTemplate.send(topic, event);
    }
}
