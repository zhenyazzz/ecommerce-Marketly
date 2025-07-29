package org.example.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.userservice.dto.request.CreateUserRequest;
import org.example.userservice.dto.request.UpdateUserRequest;
import org.example.userservice.dto.request.UserSearchRequest;
import org.example.userservice.dto.response.ProfileResponse;
import org.example.userservice.dto.response.UserResponse;
import org.example.userservice.exception.UserNotFoundException;
import org.example.userservice.kafka.event.UserProfileUpdatedEvent;
import org.example.userservice.kafka.producer.KafkaProducerService;
import org.example.userservice.kafka.event.UserDeletedEvent;
import org.example.userservice.kafka.event.UserCreatedEvent;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.model.User;
import org.example.userservice.model.UserStatus;
import org.example.userservice.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;
    private final UserMapper userMapper;

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toUserResponse);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public UserResponse createUser(CreateUserRequest userRequest) {
        log.info("Creating user: {}", userRequest.username());
        User user = userMapper.toUser(userRequest);
        User savedUser = userRepository.save(user);
        
        // Отправляем событие о создании пользователя
        UserCreatedEvent event = new UserCreatedEvent(
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getEmail()
        );
        kafkaProducerService.sendUserCreatedEvent("user-created", event);
        
        return userMapper.toUserResponse(savedUser);
    }

    public UserResponse updateUser(Long id, UpdateUserRequest userRequest) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        
        log.info("Updating user with id {}: {}", id, userRequest);
        userMapper.updateUserFromRequest(userRequest, existing);
        User updated = userRepository.save(existing);
        
        UserProfileUpdatedEvent event = userMapper.toUserProfileUpdatedEvent(updated);
        kafkaProducerService.sendUserProfileUpdatedEvent("user-profile-updated", event);
        
        return userMapper.toUserResponse(updated);
    }

    public void deleteUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            log.info("Deleting user with id: {}", id);
            userRepository.deleteById(id);
            
            kafkaProducerService.sendUserDeletedEvent(
                "user-deleted",
                new UserDeletedEvent(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    Instant.now()
                )
            );
        });
    }

    public ProfileResponse getMyProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return userMapper.toProfileResponse(user);
    }

    public ProfileResponse updateMyProfile(String username, UpdateUserRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        userMapper.updateUserFromRequest(request, user);
        User updatedUser = userRepository.save(user);
        
        return userMapper.toProfileResponse(updatedUser);
    }

    public Page<UserResponse> searchUsers(UserSearchRequest searchRequest, Pageable pageable) {
        log.info("Searching users with filters: {}", searchRequest);
        
        if (searchRequest.search() != null && !searchRequest.search().trim().isEmpty()) {
            // Если есть статус
            if (searchRequest.status() != null) {
                return userRepository.findByStatusAndUsernameContainingOrEmailContainingIgnoreCase(
                        searchRequest.status(), searchRequest.search(), pageable)
                        .map(userMapper::toUserResponse);
            }
            // Только поиск по тексту
            return userRepository.findByUsernameContainingOrEmailContainingIgnoreCase(
                    searchRequest.search(), pageable)
                    .map(userMapper::toUserResponse);
        }
        
        // Если есть статус и роли
        if (searchRequest.status() != null && searchRequest.roles() != null && !searchRequest.roles().isEmpty()) {
            return userRepository.findByStatusAndRolesIn(
                    searchRequest.status(), searchRequest.roles(), pageable)
                    .map(userMapper::toUserResponse);
        }
        
        // Если есть только статус
        if (searchRequest.status() != null) {
            return userRepository.findByStatus(searchRequest.status(), pageable)
                    .map(userMapper::toUserResponse);
        }
        
        // Если есть только роли
        if (searchRequest.roles() != null && !searchRequest.roles().isEmpty()) {
            return userRepository.findByRolesIn(searchRequest.roles(), pageable)
                    .map(userMapper::toUserResponse);
        }
        
        // Если нет фильтров - возвращаем всех
        return getAllUsers(pageable);
    }
} 