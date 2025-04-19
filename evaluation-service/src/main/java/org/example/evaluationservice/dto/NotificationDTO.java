package org.example.evaluationservice.dto;

import java.time.LocalDateTime;

public class NotificationDTO {
    private String message;
    private String type;
    private LocalDateTime timestamp;
    private String source;

    public NotificationDTO() {
    }

    public NotificationDTO(String message, String type, String source) {
        this.message = message;
        this.type = type;
        this.source = source;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
} 