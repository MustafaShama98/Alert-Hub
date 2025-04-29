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
            "CAST(COUNT(p) || ' tasks found for tag ' || p.tag AS string)) " +
            "FROM PlatformInformation p " +
            "WHERE p.label = :label AND p.timestamp >= :sinceDate " +
            "GROUP BY p.tag, p.label " +
            "ORDER BY COUNT(p) DESC")
    List<LoaderResponseDTO> findTopAssigneeByLabelSince(@Param("label") String label, @Param("sinceDate") LocalDateTime sinceDate);


    @Query("SELECT new org.example.loaderservice.dto.LoaderResponseDTO(" +
            "p.tag, " +  // tag will be the label itself
            "p.label, " +  // label remains the same
            "COUNT(DISTINCT p.task_number), " + // label_counts is the task count for this label
            "COUNT(DISTINCT p.task_number), " + // task_counts is the same in this case
            "CONCAT(COUNT(DISTINCT p.task_number), ' tasks found for label ', p.label)) " +
            "FROM PlatformInformation p " +
            "WHERE p.developer_id = :developer AND p.timestamp >= :sinceDate " +
            "GROUP BY p.tag, p.label " +
            "ORDER BY COUNT(DISTINCT p.task_number) DESC")
    List<LoaderResponseDTO> countTasksByLabelForDeveloper(@Param("developer") String developer,
            @Param("sinceDate") LocalDateTime sinceDate);

    @Query("SELECT new org.example.loaderservice.dto.LoaderResponseDTO(" +
            "'total', " + // tag for overall count
            "'all', " +  // label for overall count
            "COUNT(DISTINCT p.task_number), " + // label_counts
            "COUNT(DISTINCT p.task_number), " + // task_counts
            "CONCAT(COUNT(DISTINCT p.task_number), ' total tasks found for developer ', :developer)) " +
            "FROM PlatformInformation p " +
            "WHERE p.developer_id = :developer AND p.timestamp >= :sinceDate")
    LoaderResponseDTO countTasksForDeveloper(@Param("developer") String developer,
            @Param("sinceDate") LocalDateTime sinceDate);

}
