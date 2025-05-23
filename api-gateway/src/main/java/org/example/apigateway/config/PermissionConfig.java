package org.example.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Configuration
public class PermissionConfig {

    @Bean
    public Map<Pattern, List<String>> endpointPermissions() {
        Map<Pattern, List<String>> permissions = new HashMap<>();
        
        // Security Service permissions
        permissions.put(Pattern.compile("^/api/security/admin/.*"), List.of("admin"));
        
        // Loader Service permissions
        permissions.put(Pattern.compile("^/api/loader/.*"), List.of("triggerScan"));
        
        // Evaluation Service permissions
        permissions.put(Pattern.compile("^/api/evaluation/developer/most-label"), List.of("triggerEvaluation"));
        permissions.put(Pattern.compile("^/api/evaluation/developer/.*/label-aggregate"), List.of("triggerEvaluation"));
        permissions.put(Pattern.compile("^/api/evaluation/developer/.*/task-amount"), List.of("triggerEvaluation"));

        // Processor Service permissions - example with multiple permissions
        permissions.put(Pattern.compile("^/api/processor/trigger-manual-process"), Arrays.asList("triggerProcess", "manualOverride"));

        // Metrics Service permissions
        permissions.put(Pattern.compile("^/api/metrics/addMetric"), List.of("createMetric"));
        permissions.put(Pattern.compile("^/api/metrics/.*/delete"), List.of("deleteMetric"));
        permissions.put(Pattern.compile("^/api/metrics/all"), List.of("read"));
        permissions.put(Pattern.compile("^/api/metrics/.*"), List.of("updateMetric"));

        // Action Service permissions - example with multiple permissions
        permissions.put(Pattern.compile("^/api/action/addAction"), Arrays.asList("createAction", "modifyActions"));
        permissions.put(Pattern.compile("^/api/action/deleteAction/.*"), Arrays.asList("deleteAction", "modifyActions"));
        permissions.put(Pattern.compile("^/api/action/updateAction"), Arrays.asList("deleteAction", "modifyActions"));
        permissions.put(Pattern.compile("^/api/action/all"), List.of("read"));
        permissions.put(Pattern.compile("^/api/action/.*"), List.of("updateAction"));

        return permissions;
    }
} 