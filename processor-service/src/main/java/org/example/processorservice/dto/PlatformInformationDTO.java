package org.example.processorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformInformationDTO {
    private Integer task_id;
    private String task_number;
    private LocalDateTime timestamp;
    private Integer manager_id;
    private String project;
    private String tag;
    private String label;
    private Integer developer_id;
    private String environment;
    private String user_story;
    private Integer task_point;
    private String sprint;
}