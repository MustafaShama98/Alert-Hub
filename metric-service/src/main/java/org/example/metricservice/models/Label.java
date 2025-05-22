package org.example.metricservice.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Label {
    BUG,
    DOCUMENTATION,
    DUPLICATE,
    ENHANCEMENT,
    GOOD_FIRST_ISSUE,
    HELP_WANTED,
    INVALID,
    QUESTION,
    WONTFIX;

    @JsonCreator
    public static Label fromString(String key) {
        return Arrays.stream(Label.values())
                .filter(e -> e.name().equalsIgnoreCase(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid label: " + key));
    }
}
