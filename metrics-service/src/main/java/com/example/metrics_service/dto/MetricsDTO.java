package com.example.metrics_service.dto;

public class MetricsDTO {
    private Long id;
    private String userId;
    private String name;
    private String label;
    private Integer threshold;
    private Integer timeFrameHours;

    public MetricsDTO(Long id, String userId, String name, String label, Integer threshold, Integer timeFrameHours) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.label = label;
        this.threshold = threshold;
        this.timeFrameHours = timeFrameHours;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public Integer getTimeFrameHours() {
        return timeFrameHours;
    }

    public void setTimeFrameHours(Integer timeFrameHours) {
        this.timeFrameHours = timeFrameHours;
    }
}
