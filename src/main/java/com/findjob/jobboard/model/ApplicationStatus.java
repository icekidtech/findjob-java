package com.findjob.jobboard.model;

/**
 * ApplicationStatus Enum - Represents the status of a job application
 */
public enum ApplicationStatus {
    PENDING("Pending", "Waiting for client review"),
    ACCEPTED("Accepted", "Client accepted this proposal"),
    REJECTED("Rejected", "Client declined this proposal"),
    WITHDRAWN("Withdrawn", "Freelancer withdrew the application");
    
    private final String displayName;
    private final String description;
    
    ApplicationStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
}
