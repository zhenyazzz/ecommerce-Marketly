package org.example.userservice.dto.response;

import org.example.userservice.model.User;

public record UserShortInfo(
        Long id,
        String username,
        String email
) {
    public static UserShortInfo fromUser(User user) {
        return new UserShortInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
