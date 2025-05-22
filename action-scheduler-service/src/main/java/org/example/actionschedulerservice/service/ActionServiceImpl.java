package org.example.actionschedulerservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.actionschedulerservice.repository.ActionRepository;
import org.example.actionschedulerservice.repository.beans.Action;

import java.util.List;

@Service
public class ActionServiceImpl implements ActionService {

    @Autowired
    private ActionRepository repository;

    @Override
    public Action addAction(Action request) {
//        var user_id = UserContext.getUserId();
//        var user_email = UserContext.getUserEmail();
//        request.setUserId(user_id);
//        request.setTo(user_email);
        return repository.save(request);
    }
//
//    @Override
//    public Action updateAction(Long id, ActionRequest request) {
//        Action action = repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Action not found"));
//        BeanUtils.copyProperties(request, action, "id");
//        return repository.save(action);
//    }

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

}
