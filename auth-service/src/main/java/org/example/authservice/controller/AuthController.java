package org.example.authservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.authservice.dto.AssignRoleRequest;
import org.example.authservice.dto.AuthRequest;
import org.example.authservice.dto.AuthResponse;
import org.example.authservice.dto.SignUpRequest;
import org.example.authservice.kafka.KafkaProducerService;
import org.example.authservice.kafka.event.UserLoginEvent;
import org.example.authservice.kafka.event.UserRoleUpdateEvent;
import org.example.authservice.model.Role;
import org.example.authservice.model.User;
import org.example.authservice.repository.UserRepository;
import org.example.authservice.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/signUp")
    public ResponseEntity<?> register(@RequestBody SignUpRequest singUpRequest) {
        log.info("Received registration request for user: {}", singUpRequest.username());
        if (userRepository.findByUsername(singUpRequest.username()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Username already exists");
        }

        User user = User.builder()
                .username(singUpRequest.username())
                .password(passwordEncoder.encode(singUpRequest.password()))
                .roles(Set.of(Role.ROLE_USER))
                .build();

        User savedUser = userRepository.save(user);

        kafkaProducerService.sendUserRegistrationEvent(
                "user-registration",
                new org.example.authservice.kafka.event.UserRegistrationEvent(
                        savedUser.getId(),
                        savedUser.getUsername(),
                        savedUser.getRoles().stream().map(Role::name).collect(Collectors.toSet())
                )
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.username(),
                            authRequest.password()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateTokenFromUsername(authentication);

        kafkaProducerService.sendUserLoginEvent(
                "user-login",
                new UserLoginEvent(
                        authentication.getName(),
                        authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
                )
        );

        return ResponseEntity.ok(new AuthResponse(jwt, authentication.getName(), authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet())));
    }

    @PostMapping("/assign-role")
    public ResponseEntity<?> assignRole(@RequestBody AssignRoleRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRoles().stream().map(Enum::name).noneMatch(role -> role.equals(request.role()))) {
            user.getRoles().add(Role.valueOf(request.role()));
            userRepository.save(user);

            kafkaProducerService.sendUserRoleUpdateEvent(
                "user-role-update",
                new UserRoleUpdateEvent(
                    user.getUsername(),
                    request.role(),
                    "ADDED"
                )
            );
        }

        return ResponseEntity.ok("Role assigned successfully");
    }
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Invalid or missing Authorization header");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Authorization header");
            }

            String token = jwtUtil.getJwtFromHeader(authHeader);
            if (jwtUtil.validateJwtToken(token)) {
                String username = jwtUtil.getUserNameFromJwtToken(token);
                Set<String> roles = new HashSet<>(jwtUtil.getRoles(token));

                log.info("Token validated successfully for username: {}", username);
                return ResponseEntity.ok(new AuthResponse(token, username, roles));
            } else {
                log.warn("Invalid token provided");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token validation error: " + e.getMessage());
        }
    }
}