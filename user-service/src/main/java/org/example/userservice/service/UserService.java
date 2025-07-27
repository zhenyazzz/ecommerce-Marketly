package org.example.userservice.service;

import lombok.RequiredArgsConstructor;
import org.example.userservice.model.Role;
import org.example.userservice.model.User;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.kafka.KafkaProducerService;
import org.example.userservice.kafka.event.UserProfileUpdatedEvent;
import org.example.userservice.kafka.event.UserDeletedEvent;
import org.example.userservice.dto.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.example.userservice.dto.UserDto;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id).map(this::toDto);
    }

    public UserDto createUser(UserRequest userRequest) {
        log.info("Saving new user: {}", userRequest);
        User user = User.builder()
                .username(userRequest.username())
                .email(userRequest.email())
                .roles(userRequest.roles() != null ? userRequest.roles().stream().map(Role::valueOf).collect(java.util.stream.Collectors.toSet()) : java.util.Set.of())
                .status(userRequest.status())
                .createdAt(java.time.Instant.now())
                .updatedAt(java.time.Instant.now())
                .build();
        return toDto(userRepository.save(user));
    }

    public Optional<UserDto> updateUser(Long id, UserRequest userRequest) {
        return userRepository.findById(id).map(existing -> {
            log.info("Updating user with id {}: {}", id, userRequest);
            existing.setUsername(userRequest.username());
            existing.setEmail(userRequest.email());
            existing.setRoles(userRequest.roles() != null ? userRequest.roles().stream().map(Role::valueOf).collect(java.util.stream.Collectors.toSet()) : java.util.Set.of());
            existing.setStatus(userRequest.status());
            existing.setUpdatedAt(java.time.Instant.now());
            User updated = userRepository.save(existing);
            kafkaProducerService.sendUserProfileUpdatedEvent(
                "user-profile-updated",
                new UserProfileUpdatedEvent(
                    updated.getId(),
                    updated.getUsername(),
                    updated.getEmail(),
                    updated.getRoles().stream().map(Role::name).collect(java.util.stream.Collectors.toSet()),
                    updated.getStatus(),
                    updated.getUpdatedAt()
                )
            );
            return toDto(updated);
        });
    }

    public void deleteUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            userRepository.deleteById(id);
            kafkaProducerService.sendUserDeletedEvent(
                "user-deleted",
                new UserDeletedEvent(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    java.time.Instant.now()
                )
            );
        });
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles() != null ? user.getRoles().stream().map(Enum::name).collect(java.util.stream.Collectors.toSet()) : java.util.Set.of(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
} 