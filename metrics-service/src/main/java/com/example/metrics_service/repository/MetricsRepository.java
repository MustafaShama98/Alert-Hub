package com.example.metrics_service.repository;

import com.example.metrics_service.repository.beans.Metrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetricsRepository extends JpaRepository<Metrics, Long> {
    List<Metrics> findAllByUserId(String userId);
}
