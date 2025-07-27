package org.example.userservice.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.userservice.kafka.event.UserRegistrationEvent;
import org.example.userservice.model.Role;
import org.example.userservice.model.User;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.service.UserService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationEventConsumer {
    private final UserRepository userRepository;

    @KafkaListener(topics = "user-registration", groupId = "user-service-group")
    public void consume(ConsumerRecord<String, UserRegistrationEvent> record) {
        UserRegistrationEvent event = record.value();
        log.info("Received UserRegistrationEvent: {}", event);
        User user = User.builder()
                .username(event.getUsername())
                .email(event.getUsername() + "@example.com")
                .roles(event.getRoles().stream().map(Role::valueOf).collect(Collectors.toSet()))
                .status("ACTIVE")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        userRepository.save(user);
    }
} 