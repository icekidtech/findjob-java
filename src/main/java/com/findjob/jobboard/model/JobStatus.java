package com.findjob.jobboard.model;

/**
 * JobStatus Enum - Represents the status of a job posting
 */
public enum JobStatus {
    OPEN("Open", "Accepting applications"),
    IN_PROGRESS("In Progress", "Freelancer assigned, work in progress"),
    COMPLETED("Completed", "Job completed successfully"),
    CANCELLED("Cancelled", "Job posting cancelled");
    
    private final String displayName;
    private final String description;
    
    JobStatus(String displayName, String description) {
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
