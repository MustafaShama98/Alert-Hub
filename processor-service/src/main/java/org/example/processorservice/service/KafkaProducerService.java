package org.example.processorservice.service;

import org.example.processorservice.dto.NotificationMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, NotificationMessageDTO> kafkaTemplate ;

    public void sendNotification(String topic,NotificationMessageDTO notification) {
        kafkaTemplate.send(topic, notification);
    }
}
