package org.example.evaluationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

public interface NotificationService {
    
    /**
     * Send a notification to the notifications queue.
     *
     * @param message The message to send
     * @param type The type of notification
     */
    void sendNotification(String message, String type);
}

@Service
class NotificationServiceImpl implements NotificationService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String notificationTopic = "notifications";

    @Autowired
    public NotificationServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendNotification(String message, String type) {
        String notification = String.format("{\"type\":\"%s\",\"message\":\"%s\",\"timestamp\":%d}", 
                type, message, System.currentTimeMillis());
        kafkaTemplate.send(notificationTopic, notification);
    }
} 