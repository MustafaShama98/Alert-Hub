package org.example.loaderservice.repository;

import org.example.loaderservice.repository.bean.PlatformInformation;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformInformationRepository extends JpaRepository<PlatformInformation, Long> {
    @Query(value = "TRUNCATE TABLE users", nativeQuery = true)
    void truncateTable();
}
