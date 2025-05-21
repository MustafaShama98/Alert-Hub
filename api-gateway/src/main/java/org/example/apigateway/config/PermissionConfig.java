package org.example.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Configuration
public class PermissionConfig {

    @Bean
    public Map<Pattern, String> endpointPermissions() {
        Map<Pattern, String> permissions = new HashMap<>();
        
        // Security Service permissions
        permissions.put(Pattern.compile("^/api/security/admin/.*"), "admin");
        
        // Loader Service permissions
        permissions.put(Pattern.compile("^/api/loader/.*"), "triggerScan");
        
        // Evaluation Service permissions
        permissions.put(Pattern.compile("^/api/evaluation/developer/most-label"), "triggerEvaluation");
        permissions.put(Pattern.compile("^/api/evaluation/developer/.*/label-aggregate"), "triggerEvaluation");
        permissions.put(Pattern.compile("^/api/evaluation/developer/.*/task-amount"), "triggerEvaluation");

        permissions.put(Pattern.compile("^/api/processor/.*"), "triggerProcess");

        permissions.put(Pattern.compile("^/api/metrics/addMetric"), "createAction");
        permissions.put(Pattern.compile("^/api/metrics/.*/delete"), "deleteAction");
        permissions.put(Pattern.compile("^/api/metrics/.*"), "updateAction");


        permissions.put(Pattern.compile("^/api/action/addAction"), "createMetric");
        permissions.put(Pattern.compile("^/api/action/deleteAction"), "deleteAction");
        permissions.put(Pattern.compile("^/api/action/.*"), "updateAction");

        return permissions;
    }
} 