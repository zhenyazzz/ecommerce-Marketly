package org.example.userservice.dto;

import java.time.Instant;
import java.util.Set;

public record UserDto(
        Long id,
        String username,
        String email,
        Set<String> roles,
        String status,
        Instant createdAt,
        Instant updatedAt
) {} 