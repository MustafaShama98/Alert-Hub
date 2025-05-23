package org.example.actionschedulerservice.service;

import org.example.actionschedulerservice.util.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.actionschedulerservice.repository.ActionRepository;
import org.example.actionschedulerservice.repository.beans.Action;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActionServiceImpl implements ActionService {

    @Autowired
    private ActionRepository repository;

    @Override
    public Action addAction(Action request) {
       var user_id = UserContext.getUserId();
       var email = UserContext.getUserEmail();
       System.out.println("user_id:----"+user_id);
       request.setUserId(Long.valueOf(user_id));
       request.setTo(email);
        return repository.save(request);
    }

    @Override
    public Action updateAction(Long id, Action request) {
        // Get current user context
        var userId = Long.valueOf(UserContext.getUserId());
        var email = UserContext.getUserEmail();

        // Find existing action
        Action existingAction = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action not found"));

        // Verify ownership
        if (!existingAction.getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized to update this action");
        }

        // Update fields while preserving certain values
        request.setId(id);
        request.setUserId(userId);
        request.setTo(email);
        request.setCreateDate(existingAction.getCreateDate());
        request.setLastUpdate(LocalDateTime.now());

        // Save and return updated action
        return repository.save(request);
    }

    @Override
    public void deleteAction(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Action findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action not found"));
    }

    @Override
    public List<Action> findByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public List<Action> findAll() {
        return repository.findAll();
    }
}
