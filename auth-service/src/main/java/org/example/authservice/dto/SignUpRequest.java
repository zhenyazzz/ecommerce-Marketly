package org.example.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank(message = "Username cannot be blank")
        String username,

        @NotBlank
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password
) {}
