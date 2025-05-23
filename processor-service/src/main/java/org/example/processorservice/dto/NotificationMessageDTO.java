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
    private Object data;

    @Data
    @Builder
    public static class user {
        private String email;
        private long userId;
        private String name;
    }
    public static NotificationMessageDTO create(String type, String message, user user, String topic, Object data) {
        return new NotificationMessageDTO(type, System.currentTimeMillis(),topic,user,message,data);
    }
}