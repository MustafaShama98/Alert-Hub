package org.example.processorservice.feignClient;

import org.example.processorservice.dto.LoaderResponseDTO;
import org.example.processorservice.dto.PlatformInformationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "Loader-service", url = "http://localhost:8081/api/loader/developer")
public interface LoaderServiceClient {

    @GetMapping("/jira")
    ResponseEntity<List<PlatformInformationDTO>> getInfoData();

}