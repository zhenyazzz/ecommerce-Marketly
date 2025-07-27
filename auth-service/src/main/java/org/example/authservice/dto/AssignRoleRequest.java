package org.example.authservice.dto;

import java.util.Set;

public record AssignRoleRequest(
    Long id,
    String username,
    String role
) {
}
