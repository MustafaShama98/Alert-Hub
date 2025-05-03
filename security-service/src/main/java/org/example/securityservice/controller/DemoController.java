package org.example.securityservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is a public endpoint";
    }

    @GetMapping("/authenticated")
    @PreAuthorize("isAuthenticated()")
    public String authenticatedEndpoint() {
        return "This is an authenticated endpoint";
    }

    @GetMapping("/read")
    @PreAuthorize("hasAuthority('read')")
    public String readEndpoint() {
        return "You have READ permission";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAuthority('createAction')")
    public String createEndpoint() {
        return "You have CREATE_ACTION permission";
    }

    @GetMapping("/update")
    @PreAuthorize("hasAuthority('updateAction')")
    public String updateEndpoint() {
        return "You have UPDATE_ACTION permission";
    }

    @GetMapping("/evaluation")
    @PreAuthorize("hasAuthority('triggerEvaluation')")
    public String evaluationEndpoint() {
        return "You have TRIGGER_EVALUATION permission";
    }
}
