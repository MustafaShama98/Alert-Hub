package com.example.metrics_service.service;

import com.example.metrics_service.dto.MetricsDTO;
import com.example.metrics_service.repository.MetricsRepository;
import com.example.metrics_service.repository.beans.Metrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MetricsService {
    @Autowired
    MetricsRepository metricsRepository;

    public List<MetricsDTO> getByUserId(String id) {
        List<Metrics> metricsList = metricsRepository.findAllByUserId(id);
        return metricsList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    private MetricsDTO convertToDTO(Metrics metrics) {
        return new MetricsDTO(
                metrics.getId(),
                metrics.getUserId(),
                metrics.getName(),
                metrics.getLabel(),
                metrics.getThreshold(),
                metrics.getTimeFrameHours()
        );
    }
}
