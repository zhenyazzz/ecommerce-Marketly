package org.example.userservice.dto.response;

import java.time.Instant;
import java.util.Set;
import org.example.userservice.model.Role;
import org.example.userservice.model.User;
import org.example.userservice.model.UserStatus;

public record UserResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String phone,
        Set<Role> roles,
        UserStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserResponse fromUser(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getRoles(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
