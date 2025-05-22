package org.example.processorservice.controller;

import feign.FeignException;
import org.example.processorservice.dto.ActionDTO;
import org.example.processorservice.dto.MetricsDTO;
import org.example.processorservice.service.ProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/processor")
public class ProcessorController {

    private final ProcessorService processorService;

    @Autowired
    public ProcessorController(ProcessorService processorService) {
        this.processorService = processorService;
    }

    @GetMapping("/all")
    public List<MetricsDTO> getAllMetrics() {
        return processorService.fetchAllMetrics();
    }

    @GetMapping("/by-user/{userId}")
    public List<MetricsDTO> getMetricsByUserId(@PathVariable String userId) {
        return processorService.getMetricsByUser(userId);
    }

    @GetMapping("/by-id/{id}")
    public MetricsDTO getMetricById(@PathVariable Long id) {
        try {
            return processorService.getMetricById(id);
        } catch (FeignException.NotFound e) {
            // Metric not found in the metric-service
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Metric with ID " + id + " not found");
        } catch (FeignException e) {
            // Other Feign errors (e.g. 500, 403)
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to fetch metric: " + e.getMessage(), e);
        } catch (Exception e) {
            // Fallback for unexpected errors
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", e);
        }
    }

    @PostMapping("/update/{id}")
    public MetricsDTO updateMetric(@PathVariable Long id, @RequestBody MetricsDTO metrics) {
        return processorService.sendMetricUpdate(id, metrics);
    }


    @DeleteMapping("/delete/{id}")
    public MetricsDTO deleteMetric(@PathVariable Long id) {
        return processorService.deleteMetric(id);
    }

    /**
     * Endpoint to manually trigger an action process, bypassing all predefined conditions
     * @param action The action to trigger
     * @return Response indicating success or failure
     */
    @PostMapping("/trigger-manual-process")
    public ResponseEntity<String> triggerManualProcess(@RequestBody ActionDTO action) {
        boolean success = processorService.manuallyTriggerAction(action);

        if (success) {
            return ResponseEntity.ok("Action triggered successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to trigger action");
        }
    }
}
