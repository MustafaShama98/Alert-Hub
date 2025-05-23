package org.example.actionschedulerservice.controller;

import org.example.actionschedulerservice.service.JobsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.actionschedulerservice.repository.beans.Action;
import org.example.actionschedulerservice.service.ActionService;

import java.util.List;

@RestController
@RequestMapping("/api/action")
public class ActionController {
    @Autowired
    private ActionService actionService;
    @Autowired
    private JobsService jobsService;

    @GetMapping("/all")
    public ResponseEntity<List<Action>> getAllActions() {
        return ResponseEntity.ok(actionService.findAll());
    }

    @PostMapping("/addAction")
    public ResponseEntity<Action> addAction(@RequestBody Action request) {
        return ResponseEntity.ok(actionService.addAction(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Action> updateAction(@PathVariable Long id, @RequestBody Action request) {
        try {
            Action updatedAction = actionService.updateAction(id, request);
            return ResponseEntity.ok(updatedAction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deleteAction/{id}")
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
