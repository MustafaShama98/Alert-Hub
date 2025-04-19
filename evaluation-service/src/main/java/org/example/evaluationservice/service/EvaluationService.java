package org.example.evaluationservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.example.evaluationservice.dto.PlatformInformationDTO;
import org.example.evaluationservice.feign.LoaderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EvaluationService {

    @Autowired
    private LoaderClient loaderClient;

    public List<PlatformInformationDTO> getHighestLabel(String label, int sinceDays) {
        LocalDateTime since = LocalDateTime.now().minusDays(sinceDays);
        return loaderClient.getDataSince(since.toString());
    }
}
