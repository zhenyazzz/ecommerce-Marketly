package org.example.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private Set<String> roles;
}
