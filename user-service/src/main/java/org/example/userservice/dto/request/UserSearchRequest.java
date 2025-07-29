package org.example.userservice.dto.request;

import org.example.userservice.model.Role;
import org.example.userservice.model.UserStatus;

import java.util.Set;

public record UserSearchRequest(
        String search,           
        UserStatus status,       
        Set<Role> roles         
) {} 