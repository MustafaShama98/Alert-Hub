package org.example.loaderservice.dto;

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
    private String developerName;

    public LoaderResponseDTO(String tag, String label, Long label_counts, Long task_counts, String payload) {
        this.tag = tag;
        this.label = label;
        this.label_counts = label_counts;
        this.task_counts = task_counts;
        this.payload = payload;
        this.developerName = tag; // Since developer name is stored in tag field
    }

    public String getDeveloperName() {
        return this.tag; // Always return tag as the developer name
    }
}
