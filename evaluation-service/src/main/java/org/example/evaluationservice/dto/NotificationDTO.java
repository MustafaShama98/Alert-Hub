package org.example.evaluationservice.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class NotificationDTO {
    private String type;
    private String topic;
    private LocalDateTime timestamp;
    private UserInfo user;
    private Object data;

    @Data
    @Builder
    public static class UserInfo {
        private String userId;
        private String email;
        private String name;
    }

    @Data
    @Builder
    public static class MostLabelData {
        private String developerId;
        private String developerName;
        private String label;
        private int count;
        private int timeFrameDays;
    }

    @Data
    @Builder
    public static class LabelAggregateData {
        private String developerId;
        private int timeFrameDays;
        private List<LabelCount> labels;
    }

    @Data
    @Builder
    public static class LabelCount {
        private String label;
        private Long count;
    }

    @Data
    @Builder
    public static class TaskAmountData {
        private String developerId;
        private String developerName;
        private int taskCount;
        private int timeFrameDays;
    }

    @Data
    @Builder
    public static class EvaluationData {
        private String developerId;
        private String label;
        private int timeRange;
    }
} 