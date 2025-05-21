package org.example.processorservice.service;

import org.example.processorservice.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class KafkaConsumerImpl {
    @Autowired
    private KafkaProducerService notificationProducer ;
    List<PlatformInformationDTO> platformInformationDTO;
    @Autowired
    private ProcessorService processorService; // Inject your API client
    @KafkaListener(topics = "Jobs-Topic", groupId = "job-consumer-group", containerFactory = "jobKafkaListenerContainerFactory")
    public void listen(ActionDTO message)
    {
        List<List<Integer>> metricsId = message.getCondition();
        boolean andOperator = true;
        boolean orOperator = false;
        for (List<Integer> integers : metricsId) {
            for (Integer integer : integers) {
                MetricsDTO metric = processorService.getMetricById((Long) integer.longValue());
                if (!getIssuesIfThresholdMet(this.platformInformationDTO
                        , metric.getLabel(), metric.getThreshold(), metric.getTimeFrameHours())) {
                    andOperator = false;
                    break;
                }
            }
            if (andOperator) {
                orOperator = true;
                break;
            }
        }
        if(orOperator) {
            String topic = message.getActionType().toLowerCase() + ("-topic");
            // Create structured notification
            /// ////////////////
            NotificationMessageDTO notificationMessageDTO = NotificationMessageDTO.builder()
                    .type("ACTION")
                    .topic(topic)
                    .user(NotificationMessageDTO.user.builder()
                            .email(message.getTo())
                            .userId(message.getUserId())
                            .build())
                    .message(message.getMessage())
                    .build();
            notificationProducer.sendNotification(topic, notificationMessageDTO);
        }
        /// //////////////
        System.out.println("message recieved :"+ message+" from MstTopic Dawood is here!! !!");
    }

    // Runs every minute
    //@Scheduled(cron = "0 0 * * * *")
    //every 5 min
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void fetchPlatformInfo(){
        this.platformInformationDTO = this.processorService.getInformation();
    }


    public boolean getIssuesIfThresholdMet(List<PlatformInformationDTO> allIssues
            , Label label, int threshold, int timeFrameHours) {
        LocalDateTime timeFrameStart = LocalDateTime.now().minusHours(timeFrameHours);

        List<PlatformInformationDTO> filteredIssues = allIssues.stream()
                .filter(issue -> issue.getLabel().equals(label.toString()))
                .filter(issue -> issue.getTimestamp().isAfter(timeFrameStart)) // assuming you have a createdAt field
                .toList();

        if (filteredIssues.size() >= threshold) {
            return true;
        } else {
            return false;
        }
    }
}
