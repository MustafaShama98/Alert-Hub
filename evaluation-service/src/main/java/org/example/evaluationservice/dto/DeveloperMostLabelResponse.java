package org.example.evaluationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperMostLabelResponse {
    private String developerId;
    private String developerName;
    private String label;
    private int count;
    private int timeFrameDays;
} 