package org.example.actionschedulerservice.repository;

import org.example.actionschedulerservice.model.RunOnDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.actionschedulerservice.repository.beans.Action;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {
    List<Action> findByUserId(Long id);
    Optional<Action> findById(Long id);
    Optional<List<Action>> findByRunOnDay(RunOnDay day);
    Optional<List<Action>> findByRunOnTime(LocalTime time);
    List<Action> findByRunOnDayAndRunOnTime(RunOnDay runOnDays, LocalTime runOnTime);


}