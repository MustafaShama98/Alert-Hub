package org.example.evaluationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private String type;
    private String message;
    private long timestamp;

    public static NotificationMessage create(String type, String message) {
        return new NotificationMessage(type, message, System.currentTimeMillis());
    }
}
