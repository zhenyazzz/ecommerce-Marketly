package org.example.userservice.dto;

import java.time.Instant;
import java.util.Set;

public record UserResponse(
        Long id,
        String email,
        Set<RoleType> roles,
        Instant createdAt
) {
}
