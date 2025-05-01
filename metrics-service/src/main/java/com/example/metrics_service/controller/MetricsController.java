package com.example.metrics_service.controller;

import com.example.metrics_service.dto.MetricsDTO;
import com.example.metrics_service.repository.beans.Metrics;
import com.example.metrics_service.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/metrics")
public class MetricsController {
    @Autowired
    MetricsService metricsService;
    @GetMapping("/{id}/by-id")
    public ResponseEntity<List<MetricsDTO>> getInfoData(@PathVariable String id) {
        //<Metrics> mt = metricsService.getByUserId(id);
        return ResponseEntity.ok(metricsService.getByUserId(id));
    }
}
