package org.example.processorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessageDTO {
    private String type;
    private String message;
    private long timestamp;

    public static NotificationMessageDTO create(String type, String message) {
        return new NotificationMessageDTO(type, message, System.currentTimeMillis());
    }
}