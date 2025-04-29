package org.example.evaluationservice.feign;

import java.util.List;

import org.example.evaluationservice.dto.LoaderResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "loader-service", path = "/api/loader")
public interface LoaderServiceClient {



    @GetMapping("/most-label")
    LoaderResponseDTO mostLabelDevelper(@RequestParam("label") String label,
                                                    @RequestParam("since") int sinceDays);

    @GetMapping("/{developer_id}/label-aggregate")
    LoaderResponseDTO labelAggregate(@PathVariable String developer_id,
                                     @RequestParam int since);

    @GetMapping("/{developer_id}/task-amount")
    LoaderResponseDTO taskAmount(@PathVariable String developer_id,
                                            @RequestParam int since);
} 