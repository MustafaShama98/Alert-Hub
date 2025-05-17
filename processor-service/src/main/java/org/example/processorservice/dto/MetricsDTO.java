package org.example.processorservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsDTO {
    private Long id;
    private String userId;
    private String name;
    private String label;
    private Integer threshold;
    private Integer timeFrameHours;
}
