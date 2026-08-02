package com.findjob.jobboard.model;

/**
 * UserRole Enum - Defines user types in FindJob
 */
public enum UserRole {
    FREELANCER("Freelancer", "Offers services and applies for jobs"),
    CLIENT("Client", "Posts jobs and hires freelancers"),
    ADMIN("Admin", "Manages platform and content");
    
    private final String displayName;
    private final String description;
    
    UserRole(String displayName, String description) {
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
