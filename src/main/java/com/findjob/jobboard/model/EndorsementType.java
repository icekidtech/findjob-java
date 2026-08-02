package com.findjob.jobboard.model;

/**
 * EndorsementType Enum - Types of skill endorsements
 * Distinguishes between different sources of endorsements
 */
public enum EndorsementType {
    PEER,       // From another freelancer
    CLIENT,     // From a client after project completion
    VERIFIED;   // From admin verification
    
    /**
     * Get display name for endorsement type
     */
    public String getDisplayName() {
        switch (this) {
            case PEER:
                return "Peer Endorsement";
            case CLIENT:
                return "Client Endorsement";
            case VERIFIED:
                return "Verified Badge";
            default:
                return name();
        }
    }
}
