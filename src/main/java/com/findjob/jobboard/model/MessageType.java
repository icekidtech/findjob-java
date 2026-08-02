package com.findjob.jobboard.model;

/**
 * MessageType Enum - Types of messages
 * Categorizes different message types for better handling
 */
public enum MessageType {
    TEXT,       // Plain text message
    FILE,       // File attachment
    IMAGE,      // Image attachment
    PROPOSAL,   // Proposal message
    OFFER,      // Work offer
    MILESTONE,  // Milestone update
    SYSTEM;     // System notification
    
    /**
     * Get display name for message type
     */
    public String getDisplayName() {
        switch (this) {
            case TEXT:
                return "Text Message";
            case FILE:
                return "File";
            case IMAGE:
                return "Image";
            case PROPOSAL:
                return "Proposal";
            case OFFER:
                return "Work Offer";
            case MILESTONE:
                return "Milestone Update";
            case SYSTEM:
                return "System Notification";
            default:
                return name();
        }
    }
}
