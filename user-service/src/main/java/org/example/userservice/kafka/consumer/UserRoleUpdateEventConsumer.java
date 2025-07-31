package org.example.userservice.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.userservice.kafka.event.UserRoleUpdateEvent;
import org.example.userservice.model.Role;
import org.example.userservice.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRoleUpdateEventConsumer {
    private final UserRepository userRepository;

    @KafkaListener(topics = "user-role-update", groupId = "user-service-group")
    public void consume(ConsumerRecord<String, UserRoleUpdateEvent> record) {
        UserRoleUpdateEvent event = record.value();
        log.info("Received UserRoleUpdateEvent: {}", event);
        userRepository.findByUsername(event.getUsername()).ifPresent(user -> {
            Set<Role> roles = user.getRoles();
            if ("ADDED".equals(event.getAction())) {
                roles.add(Role.valueOf(event.getRole()));
            } else if ("REMOVED".equals(event.getAction())) {
                roles.remove(Role.valueOf(event.getRole()));
            }
            user.setRoles(roles);
            userRepository.save(user);
        });
    }
} 