package org.com.notificationservice.service.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.notificationservice.model.Notification;
import org.com.notificationservice.model.NotificationChannel;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsNotificationProvider implements NotificationChannelProvider {

    // TODO: Integrate with actual SMS provider (Twilio, AWS SNS, etc.)
    
    @Override
    public boolean send(Notification notification) {
        try {
            // Mock SMS sending - replace with actual SMS provider integration
            log.info("SMS sent successfully to: {} with content: {}", 
                    notification.getRecipient(), notification.getContent());
            
            // Simulate network delay
            Thread.sleep(100);
            
            return true;
        } catch (Exception e) {
            log.error("Failed to send SMS to: {}", notification.getRecipient(), e);
            return false;
        }
    }

    @Override
    public String getChannelType() {
        return NotificationChannel.SMS.name();
    }

    @Override
    public boolean isAvailable() {
        // TODO: Add actual SMS provider health check
        return true;
    }
} 