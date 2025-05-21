package org.example.actionschedulerservice.service;

import jakarta.transaction.Transactional;
import org.example.actionschedulerservice.model.RunOnDay;
import org.example.actionschedulerservice.repository.ActionRepository;
import org.example.actionschedulerservice.repository.beans.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class JobsService {

    @Autowired
    private KafkaTemplate<String, Action> kafkaTemplate ;


    @Autowired
    private ActionRepository repository;
    public void sendMessage(Action message)
    {
        try {
            kafkaTemplate.send("Jobs-Topic", message).get();  // wait for confirmation
            System.out.println("Sent: " + message);
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }

    }


    // Runs every minute
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void processDueTasks() {
        RunOnDay today = RunOnDay.valueOf(LocalDate.now().getDayOfWeek().name().toUpperCase());
        LocalTime now = LocalTime.now().withNano(0);

        List<Action> dueTasks = repository.findByRunOnDayAndRunOnTime(today,now);
        List<Action> dueTasks2 = repository.findByRunOnDayAndRunOnTime(RunOnDay.ALL,now);
        System.out.println(LocalTime.now().withNano(0));

        if( !dueTasks.isEmpty()){
            System.out.println("full!");
        }else{
            System.out.println("empty!");

        }
        if(!dueTasks.isEmpty()) {

            dueTasks.forEach(action -> {
                        System.out.println(action.getId().toString() + '\n' + action.getName());
                        sendMessage(action);
//            task.setSent(true);
//            repository.save(task);
                    });

        }
        dueTasks2.forEach(action -> {
            System.out.println(action.getId().toString() + '\n' + action.getName());
            sendMessage(action);
//            task.setSent(true);
//            repository.save(task);
        });
        System.out.println("HERE!!!!!!!!!!!!!!!");

    }


}

