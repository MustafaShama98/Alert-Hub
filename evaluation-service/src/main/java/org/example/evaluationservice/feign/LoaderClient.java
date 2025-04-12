package org.example.evaluationservice.feign;

import java.util.List;

import org.example.evaluationservice.dto.PlatformInformationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "loaderClient", url = "http://localhost:8081")
public interface LoaderClient {

    @GetMapping("/api/loader/since")
    List<PlatformInformationDTO> getDataSince(
            @RequestParam("since") String isoDateTime
    );
}