package org.example.evaluationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class LoaderResponseDTO {

    private String tag;
    private String label;
    private Long label_counts;
    private Long task_counts;
    private String payload;
}
