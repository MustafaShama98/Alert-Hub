package org.example.metricservice.repository;

import org.example.metricservice.repository.beans.Metrics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MetricsRepository  extends JpaRepository<Metrics, Long> {
    List<Metrics> findByUserId(String userId);
}
