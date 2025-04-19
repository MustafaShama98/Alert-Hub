package org.example.evaluationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoaderResponse {
    private String id;
    private String content;
    private String status;
    private String timestamp;
} 