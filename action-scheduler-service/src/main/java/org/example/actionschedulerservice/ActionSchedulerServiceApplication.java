package org.example.actionschedulerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ActionSchedulerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActionSchedulerServiceApplication.class, args);
    }

}
