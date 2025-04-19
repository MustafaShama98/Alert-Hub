package org.example.evaluationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperTaskAmountResponse {
    private String developerId;
    private String developerName;
    private int taskCount;
    private int timeFrameDays;
} 