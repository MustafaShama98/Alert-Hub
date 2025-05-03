package com.example.metrics_service.controller;

import com.example.metrics_service.dto.MetricsDTO;
import com.example.metrics_service.service.MetricsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/metrics")
@Validated
public class MetricsController {
    
    private final MetricsService metricsService;

    @Autowired
    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/{id}/by-id")
    public ResponseEntity<List<MetricsDTO>> getInfoData(@PathVariable String id) {
        return ResponseEntity.ok(metricsService.getByUserId(id));
    }
}
