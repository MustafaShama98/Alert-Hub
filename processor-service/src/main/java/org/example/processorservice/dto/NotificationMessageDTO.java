package org.example.processorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class NotificationMessageDTO {
    private String type;
    private long timestamp;
    private String topic;

    private user user;
    private String message;

    @Data
    @Builder
    public static class user {
        private String email;
        private long userId;
    }
    public static NotificationMessageDTO create(String type, String message, user user, String topic) {
        return new NotificationMessageDTO(type, System.currentTimeMillis(),topic,user,message);
    }
}