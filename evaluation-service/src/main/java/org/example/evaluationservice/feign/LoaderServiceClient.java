package org.example.evaluationservice.feign;

import org.example.evaluationservice.dto.LoaderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "loader-service", path = "/api/loader")
public interface LoaderServiceClient {
    
    @GetMapping("/{id}")
    LoaderResponse getLoaderContent(@PathVariable("id") String id);
    
    @GetMapping("/status/{id}")
    LoaderResponse getLoaderStatus(@PathVariable("id") String id);
} 