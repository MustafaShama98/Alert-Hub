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

    public List<PlatformInformationDTO> getHighestLabel(String label, int sinceDays) {
//        LocalDateTime since = LocalDateTime.now().minusDays(sinceDays);
//        return loaderClient.getDataSince(since.toString());
        return null;
    }

    @Override
    public DeveloperMostLabelResponse findDeveloperWithMostLabel(String label, int sinceDays) {
        // Calculate the date from which to start looking
        LocalDateTime startDate = LocalDateTime.now().minusDays(sinceDays);
        long startTimestamp = startDate.toEpochSecond(ZoneOffset.UTC);
        
        // Get data from loader service
        LoaderResponse loaderResponse = loaderServiceClient.getDataSince(String.valueOf(startTimestamp));
        
        // Process the data to find the developer with the most occurrences of the label
        Map<String, Integer> developerLabelCounts = new HashMap<>();
        
        // Assuming loaderResponse contains a list of tasks with developer and label information
        // This is a placeholder implementation - adjust based on your actual data structure
        for (Task task : loaderResponse.getTasks()) {
            if (task.getLabels().contains(label)) {
                String developerId = task.getDeveloperId();
                developerLabelCounts.merge(developerId, 1, Integer::sum);
            }
        }
        
        // Find the developer with the highest count
        String developerId = developerLabelCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("unknown");
        
        int count = developerLabelCounts.getOrDefault(developerId, 0);
        
        // Create the response
        DeveloperMostLabelResponse response = DeveloperMostLabelResponse.builder()
                .developerId(developerId)
                .developerName("Developer " + developerId) // Replace with actual developer name lookup
                .label(label)
                .count(count)
                .timeFrameDays(sinceDays)
                .build();
        
        // Send notification
        String notificationMessage = String.format("Developer %s has the most occurrences of label '%s' with %d tasks in the last %d days",
                developerId, label, count, sinceDays);
        notificationService.sendNotification(notificationMessage, "MOST_LABEL");
        
        return response;
    }

    /**
     * Get the aggregation of labels for a specific developer within a time frame.
     *
     * @param developerId The ID of the developer
     * @param sinceDays The number of days to look back
     * @return The label aggregation for the developer
     */
    @Override
    public DeveloperLabelAggregateResponse getDeveloperLabelAggregate(String developerId, int sinceDays) {
        // Calculate the date from which to start looking
        LocalDateTime startDate = LocalDateTime.now().minusDays(sinceDays);
        long startTimestamp = startDate.toEpochSecond(ZoneOffset.UTC);
        
        // Get data from loader service
        LoaderResponse loaderResponse = loaderServiceClient.getDataSince(String.valueOf(startTimestamp));
        
        // Process the data to aggregate labels for the specified developer
        Map<String, Integer> labelCounts = new HashMap<>();
        
        // Assuming loaderResponse contains a list of tasks with developer and label information
        // This is a placeholder implementation - adjust based on your actual data structure
        for (Task task : loaderResponse.getTasks()) {
            if (task.getDeveloperId().equals(developerId)) {
                for (String label : task.getLabels()) {
                    labelCounts.merge(label, 1, Integer::sum);
                }
            }
        }
        
        // Create the response
        DeveloperLabelAggregateResponse response = DeveloperLabelAggregateResponse.builder()
                .developerId(developerId)
                .developerName("Developer " + developerId) // Replace with actual developer name lookup
                .labelCounts(labelCounts)
                .timeFrameDays(sinceDays)
                .build();
        
        // Send notification
        String notificationMessage = String.format("Label aggregation for developer %s in the last %d days: %s",
                developerId, sinceDays, labelCounts.toString());
        notificationService.sendNotification(notificationMessage, "LABEL_AGGREGATE");
        
        return response;
    }

    /**
     * Get the total number of tasks assigned to a developer within a time frame.
     *
     * @param developerId The ID of the developer
     * @param sinceDays The number of days to look back
     * @return The total number of tasks for the developer
     */
    @Override
    public DeveloperTaskAmountResponse getDeveloperTaskAmount(String developerId, int sinceDays) {
        // Calculate the date from which to start looking
        LocalDateTime startDate = LocalDateTime.now().minusDays(sinceDays);
        long startTimestamp = startDate.toEpochSecond(ZoneOffset.UTC);
        
        // Get data from loader service
        LoaderResponse loaderResponse = loaderServiceClient.getDataSince(String.valueOf(startTimestamp));
        
        // Count tasks for the specified developer
        int taskCount = 0;
        
        // Assuming loaderResponse contains a list of tasks with developer information
        // This is a placeholder implementation - adjust based on your actual data structure
        for (Task task : loaderResponse.getTasks()) {
            if (task.getDeveloperId().equals(developerId)) {
                taskCount++;
            }
        }
        
        // Create the response
        DeveloperTaskAmountResponse response = DeveloperTaskAmountResponse.builder()
                .developerId(developerId)
                .developerName("Developer " + developerId) // Replace with actual developer name lookup
                .taskCount(taskCount)
                .timeFrameDays(sinceDays)
                .build();
        
        // Send notification
        String notificationMessage = String.format("Developer %s has %d tasks in the last %d days",
                developerId, taskCount, sinceDays);
        notificationService.sendNotification(notificationMessage, "TASK_AMOUNT");
        
        return response;
    }
}
