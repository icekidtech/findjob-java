package com.findjob.jobboard.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Skill Entity - Represents a skill in the system
 * Skills are used to tag both users and jobs for better matching
 */
@Entity
@Table(name = "skills", indexes = {
    @Index(name = "idx_skill_name", columnList = "name"),
    @Index(name = "idx_skill_category", columnList = "category")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Skill name is required")
    @Column(nullable = false, length = 100, unique = true)
    private String name; // e.g., "Java", "React", "UI Design"
    
    @Column(length = 100)
    private String category; // Backend, Frontend, Mobile, Design, etc.
    
    @Column(columnDefinition = "TEXT")
    private String description; // Detailed description of the skill
    
    @Column(nullable = false)
    private Boolean isVerified = false; // Verified by admin
    
    @Column(nullable = false)
    private Integer endorsementCount = 0; // Total endorsements
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // ==========================================
    // Helper Methods
    // ==========================================
    
    /**
     * Increment endorsement count
     */
    public void incrementEndorsements() {
        this.endorsementCount++;
    }
    
    /**
     * Decrement endorsement count
     */
    public void decrementEndorsements() {
        if (this.endorsementCount > 0) {
            this.endorsementCount--;
        }
    }
}
