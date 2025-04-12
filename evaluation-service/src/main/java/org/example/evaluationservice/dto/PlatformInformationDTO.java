package org.example.evaluationservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformInformationDTO {
    private String developer_id;
    private String label;
    private LocalDateTime timestamp;
    private String manager_id;
    private String project;
    private Integer task_point;
    private String sprint;
    private String user_story;


}
