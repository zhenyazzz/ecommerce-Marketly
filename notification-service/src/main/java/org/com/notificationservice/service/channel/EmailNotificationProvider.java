package org.com.notificationservice.service.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.notificationservice.model.Notification;
import org.com.notificationservice.model.NotificationChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationProvider implements NotificationChannelProvider {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public boolean send(Notification notification) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(notification.getRecipient());
            message.setSubject(notification.getSubject());
            message.setText(notification.getContent());
            
            mailSender.send(message);
            
            log.info("Email sent successfully to: {}", notification.getRecipient());
            return true;
        } catch (Exception e) {
            log.error("Failed to send email to: {}", notification.getRecipient(), e);
            return false;
        }
    }

    @Override
    public String getChannelType() {
        return NotificationChannel.EMAIL.name();
    }

    @Override
    public boolean isAvailable() {
        try {
            return mailSender != null;
        } catch (Exception e) {
            log.error("Email provider is not available", e);
            return false;
        }
    }
} 