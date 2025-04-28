package org.example.loaderservice.repository;

import feign.Param;
import org.example.loaderservice.repository.bean.PlatformInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlatformInformationRepository extends JpaRepository<PlatformInformation, Long> {
    @Query(value = "TRUNCATE TABLE users", nativeQuery = true)
    void truncateTable();

    @Query("SELECT p.tag, COUNT(p) as taskCount " +
            "FROM PlatformInformation p " +
            "WHERE p.label = :label AND p.timestamp >= :sinceDate " +
            "GROUP BY p.tag " +
            "ORDER BY taskCount DESC")
    List<Object[]> findTopAssigneeByLabelSince(@Param("label") String label, @Param("sinceDate") LocalDateTime sinceDate);


    @Query("SELECT p.label, COUNT(DISTINCT p.task_number) " +
            "FROM PlatformInformation p " +
            "WHERE p.developer_id = :developer AND p.timestamp >= :sinceDate " +
            "GROUP BY p.label")
    List<Object[]> countTasksByLabelForDeveloper(@Param("developer") String developer
            , @Param("sinceDate") LocalDateTime sinceDate);

    @Query("SELECT COUNT(DISTINCT p.task_number) " +
            "FROM PlatformInformation p " +
            "WHERE p.developer_id = :developer AND p.timestamp >= :sinceDate ")
    Long countTaskslForDeveloper(@Param("developer") String developer
            , @Param("sinceDate") LocalDateTime sinceDate);


}
