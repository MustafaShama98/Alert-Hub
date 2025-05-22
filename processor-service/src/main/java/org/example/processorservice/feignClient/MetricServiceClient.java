package org.example.processorservice.feignClient;
import org.example.processorservice.dto.MetricsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "metric-service" , path = "/api/metrics") // Change port if needed
public interface MetricServiceClient {

    @GetMapping("/{id}/by-id")
    ResponseEntity<MetricsDTO> getMatricById(@PathVariable Long id);

    @PostMapping("/{id}")
    ResponseEntity<MetricsDTO> updateMetric(@PathVariable("id") Long id, @RequestBody MetricsDTO metrics);

    @GetMapping("/{id}/by-user-id")
    ResponseEntity<List<MetricsDTO>> getByUserId(@PathVariable("id") String userId);

    @GetMapping("/all")
    ResponseEntity<List<MetricsDTO>> getAllMetrics();

    @PostMapping("/{id}/delete")
    ResponseEntity<MetricsDTO> deleteMetric(@PathVariable("id") Long id);

    @PostMapping("/addMetric")
    ResponseEntity<?> addMetric(@RequestBody MetricsDTO metrics);
}
