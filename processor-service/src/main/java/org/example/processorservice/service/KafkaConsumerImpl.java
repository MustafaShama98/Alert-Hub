package org.example.processorservice.service;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class KafkaConsumerImpl {
    @Autowired
    private KafkaProducerService notificationProducer ;
    List<PlatformInformationDTO> platformInformationDTO;
    @Autowired
    private ProcessorService processorService; // Inject your API client
    @KafkaListener(topics = "Jobs-Topic", groupId = "job-consumer-group", containerFactory = "jobKafkaListenerContainerFactory")
    public void listen(ActionDTO message) {
        log.info("Received action message: {}", message);
        
        try {
            log.info("Fetching platform information...");
            this.platformInformationDTO = this.processorService.getInformation();
            log.info("Retrieved {} platform information records", this.platformInformationDTO != null ? this.platformInformationDTO.size() : 0);

            List<List<Integer>> metricsId = message.getCondition();
            log.info("Processing metrics conditions: {}", metricsId);

            boolean andOperator = true;
            boolean orOperator = false;

            for (List<Integer> integers : metricsId) {
                log.info("Processing AND condition group: {}", integers);
                andOperator = true;

                for (Integer integer : integers) {
                    log.info("Checking metric ID: {}", integer);
                    MetricsDTO metric = processorService.getMetricById((Long) integer.longValue());
                    log.info("Retrieved metric: label={}, threshold={}, timeFrame={}", 
                            metric.getLabel(), metric.getThreshold(), metric.getTimeFrameHours());

                    boolean thresholdMet = getIssuesIfThresholdMet(this.platformInformationDTO,
                            metric.getLabel(), metric.getThreshold(), metric.getTimeFrameHours());
                    log.info("Threshold check result for metric {}: {}", integer, thresholdMet);

                    if (!thresholdMet) {
                        andOperator = false;
                        log.info("AND condition failed for metric {}, breaking inner loop", integer);
                        break;
                    }
                }

                if (andOperator) {
                    orOperator = true;
                    log.info("AND condition group succeeded, setting OR condition to true");
                    break;
                }
                log.info("AND condition group failed, checking next group if available");
            }

            if (orOperator) {
                String topic = message.getActionType().toLowerCase() + "-topic";
                log.info("All conditions met, preparing to send notification to topic: {}", topic);

                NotificationMessageDTO notificationMessageDTO = NotificationMessageDTO.builder()
                        .type("ACTION")
                        .topic(topic)
                        .user(NotificationMessageDTO.user.builder()
                                .email(message.getTo())
                                .userId(message.getUserId())
                                .name(message.getName())
                                .build())
                        .message(message.getMessage())
                        .data(message)
                        .build();
                
                log.info("Sending notification: {}", notificationMessageDTO);
                notificationProducer.sendNotification(topic, notificationMessageDTO);
                log.info("Notification sent successfully");
            } else {
                log.info("Conditions not met, no notification will be sent");
            }
        } catch (Exception e) {
            log.error("Error processing action message: {}", message, e);
            throw e;
        }
    }

    // Runs every minute
    //@Scheduled(cron = "0 0 * * * *")
    //every 5 min
//    @Scheduled(cron = "0 */4 * * * *")
//    @Transactional
//    public void fetchPlatformInfo(){
//        this.platformInformationDTO = this.processorService.getInformation();
//    }
//

    public boolean getIssuesIfThresholdMet(List<PlatformInformationDTO> allIssues
            , Label label, int threshold, int timeFrameHours) {
        LocalDateTime timeFrameStart = LocalDateTime.now().minusHours(timeFrameHours);
        log.info("Checking issues for label {} with threshold {} in last {} hours (since {})", 
                label, threshold, timeFrameHours, timeFrameStart);

        List<PlatformInformationDTO> filteredIssues = allIssues.stream()
                .filter(issue -> {
                    boolean labelMatch = issue.getLabel().equalsIgnoreCase(label.toString());
                    System.out.println("labelMatch: " + issue.getLabel() + ' ' + label);
                    boolean timeMatch = issue.getTimestamp().isAfter(timeFrameStart);
                    log.debug("Issue: label={}, timestamp={}, labelMatch={}, timeMatch={}", 
                            issue.getLabel(), issue.getTimestamp(), labelMatch, timeMatch);
                    return labelMatch && timeMatch;
                })
                .toList();

        log.info("Found {} issues matching label {} within timeframe. Threshold is {}", 
                filteredIssues.size(), label, threshold);
        
        if (filteredIssues.size() >= threshold) {
            log.info("Threshold met for label {}: {} issues >= threshold {}", 
                    label, filteredIssues.size(), threshold);
            return true;
        } else {
            log.info("Threshold not met for label {}: {} issues < threshold {}", 
                    label, filteredIssues.size(), threshold);
            return false;
        }
    }
}
