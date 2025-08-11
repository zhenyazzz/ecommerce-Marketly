package org.com.notificationservice.service.channel;

import org.com.notificationservice.model.Notification;

public interface NotificationChannelProvider {

    boolean send(Notification notification);
    
    String getChannelType();
    

    boolean isAvailable();
} 