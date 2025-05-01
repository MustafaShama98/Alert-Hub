package com.example.metrics_service.repository;

import com.example.metrics_service.repository.beans.Metrics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MetricsRepository extends JpaRepository<Metrics, Long> {
    List<Metrics> findAllByUserId(String userId);
}
