package org.example.userservice.dto.response;

import org.example.userservice.model.Role;
import org.example.userservice.model.User;

import java.util.Set;

public record ProfileResponse(
        String username,
        String email,
        String firstName,
        String lastName,
        String phone,
        Set<Role> roles
) {
    public static ProfileResponse fromUser(User user) {
        return new ProfileResponse(
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getRoles()
        );
    }
}
