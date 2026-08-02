package com.findjob.jobboard.model;

/**
 * JobExperienceLevel Enum - Defines required experience level for a job
 */
public enum JobExperienceLevel {
    ENTRY_LEVEL("Entry Level", "Good for beginners"),
    INTERMEDIATE("Intermediate", "Requires some experience"),
    EXPERT("Expert", "Requires significant expertise");
    
    private final String displayName;
    private final String description;
    
    JobExperienceLevel(String displayName, String description) {
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
