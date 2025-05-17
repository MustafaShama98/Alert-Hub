package org.example.actionschedulerservice.controller;

import org.example.actionschedulerservice.service.JobsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.actionschedulerservice.repository.beans.Action;
import org.example.actionschedulerservice.service.ActionService;

import java.util.List;

@RestController
@RequestMapping("/api/actions")
public class ActionController {
    @Autowired
    private ActionService actionService;
    @Autowired
    private JobsService jobsService;

    @PostMapping("/addAction")
    public ResponseEntity<Action> addAction(@RequestBody Action request) {
        return ResponseEntity.ok(actionService.addAction(request));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAction(@PathVariable Long id) {
        actionService.deleteAction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Action> findById(@PathVariable Long id) {
        try {
            Action action = actionService.findById(id);
            return ResponseEntity.ok(action);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Action>> findByUserId(@PathVariable Long userId) {
        if(actionService.findByUserId(userId).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actionService.findByUserId(userId));
    }
}
