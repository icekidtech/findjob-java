package com.findjob.jobboard.model;

/**
 * BudgetType Enum - Defines how the job budget is structured
 */
public enum BudgetType {
    FIXED("Fixed Price", "Pay a fixed amount for project completion"),
    HOURLY("Hourly Rate", "Pay per hour worked");
    
    private final String displayName;
    private final String description;
    
    BudgetType(String displayName, String description) {
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
