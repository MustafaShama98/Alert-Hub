package org.example.evaluationservice.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.evaluationservice.dto.LoaderResponseDTO;
import org.example.evaluationservice.dto.PlatformInformationDTO;
import org.example.evaluationservice.feign.LoaderServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.example.evaluationservice.dto.DeveloperLabelAggregateResponse;
import org.example.evaluationservice.dto.DeveloperMostLabelResponse;
import org.example.evaluationservice.dto.DeveloperTaskAmountResponse;
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
     * @return List of label aggregations with counts for the developer
     */
    List<LoaderResponseDTO> getDeveloperLabelAggregate(String developerId, int sinceDays);
    
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
        LoaderResponseDTO loaderResponse = loaderServiceClient.mostLabelDevelper(label, sinceDays);
        
        DeveloperMostLabelResponse response = DeveloperMostLabelResponse.builder()
                .developerId(loaderResponse.getTag())  // tag contains the developer ID
                .developerName(loaderResponse.getTag()) // using tag as the developer name since that's what we store
                .label(loaderResponse.getLabel())
                .count(loaderResponse.getLabel_counts().intValue()) // convert Long to int
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
        notificationService.sendNotification(notificationMessage, "email-topic", "MOST_LABEL_SEARCH");
        
        return response;
    }

    @Override
    public List<LoaderResponseDTO> getDeveloperLabelAggregate(String developerId, int sinceDays) {
        List<LoaderResponseDTO> loaderResponses = loaderServiceClient.labelAggregate(developerId, sinceDays);
        
        // Send notification to Kafka with a summary
        StringBuilder labelsBuilder = new StringBuilder();
        for (LoaderResponseDTO response : loaderResponses) {
            labelsBuilder.append("\n- ").append(response.getLabel())
                        .append(": ").append(response.getLabel_counts())
                        .append(" tasks");
        }
        
        String notificationMessage = String.format(
            "Label aggregation for developer %s in the last %d days:%s",
            developerId,
            sinceDays,
            labelsBuilder.toString()
        );
        notificationService.sendNotification(notificationMessage, "email-topic", "LABEL_AGGREGATE");

        return loaderResponses;
    }

    @Override
    public DeveloperTaskAmountResponse getDeveloperTaskAmount(String developerId, int sinceDays) {
        LoaderResponseDTO loaderResponse = loaderServiceClient.taskAmount(developerId, sinceDays);
        
        DeveloperTaskAmountResponse response = DeveloperTaskAmountResponse.builder()
                .developerId(developerId)
                .developerName(developerId) // Using developerId as name since we don't have a separate name field
                .taskCount(loaderResponse.getTask_counts().intValue())
                .timeFrameDays(sinceDays)
                .build();

        // Send notification to Kafka
        String notificationMessage = String.format(
            "Developer %s has completed %d tasks in the last %d days",
            response.getDeveloperName(),
            response.getTaskCount(),
            response.getTimeFrameDays()
        );
        notificationService.sendNotification(notificationMessage, "email-topic", "TASK_AMOUNT");

        return response;
    }
}



