package org.example.actionschedulerservice.service;

import jakarta.transaction.Transactional;
import org.example.actionschedulerservice.repository.ActionRepository;
import org.example.actionschedulerservice.repository.beans.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

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
        Optional<List<Action>> dueTasks = repository.findByRunOnTime(LocalTime.now().withNano(0));
        System.out.println(LocalTime.now().withNano(0));
        if(dueTasks.isPresent() && !dueTasks.get().isEmpty()){
            System.out.println("full!");
        }else{
            System.out.println("empty!");

        }
        dueTasks.ifPresent(actions ->
            actions.forEach(action -> {
                System.out.println(action.getId().toString() + '\n' + action.getName());
                sendMessage(action);
//            task.setSent(true);
//            repository.save(task);
            })
        );
        System.out.println("HERE!!!!!!!!!!!!!!!");

    }
}
