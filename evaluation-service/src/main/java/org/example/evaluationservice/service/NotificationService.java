package org.example.evaluationservice.service;

import org.example.evaluationservice.dto.NotificationDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String notificationTopic;

    public NotificationService(KafkaTemplate<String, Object> kafkaTemplate,
                             @Value("${kafka.topic.notification}") String notificationTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.notificationTopic = notificationTopic;
    }

    public void sendNotification(String message, String type, String source) {
        NotificationDTO notification = new NotificationDTO(message, type, source);
        kafkaTemplate.send(notificationTopic, notification);
    }
} 