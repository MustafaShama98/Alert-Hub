package org.example.processorservice.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionDTO{

    private Long id;
    private Long userId;
    private String name;
    private LocalDate createDate;
    private List<List<Integer>> condition;
    private String to;
    private String actionType;
    private LocalTime runOnTime;
    private String runOnDay;
    private String message;
    private boolean isEnabled;
    private boolean isDeleted;
    private LocalDateTime lastUpdate;
    private LocalDateTime lastRun;
}