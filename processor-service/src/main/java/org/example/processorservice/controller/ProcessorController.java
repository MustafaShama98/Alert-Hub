package org.example.processorservice.controller;

import org.example.processorservice.dto.MetricsDTO;
import org.example.processorservice.service.ProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return processorService.getMetricById(id);
    }

    @PostMapping("/update/{id}")
    public MetricsDTO updateMetric(@PathVariable Long id, @RequestBody MetricsDTO metrics) {
        return processorService.sendMetricUpdate(id, metrics);
    }


    @DeleteMapping("/delete/{id}")
    public MetricsDTO deleteMetric(@PathVariable Long id) {
        return processorService.deleteMetric(id);
    }
}
