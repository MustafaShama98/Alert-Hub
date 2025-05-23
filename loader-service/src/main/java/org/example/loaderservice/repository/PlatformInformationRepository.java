package org.example.loaderservice.repository;

import feign.Param;

import org.example.loaderservice.dto.LoaderResponseDTO;
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

    @Query("SELECT new org.example.loaderservice.dto.LoaderResponseDTO(" +
            "p.tag, " +
            "p.label, " +
            "COUNT(CASE WHEN p.label = :label THEN 1 END), " +
            "COUNT(p), " +
            "CAST(COUNT(p) || ' tasks found for tag ' || p.tag AS string), " +
            "p.tag) " +
            "FROM PlatformInformation p " +
            "WHERE p.label = :label AND p.timestamp >= :sinceDate " +
            "GROUP BY p.tag, p.label " +
            "ORDER BY COUNT(p) DESC")
    List<LoaderResponseDTO> findTopAssigneeByLabelSince(@Param("label") String label, @Param("sinceDate") LocalDateTime sinceDate);

    @Query("SELECT new org.example.loaderservice.dto.LoaderResponseDTO(" +
            "p.tag, " +
            "p.label, " +
            "COUNT(DISTINCT p.task_number), " +
            "COUNT(DISTINCT p.task_number), " +
            "CONCAT(COUNT(DISTINCT p.task_number), ' tasks found for label ', p.label), " +
            "p.tag) " +
            "FROM PlatformInformation p " +
            "WHERE p.developer_id = :developer AND p.timestamp >= :sinceDate " +
            "GROUP BY p.tag, p.label " +
            "ORDER BY COUNT(DISTINCT p.task_number) DESC")
    List<LoaderResponseDTO> countTasksByLabelForDeveloper(@Param("developer") String developer,
            @Param("sinceDate") LocalDateTime sinceDate);

    @Query("SELECT new org.example.loaderservice.dto.LoaderResponseDTO(" +
            "(SELECT MIN(p2.tag) FROM PlatformInformation p2 WHERE CAST(p2.developer_id AS string) = :developer), " +
            "'all', " +
            "COUNT(DISTINCT p.task_number), " +
            "COUNT(DISTINCT p.task_number), " +
            "CONCAT(COUNT(DISTINCT p.task_number), ' total tasks found for developer ', :developer), " +
            "(SELECT MIN(p2.tag) FROM PlatformInformation p2 WHERE CAST(p2.developer_id AS string) = :developer)) " +
            "FROM PlatformInformation p " +
            "WHERE CAST(p.developer_id AS string) = :developer AND p.timestamp >= :sinceDate")
    LoaderResponseDTO countTasksForDeveloper(@Param("developer") String developer,
            @Param("sinceDate") LocalDateTime sinceDate);

    // Debug query to see raw data
    @Query("SELECT p FROM PlatformInformation p WHERE CAST(p.developer_id AS string) = :developer")
    List<PlatformInformation> findRawDataByDeveloperId(@Param("developer") String developer);
}
