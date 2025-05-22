package org.example.loaderservice.repository.bean;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "platform_information")
public class PlatformInformation {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer task_id;

    @NotNull(message = "Task number cannot be null")
    @Column(nullable = false)
    private String task_number;

    @NotNull(message = "Timestamp cannot be null")
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @NotNull(message = "Manager ID cannot be null")
    @Column(nullable = false)
    private Integer manager_id = 0;

    @NotBlank(message = "Project cannot be blank")
    @Size(max = 255, message = "Project name cannot exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String project;

    @NotBlank(message = "Assignee cannot be blank")
    @Size(max = 255, message = "Assignee name cannot exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String tag;

    @Size(max = 255, message = "Label cannot exceed 255 characters")
    private String label;

    @NotNull(message = "Developer ID cannot be null")
    @Column(nullable = false)
    private Integer developer_id = 0;

    @Size(max = 255, message = "Developer name cannot exceed 255 characters")
    @Column(name = "developer_name")
    private String developer_name;

//    @Size(max = 500, message = "Issue description cannot exceed 500 characters")
//    private String issue;

    @Size(max = 255, message = "Environment cannot exceed 255 characters")
    private String environment;

    @Size(max = 500, message = "User story cannot exceed 500 characters")
    private String user_story;

    @NotNull(message = "Task point cannot be null")
    @Column(nullable = false)
    private Integer task_point = 0;

    @Size(max = 255, message = "Sprint name cannot exceed 255 characters")
    private String sprint;

}