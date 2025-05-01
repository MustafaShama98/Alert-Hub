package com.example.metrics_service.repository.beans;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private Integer threshold;

    @Column(name = "time_frame_hours", nullable = false)
    private Integer timeFrameHours;

//    public Long getId() {
//        return id;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getLabel() {
//        return label;
//    }
//
//    public Integer getThreshold() {
//        return threshold;
//    }
//
//    public Integer getTimeFrameHours() {
//        return timeFrameHours;
//    }
}