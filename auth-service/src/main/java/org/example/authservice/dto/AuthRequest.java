package org.example.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


public record AuthRequest(String username, String password) {}
