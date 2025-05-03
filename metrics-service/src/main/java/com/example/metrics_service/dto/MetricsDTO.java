package com.example.metrics_service.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MetricsDTO {
    private Long id;
    @NotBlank(message = "Username is required")
    private String userId;
    @NotBlank(message = "Name must not be blank")
    private String name;
    @NotBlank(message = "Label must not be blank")
    private String label;

    @NotNull(message = "Threshold is required")
    @Min(value = 0, message = "Threshold must be greater than or equal to 0")
    private Integer threshold;

    @NotNull(message = "Time frame (in hours) is required")
    @Min(value = 1, message = "Time frame must be at least 1 hour")
    private Integer timeFrameHours;
}
