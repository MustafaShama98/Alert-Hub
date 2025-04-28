package org.example.loaderservice.service;

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

    public String mostLabelDevelper(String label, int days) {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(days);
        //Pageable topOne = PageRequest.of(0, 1);

        List<Object[]> results = platformInformationRepository.findTopAssigneeByLabelSince(label, sinceDate);

        if (results.isEmpty()) {
            return "No developer found for label '" + label + "' in the last " + days + " days.";
        }

        Object[] topResult = results.get(0);

        return (String)topResult[0]; // developer name (assignee)
    }

    public List<Object[]> labelAggregate(String label, int days) {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(days);
        //Pageable topOne = PageRequest.of(0, 1);

        List<Object[]> results = platformInformationRepository.countTasksByLabelForDeveloper(label, sinceDate);

        if (results.isEmpty()) {
            List<Object[]> taskCounts = new ArrayList<>();
            taskCounts.add(new Object[]{"No developer found for label '" + label + "' in the last " + days + " days."});
            return taskCounts;
        }

        return results; // developer name (assignee)
    }

    public Long taskAmount(String label, int days) {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(days);

        return platformInformationRepository.countTaskslForDeveloper(label, sinceDate);
    }
}
