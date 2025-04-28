package org.example.evaluationservice.service;

import org.example.evaluationservice.dto.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

public interface NotificationService {
    
    /**
     * Send a notification to the notifications queue.
     *
     * @param message The message to send
     * @param topic The topic to send to
     * @param type The type of notification
     */
    void sendNotification(String message, String topic, String type);
}

@Service
class NotificationServiceImpl implements NotificationService {

    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    @Autowired
    public NotificationServiceImpl(KafkaTemplate<String, NotificationMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendNotification(String message, String topic, String type) {
        NotificationMessage notification = NotificationMessage.create(type, message);
        kafkaTemplate.send(topic, notification);
    }
} 