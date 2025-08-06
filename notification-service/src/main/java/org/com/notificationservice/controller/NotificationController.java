package org.com.notificationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.notificationservice.dto.NotificationRequest;
import org.com.notificationservice.model.Notification;
import org.com.notificationservice.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification Management", description = "Endpoints for managing notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Send notification")
    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(@Valid @RequestBody NotificationRequest request) {
        try {
            log.info("Received notification request: type={}, channel={}, recipient={}", 
                    request.getType(), request.getChannel(), request.getRecipient());

            Notification notification = notificationService.createNotification(request);
            boolean sent = notificationService.sendNotification(notification);

            if (sent) {
                return ResponseEntity.status(HttpStatus.CREATED).body(notification);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(notification);
            }
        } catch (Exception e) {
            log.error("Error sending notification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Health check")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification service is healthy");
    }
} 