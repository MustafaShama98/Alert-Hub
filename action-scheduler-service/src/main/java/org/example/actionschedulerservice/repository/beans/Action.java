package org.example.actionschedulerservice.repository.beans;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.actionschedulerservice.model.ActionType;
import org.example.actionschedulerservice.model.ConditionConverter;
import org.example.actionschedulerservice.model.RunOnDay;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Actions")
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "create_date")
    private LocalDate createDate;

    /**
     * The condition field holds logic or references (e.g., alert conditions).
     * You may want to map this as a JSON field or a serialized string.
     */
    @Column(columnDefinition = "TEXT",name="condition")
    @Convert(converter = ConditionConverter.class)
    private List<List<Integer>> condition; // e.g., "[[1,2],[3]]"

    @Column(name = "sent_to", nullable = false)
    private String to; // e.g., phone number or email address

//    @NotNull(message = "Action Type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type")
    private ActionType actionType; // e.g., EMAIL, SMS

//    @NotNull(message = "Run time cannot be null")
    @Column(name = "run_on_time")
    private LocalTime runOnTime;

//    @NotNull(message = "Run Day cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "run_on_day")
    private RunOnDay runOnDay; // e.g., Sunday, All

    @Column(columnDefinition = "TEXT",name="message")
    private String message;

    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @Column(name = "last_run")
    private LocalDateTime lastRun;


    @PrePersist
    public void onCreate() {
        this.createDate = LocalDate.now();
        this.lastUpdate = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.lastUpdate = LocalDateTime.now();
    }
}
