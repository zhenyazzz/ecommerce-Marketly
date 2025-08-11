package org.com.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateEngine templateEngine;

    
    public String processTemplate(String templateName, Map<String, Object> data) {
        try {
            Context context = new Context();
            if (data != null) {
                data.forEach(context::setVariable);
            }
            return templateEngine.process(templateName, context);
        } catch (Exception e) {
            log.error("Error processing template: {}", templateName, e);
            throw new RuntimeException("Failed to process template: " + templateName, e);
        }
    }

  
    public String getDefaultSubject(String notificationType) {
        return switch (notificationType) {
            case "ORDER_CREATED" -> "Your order has been created";
            case "ORDER_STATUS_UPDATED" -> "Your order status has been updated";
            case "ORDER_CANCELLED" -> "Your order has been cancelled";
            case "USER_REGISTERED" -> "Welcome to Marketly!";
            case "USER_EMAIL_VERIFIED" -> "Email verified successfully";
            case "PASSWORD_RESET" -> "Password reset request";
            default -> "Notification from Marketly";
        };
    }
} 