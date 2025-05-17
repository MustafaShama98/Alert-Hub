package org.example.processorservice.service;

import org.example.processorservice.dto.ActionDTO;
import org.example.processorservice.dto.NotificationMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerImpl {
    @Autowired
    private KafkaProducerService notificationProducer ;
    @KafkaListener(topics = "Jobs-Topic", groupId = "job-consumer-group", containerFactory = "jobKafkaListenerContainerFactory")
    public void listen(ActionDTO message)

    {
        String topic= message.getActionType().toLowerCase()+("-topic");
        NotificationMessageDTO  notificationMessageDTO= new NotificationMessageDTO(topic, message.getMessage(),
                10);
        notificationProducer.sendNotification(message.getActionType(),notificationMessageDTO);
        System.out.println("message recieved :"+ message+" from MstTopic Dawood is here!! !!");

    }

}
