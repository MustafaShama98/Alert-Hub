package org.example.evaluationservice.service;

import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.example.evaluationservice.dto.LoaderResponseDTO;
import org.example.evaluationservice.dto.NotificationDTO;
import org.example.evaluationservice.feign.LoaderServiceClient;
import org.springframework.stereotype.Service;

import org.example.evaluationservice.dto.DeveloperMostLabelResponse;
import org.example.evaluationservice.dto.DeveloperTaskAmountResponse;
import org.example.evaluationservice.util.UserContext;
import lombok.RequiredArgsConstructor;

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

    /**
     * Process evaluation for a developer.
     *
     * @param developerId The ID of the developer
     * @param label The label to evaluate
     * @param timeRange The time range in days
     */
    void processEvaluation(String developerId, String label, int timeRange);

    void validateUserAccess(String targetDeveloperId);
}

@Service
@RequiredArgsConstructor
class EvaluationServiceImpl implements EvaluationService {

    private final LoaderServiceClient loaderServiceClient;
    private final NotificationService notificationService;

    @Override
    public DeveloperMostLabelResponse findDeveloperWithMostLabel(String label, int sinceDays) {
        LoaderResponseDTO loaderResponse = loaderServiceClient.mostLabelDevelper(label, sinceDays);
        
        DeveloperMostLabelResponse response = DeveloperMostLabelResponse.builder()
                .developerId(loaderResponse.getTag())  // tag still contains the developer ID
                .developerName(loaderResponse.getDeveloperName()) // use developerName instead of tag
                .label(loaderResponse.getLabel())
                .count(loaderResponse.getLabel_counts().intValue())
                .timeFrameDays(sinceDays)
                .build();

        var user_id = UserContext.getUserId();
        var user_email = UserContext.getUserEmail();

        // Create structured notification
        NotificationDTO notification = NotificationDTO.builder()
                .type("MOST_LABEL_SEARCH")
                .topic("email-topic")
                .timestamp(LocalDateTime.now())
                .user(NotificationDTO.UserInfo.builder()
                        .userId(user_id)
                        .email(user_email)
                        .build())
                .data(NotificationDTO.MostLabelData.builder()
                        .developerId(response.getDeveloperId())
                        .developerName(response.getDeveloperName())
                        .label(response.getLabel())
                        .count(response.getCount())
                        .timeFrameDays(response.getTimeFrameDays())
                        .build())
                .build();

        notificationService.sendNotification(notification, "email-topic");
        
        return response;
    }

    @Override
    public List<LoaderResponseDTO> getDeveloperLabelAggregate(String developerId, int sinceDays) {
        List<LoaderResponseDTO> loaderResponses = loaderServiceClient.labelAggregate(developerId, sinceDays);
        
        // Create structured notification
        List<NotificationDTO.LabelCount> labelCounts = loaderResponses.stream()
                .map(response -> NotificationDTO.LabelCount.builder()
                        .label(response.getLabel())
                        .count(response.getLabel_counts())
                        .build())
                .collect(Collectors.toList());

        var user_id = UserContext.getUserId();
        var user_email = UserContext.getUserEmail();

        NotificationDTO notification = NotificationDTO.builder()
                .type("LABEL_AGGREGATE")
                .topic("email-topic")
                .timestamp(LocalDateTime.now())
                .user(NotificationDTO.UserInfo.builder()
                        .userId(user_id)
                        .email(user_email)
                        .build())
                .data(NotificationDTO.LabelAggregateData.builder()
                        .developerId(developerId)
                        .timeFrameDays(sinceDays)
                        .labels(labelCounts)
                        .build())
                .build();

        notificationService.sendNotification(notification, "email-topic");

        return loaderResponses;
    }

    @Override
    public DeveloperTaskAmountResponse getDeveloperTaskAmount(String developerId, int sinceDays) {
        LoaderResponseDTO loaderResponse = loaderServiceClient.taskAmount(developerId, sinceDays);
        
        DeveloperTaskAmountResponse response = DeveloperTaskAmountResponse.builder()
                .developerId(developerId)
                .developerName(loaderResponse.getDeveloperName()) // use developerName from response
                .taskCount(loaderResponse.getTask_counts().intValue())
                .timeFrameDays(sinceDays)
                .build();

        var user_id = UserContext.getUserId();
        var user_email = UserContext.getUserEmail();

        // Create structured notification
        NotificationDTO notification = NotificationDTO.builder()
                .type("TASK_AMOUNT")
                .topic("email-topic")
                .timestamp(LocalDateTime.now())
                .user(NotificationDTO.UserInfo.builder()
                        .userId(user_id)
                        .email(user_email)
                        .build())
                .data(NotificationDTO.TaskAmountData.builder()
                        .developerId(response.getDeveloperId())
                        .developerName(response.getDeveloperName())
                        .taskCount(response.getTaskCount())
                        .timeFrameDays(response.getTimeFrameDays())
                        .build())
                .build();

        notificationService.sendNotification(notification, "email-topic");

        return response;
    }

    @Override
    public void processEvaluation(String developerId, String label, int timeRange) {
        // Get user information
        String userId = UserContext.getUserId();
        String userEmail = UserContext.getUserEmail();
        String permissions = UserContext.getUserPermissions();

        // Log who is performing the evaluation
        System.out.println("Evaluation requested by: " + userEmail);
        System.out.println("Label: " + label);
        System.out.println("Time Range: " + timeRange + " days");


//        // Check if user is evaluating their own work or has admin access
//        if (!userId.equals(developerId) && !permissions.contains("admin")) {
//            throw new RuntimeException("You can only evaluate your own work unless you're an admin");
//        }

        // Proceed with evaluation logic
        performEvaluation(developerId, label, timeRange);
    }

    private void performEvaluation(String developerId, String label, int timeRange) {
        // Your evaluation logic here
        System.out.println("Performing evaluation for developer: " + developerId);
        System.out.println("Evaluating " + label + " issues in the last " + timeRange + " days");
        
        // Create notification for the evaluation
        NotificationDTO notification = NotificationDTO.builder()
            .type("EVALUATION_TRIGGERED")
            .topic("evaluation-topic")
            .timestamp(LocalDateTime.now())
            .user(NotificationDTO.UserInfo.builder()
                .userId(UserContext.getUserId())
                .email(UserContext.getUserEmail())
                .build())
            .data(NotificationDTO.EvaluationData.builder()
                .developerId(developerId)
                .label(label)
                .timeRange(timeRange)
                .build())
            .build();

        notificationService.sendNotification(notification, "evaluation-topic");
    }

    @Override
    public void validateUserAccess(String targetDeveloperId) {
        String userId = UserContext.getUserId();
        String permissions = UserContext.getUserPermissions();

        boolean hasAccess = userId.equals(targetDeveloperId) || permissions.contains("admin");
        if (!hasAccess) {
            throw new RuntimeException("Access denied: You can only access your own data unless you're an admin");
        }
    }
}



