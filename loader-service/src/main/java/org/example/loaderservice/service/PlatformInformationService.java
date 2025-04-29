package org.example.loaderservice.service;

import org.example.loaderservice.dto.LoaderResponseDTO;
import org.example.loaderservice.repository.bean.PlatformInformation;
import org.example.loaderservice.repository.PlatformInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlatformInformationService {
    @Autowired
    PlatformInformationRepository platformInformationRepository;

//    public void clearUsersTable() {
//        platformInformationRepository.truncateTable();
//    }
    public void addNewInfo(PlatformInformation platformInformation) {
        platformInformationRepository.save(platformInformation);
    }

    public LoaderResponseDTO mostLabelDevelper(String label, int days) {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(days);
        //Pageable topOne = PageRequest.of(0, 1);

        List<LoaderResponseDTO> results = platformInformationRepository.findTopAssigneeByLabelSince(label, sinceDate);

        if (results.isEmpty()) {
            return new LoaderResponseDTO("No developer found for label '" + label + "' in the last " + days + " days.", null, null, null, null);
        }

        LoaderResponseDTO topResult = results.get(0);

        return topResult; // developer name (assignee)
    }

    public List<LoaderResponseDTO> labelAggregate(String label, int days) {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(days);

        List<LoaderResponseDTO> results = platformInformationRepository.countTasksByLabelForDeveloper(label, sinceDate);

        if (results.isEmpty()) {
            List<LoaderResponseDTO> emptyResponse = new ArrayList<>();
            emptyResponse.add(new LoaderResponseDTO(
                "no_data",
                "no_data",
                0L,
                0L,
                "No developer found for label '" + label + "' in the last " + days + " days."
            ));
            return emptyResponse;
        }

        return results;
    }

    public LoaderResponseDTO taskAmount(String label, int days) {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(days);

        return platformInformationRepository.countTasksForDeveloper(label, sinceDate);
    }
}
