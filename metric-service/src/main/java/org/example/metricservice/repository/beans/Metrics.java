package org.example.metricservice.repository.beans;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.metricservice.models.Label;

import java.time.LocalDateTime;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "Metrics")  // You can change the table name if needed
public class Metrics {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "User ID cannot be null")
    @Size(min = 1, max = 50, message = "User ID must be between 1 and 50 characters")
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @NotNull(message = "Name cannot be null")
    @Column(nullable = false)
    private String name;


    @NotNull(message = "Label cannot be null")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Label label;

    @NotNull(message = "Threshold cannot be null")
    @Min(value = 0, message = "Threshold must be a positive number")
    @Column(nullable = false)
    private Integer threshold;

    @Column(name = "time_frame_hours", nullable = false)
    @NotNull(message = "Time frame hours cannot be null")
    @Min(value = 1, message = "Time frame hours must be at least 1")
    private Integer timeFrameHours;
}