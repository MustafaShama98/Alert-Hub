package org.example.evaluationservice.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.evaluationservice.dto.PlatformInformationDTO;
import org.example.evaluationservice.feign.LoaderServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.example.evaluationservice.dto.DeveloperLabelAggregateResponse;
import org.example.evaluationservice.dto.DeveloperMostLabelResponse;
import org.example.evaluationservice.dto.DeveloperTaskAmountResponse;
import org.example.evaluationservice.dto.LoaderResponse;
import org.example.evaluationservice.model.Task;

public interface EvaluationService {
    
    /**
     * Find the developer with the most occurrences of a specific label within a time frame.
     *
     * @param label The label to search for
     * @param sinceDays The number of days to look back
     * @return The developer with the most occurrences of the specified label
     */
    DeveloperMostLabelResponse findDeveloperWithMostLabel(String label, int sinceDays);
    
    /**
     * Get the aggregation of labels for a specific developer within a time frame.
     *
     * @param developerId The ID of the developer
     * @param sinceDays The number of days to look back
     * @return The label aggregation for the developer
     */
    DeveloperLabelAggregateResponse getDeveloperLabelAggregate(String developerId, int sinceDays);
    
    /**
     * Get the total number of tasks assigned to a developer within a time frame.
     *
     * @param developerId The ID of the developer
     * @param sinceDays The number of days to look back
     * @return The total number of tasks for the developer
     */
    DeveloperTaskAmountResponse getDeveloperTaskAmount(String developerId, int sinceDays);
}

@Service
class EvaluationServiceImpl implements EvaluationService {

    private final LoaderServiceClient loaderServiceClient;
    private final NotificationService notificationService;

    @Autowired
    public EvaluationServiceImpl(LoaderServiceClient loaderServiceClient, NotificationService notificationService) {
        this.loaderServiceClient = loaderServiceClient;
        this.notificationService = notificationService;
    }

    @Override
    public DeveloperMostLabelResponse findDeveloperWithMostLabel(String label, int sinceDays) {
        // For now, return mock data
        DeveloperMostLabelResponse response = DeveloperMostLabelResponse.builder()
                .developerId("DEV123")
                .developerName("John Doe")
                .label(label)
                .count(5)
                .timeFrameDays(sinceDays)
                .build();
        
        // Send notification to Kafka
        String notificationMessage = String.format(
            "Developer %s has the most occurrences of label '%s' with %d tasks in the last %d days",
            response.getDeveloperName(), 
            response.getLabel(), 
            response.getCount(), 
            response.getTimeFrameDays()
        );
        notificationService.sendNotification(notificationMessage, "email-topic","MOST_LABEL_SEARCH");
        
        return response;
    }

    @Override
    public DeveloperLabelAggregateResponse getDeveloperLabelAggregate(String developerId, int sinceDays) {
        // Mock data for label aggregation
        Map<String, Integer> labelCounts = new HashMap<>();
        labelCounts.put("bug", 3);
        labelCounts.put("feature", 2);
        labelCounts.put("enhancement", 1);

        DeveloperLabelAggregateResponse response = DeveloperLabelAggregateResponse.builder()
                .developerId(developerId)
                .developerName("John Doe")
                .labelCounts(labelCounts)
                .timeFrameDays(sinceDays)
                .build();

        // Send notification to Kafka
        String notificationMessage = String.format(
            "Label aggregation for developer %s in the last %d days: %s",
            response.getDeveloperName(),
            response.getTimeFrameDays(),
            response.getLabelCounts()
        );
        notificationService.sendNotification(notificationMessage, "email-topic","LABEL_AGGREGATE");

        return response;
    }

    @Override
    public DeveloperTaskAmountResponse getDeveloperTaskAmount(String developerId, int sinceDays) {
        // Mock data for task amount
        DeveloperTaskAmountResponse response = DeveloperTaskAmountResponse.builder()
                .developerId(developerId)
                .developerName("John Doe")
                .taskCount(8)
                .timeFrameDays(sinceDays)
                .build();

        // Send notification to Kafka
        String notificationMessage = String.format(
            "Developer %s has completed %d tasks in the last %d days",
            response.getDeveloperName(),
            response.getTaskCount(),
            response.getTimeFrameDays()
        );
        notificationService.sendNotification(notificationMessage,"email-topic", "TASK_AMOUNT");

        return response;
    }
}



