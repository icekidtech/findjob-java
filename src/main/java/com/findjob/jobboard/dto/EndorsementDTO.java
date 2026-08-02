package com.findjob.jobboard.dto;

import com.findjob.jobboard.model.EndorsementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * EndorsementDTO - Data Transfer Object for Endorsement entity
 * Used for API responses and data exchange
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndorsementDTO {
    
    private Long id;
    
    private Long endorserId;
    private String endorserName;
    private String endorserProfile;
    
    private Long endorsedUserId;
    private String endorsedUserName;
    
    private Long skillId;
    private String skillName;
    
    private EndorsementType endorsementType;
    private String message;
    
    private Boolean isActive;
    private Boolean isValid;
    
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime revokedAt;
    
    // Summary data
    private Integer totalEndorsementsForSkill;
    private Integer totalEndorsementsForUser;
}
