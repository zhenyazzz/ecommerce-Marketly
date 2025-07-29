package org.example.userservice.dto.request;

import jakarta.validation.constraints.*;
import org.example.userservice.model.Role;

import java.util.Set;

public record CreateUserRequest(
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email should be valid")
        @Size(max = 100, message = "Email must be at most 100 characters")
        String email,

        @Size(max = 100, message = "First name must be at most 100 characters")
        String firstName,

        @Size(max = 100, message = "Last name must be at most 100 characters")
        String lastName,

        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone must be a valid number with 10-15 digits, optional +")
        String phone,

        @NotNull(message = "Roles cannot be null")
        Set<Role> roles
) {}