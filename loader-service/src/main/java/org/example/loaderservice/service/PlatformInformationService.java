package org.example.loaderservice.service;

import org.example.loaderservice.repository.bean.PlatformInformation;
import org.example.loaderservice.repository.PlatformInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlatformInformationService {
    @Autowired
    PlatformInformationRepository platformInformationRepository;

    public void clearUsersTable() {
        platformInformationRepository.truncateTable();
    }
    public void addNewInfo(PlatformInformation platformInformation) {
        platformInformationRepository.save(platformInformation);
    }

}
