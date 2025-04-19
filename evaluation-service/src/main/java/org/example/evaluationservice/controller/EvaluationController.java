package org.example.evaluationservice.controller;

import org.example.evaluationservice.dto.DeveloperLabelAggregateResponse;
import org.example.evaluationservice.dto.DeveloperMostLabelResponse;
import org.example.evaluationservice.dto.DeveloperTaskAmountResponse;
import org.example.evaluationservice.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * EvaluationController is responsible for handling requests related to evaluations.
 * It provides endpoints for creating, updating, and retrieving evaluations.
 */
@RestController
@RequestMapping("/api/evaluation")
public class EvaluationController {

    private final EvaluationService evaluationService;

    @Autowired
    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @GetMapping("/developer/most-label")
    public ResponseEntity<DeveloperMostLabelResponse> getDeveloperWithMostLabel(
            @RequestParam String label,
            @RequestParam(defaultValue = "30") int since) {
        return ResponseEntity.ok(evaluationService.findDeveloperWithMostLabel(label, since));
    }

    @GetMapping("/developer/{developerId}/label-aggregate")
    public ResponseEntity<DeveloperLabelAggregateResponse> getDeveloperLabelAggregate(
            @PathVariable String developerId,
            @RequestParam(defaultValue = "30") int since) {
        return ResponseEntity.ok(evaluationService.getDeveloperLabelAggregate(developerId, since));
    }

    @GetMapping("/developer/{developerId}/task-amount")
    public ResponseEntity<DeveloperTaskAmountResponse> getDeveloperTaskAmount(
            @PathVariable String developerId,
            @RequestParam(defaultValue = "30") int since) {
        return ResponseEntity.ok(evaluationService.getDeveloperTaskAmount(developerId, since));
    }
}
