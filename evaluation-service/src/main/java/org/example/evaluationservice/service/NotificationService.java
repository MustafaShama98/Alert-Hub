package org.example.evaluationservice.service;

import org.example.evaluationservice.dto.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

public interface NotificationService {
    
    /**
     * Send a notification to the notifications queue.
     *
     * @param notification The notification DTO to send
     * @param topic The topic to send to
     */
    void sendNotification(NotificationDTO notification, String topic);
}

@Service
class NotificationServiceImpl implements NotificationService {

    private final KafkaTemplate<String, NotificationDTO> kafkaTemplate;

    @Autowired
    public NotificationServiceImpl(KafkaTemplate<String, NotificationDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendNotification(NotificationDTO notification, String topic) {
        kafkaTemplate.send(topic, notification);
    }
} 