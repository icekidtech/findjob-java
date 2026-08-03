package com.findjob.jobboard.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Endorsement Entity - Represents skill endorsements between users
 * Tracks peer verification of skills and builds credibility
 */
@Entity
@Table(name = "endorsements", indexes = {
    @Index(name = "idx_endorsement_endorser_id", columnList = "endorser_id"),
    @Index(name = "idx_endorsement_endorsed_user_id", columnList = "endorsed_user_id"),
    @Index(name = "idx_endorsement_skill_id", columnList = "skill_id"),
    @Index(name = "idx_endorsement_unique", columnList = "endorser_id,endorsed_user_id,skill_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endorsement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ==========================================
    // User References
    // ==========================================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endorser_id", nullable = false)
    private User endorser; // User giving the endorsement
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endorsed_user_id", nullable = false)
    private User endorsedUser; // User receiving the endorsement
    
    // ==========================================
    // Skill Reference
    // ==========================================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill; // The skill being endorsed
    
    // ==========================================
    // Endorsement Details
    // ==========================================
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EndorsementType endorsementType = EndorsementType.PEER;
    // PEER: From another freelancer
    // CLIENT: From a client after project completion
    // VERIFIED: From admin verification
    
    @Column(columnDefinition = "TEXT")
    private String message; // Optional endorsement message
    
    @Column(nullable = false)
    private Boolean isActive = true; // Can be revoked
    
    // ==========================================
    // Expiration (Optional)
    // ==========================================
    
    @Column
    private LocalDateTime expiresAt; // Endorsements can expire
    
    // ==========================================
    // Audit Fields
    // ==========================================
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column
    private LocalDateTime revokedAt;
    
    // ==========================================
    // Helper Methods
    // ==========================================
    
    /**
     * Check if endorsement is still active
     */
    public boolean isValid() {
        if (!isActive) {
            return false;
        }
        
        if (expiresAt != null && LocalDateTime.now().isAfter(expiresAt)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Revoke endorsement
     */
    public void revoke() {
        this.isActive = false;
        this.revokedAt = LocalDateTime.now();
    }
}
