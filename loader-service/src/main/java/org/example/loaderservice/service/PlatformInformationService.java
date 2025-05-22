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

        // Debug: Print raw data first
        System.out.println("\n=== Raw Data from Database ===");
        List<PlatformInformation> rawData = platformInformationRepository.findRawDataByDeveloperId(label);
        System.out.println("Found " + rawData.size() + " records for developer ID: " + label);
        for (PlatformInformation info : rawData) {
            System.out.println("Record:");
            System.out.println("  Developer ID: " + info.getDeveloper_id());
            System.out.println("  Developer Name: " + info.getDeveloper_name());
            System.out.println("  Task Number: " + info.getTask_number());
            System.out.println("  Tag: " + info.getTag());
            System.out.println("  Label: " + info.getLabel());
            System.out.println("---");
        }

        LoaderResponseDTO result = platformInformationRepository.countTasksForDeveloper(label, sinceDate);
        
        // Debug logging for DTO
        System.out.println("\n=== DTO Result ===");
        System.out.println("Developer ID: " + label);
        System.out.println("Result: " + (result != null ? result.toString() : "null"));
        if (result != null) {
            System.out.println("Tag: " + result.getTag());
            System.out.println("Label: " + result.getLabel());
            System.out.println("Label Counts: " + result.getLabel_counts());
            System.out.println("Task Counts: " + result.getTask_counts());
            System.out.println("Payload: " + result.getPayload());
            System.out.println("Developer Name: " + result.getDeveloperName());
        }

        if (result == null) {
            return new LoaderResponseDTO(
                "total",
                "all",
                0L,
                0L,
                "No tasks found for developer " + label,
                "Unknown Developer"  // Default developer name
            );
        }
        return result;
    }
}
