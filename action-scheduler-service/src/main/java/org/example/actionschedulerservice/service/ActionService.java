package org.example.actionschedulerservice.service;

import org.example.actionschedulerservice.repository.beans.Action;

import java.util.List;

public interface ActionService {
    Action addAction(Action request);
    Action updateAction(Long id, Action request);
    void deleteAction(Long id);
    Action findById(Long id);
    List<Action> findByUserId(Long userId);
    List<Action> findAll();
}