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
        


        
        return permissions;
    }
} 