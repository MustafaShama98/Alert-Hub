package org.example.processorservice.feignClient;
import org.example.processorservice.dto.MetricsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "metric-service" , url = "http://localhost:8083") // Change port if needed
public interface MetricServiceClient {

    @GetMapping("/metrics/{id}/by-id")
    ResponseEntity<MetricsDTO> getMatricById(@PathVariable Long id);

    @PostMapping("/metrics/{id}")
    ResponseEntity<MetricsDTO> updateMetric(@PathVariable("id") Long id, @RequestBody MetricsDTO metrics);

    @GetMapping("/metrics/{id}/by-user-id")
    ResponseEntity<List<MetricsDTO>> getByUserId(@PathVariable("id") String userId);

    @GetMapping("/metrics/all")
    ResponseEntity<List<MetricsDTO>> getAllMetrics();

    @PostMapping("/metrics/{id}/delete")
    ResponseEntity<MetricsDTO> deleteMetric(@PathVariable("id") Long id);
}
