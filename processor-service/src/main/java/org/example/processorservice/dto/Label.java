package org.example.processorservice.dto;


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

    public String getDescription() {
        switch (this) {
            case BUG:
                return "Something isn't working";
            case DOCUMENTATION:
                return "Improvements or additions to documentation";
            case DUPLICATE:
                return "This issue or pull request already exists";
            case ENHANCEMENT:
                return "New feature or request";
            case GOOD_FIRST_ISSUE:
                return "Good for newcomers";
            case HELP_WANTED:
                return "Extra attention is needed";
            case INVALID:
                return "This doesn't seem right";
            case QUESTION:
                return "Further information is requested";
            case WONTFIX:
                return "This will not be worked on";
            default:
                return name(); // fallback
        }
    }
}
