package org.example.metricservice.service;



import org.example.metricservice.repository.beans.Metrics;
import org.example.metricservice.repository.MetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MetricsService {
    @Autowired
    MetricsRepository metricsRepository;

    public List<Metrics> getByUserId(String id) {
        return metricsRepository.findByUserId(id);
        //return metricsList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Metrics getMetricsById(Long id) {
        Optional<Metrics> optionalMetrics = metricsRepository.findById(id);
        return optionalMetrics.orElse(null);  // Return null if not found, or handle as you see fit
    }

    public List<Metrics> findAllMetrics() {
        return metricsRepository.findAll();
    }


    // Method 5: Create a new Metric
    public Metrics createMetric(Metrics metrics) {
        return metricsRepository.save(metrics);
    }

    // Method 6: Update an existing Metric
    public Metrics updateMetric(Long id, Metrics metricsDetails) {
        Metrics existingMetrics = this.getMetricsById(id);  // First, check if the metric exists
        if(existingMetrics != null){
            existingMetrics.setUserId(metricsDetails.getUserId());
            existingMetrics.setName(metricsDetails.getName());
            existingMetrics.setLabel(metricsDetails.getLabel());
            existingMetrics.setThreshold(metricsDetails.getThreshold());
            existingMetrics.setTimeFrameHours(metricsDetails.getTimeFrameHours());
            metricsRepository.save(existingMetrics);  // Save the updated metric
            return existingMetrics;
        }else{
            return null;// Save the updated metric
        }
    }
    public Metrics deleteMetric(Long id) {
        Metrics existingMetrics = this.getMetricsById(id);  // First, check if the metric exists
        if(existingMetrics != null){
            metricsRepository.deleteById(id);  // Save the updated metric
            return existingMetrics;
        }else{
            return null;// Save the updated metric
        }
    }


//    private MetricsDTO convertToDTO(Metrics metrics) {
//        return new MetricsDTO(
//                metrics.getId(),
//                metrics.getUserId(),
//                metrics.getName(),
//                metrics.getLabel(),
//                metrics.getThreshold(),
//                metrics.getTimeFrameHours()
//        );
//    }
}
