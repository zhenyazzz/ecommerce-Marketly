package org.example.userservice.dto.request;

import jakarta.validation.constraints.NotNull;
import org.example.userservice.model.Role;

public record RoleAssignRequest(
        @NotNull(message = "Role cannot be null")
        Role role
) {
}
