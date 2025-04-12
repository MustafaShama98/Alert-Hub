package org.example.evaluationservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String notificationTopic;

    public NotificationService(KafkaTemplate<String, String> kafkaTemplate,
                             @Value("${kafka.topic.notification}") String notificationTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.notificationTopic = notificationTopic;
    }

    public void sendNotification(String message) {
        kafkaTemplate.send(notificationTopic, message);
    }
} 