package org.example.metricservice.controller;
import org.example.metricservice.repository.beans.Metrics;
import org.example.metricservice.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/metrics")
public class MetricsController {
    @Autowired
    MetricsService metricsService;

    //  Get a specific Metrics record by User ID

    @GetMapping("/{id}/by-user-id")
    public ResponseEntity<List<Metrics>> getMatricsByUserId(@PathVariable String id) {
        return ResponseEntity.ok(metricsService.getByUserId(id));
    }
    // Get a specific Metrics record by ID

    @GetMapping("/{id}/by-id")
    public ResponseEntity<Metrics> getMatricsById(@PathVariable Long id) {
        if(metricsService.getMetricsById(id)==null){
            //404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(metricsService.getMetricsById(id));
    }

    //  Get all Metrics records
    @GetMapping("/all")
    public ResponseEntity<List<Metrics>> getAllMetrics() {
        List<Metrics> metricsList = metricsService.findAllMetrics();
        return  ResponseEntity.ok(metricsList);
    }

    // Update a Metrics record by ID
    @PostMapping("/{id}")
    public ResponseEntity<Metrics> updateMetrics(@PathVariable Long id, @RequestBody @Valid Metrics metrics) {
       Metrics existingMetricsOptional = metricsService.updateMetric(id,metrics);
        if (existingMetricsOptional !=null) {
            return new ResponseEntity<>(existingMetricsOptional, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/delete")
    public ResponseEntity<Metrics> deleteMetrics(@PathVariable Long id) {
        Metrics existingMetricsOptional = metricsService.deleteMetric(id);
        if (existingMetricsOptional !=null) {
            return new ResponseEntity<>(existingMetricsOptional, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
