package com.findjob.jobboard.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Review Entity - Post-project ratings and feedback
 * Allows clients to rate freelancers and vice versa
 * Builds reputation system
 */
@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_review_job", columnList = "job_id"),
    @Index(name = "idx_review_author", columnList = "author_id"),
    @Index(name = "idx_review_recipient", columnList = "recipient_id"),
    @Index(name = "idx_review_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ==========================================
    // Job & User References
    // ==========================================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job; // The job being reviewed
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author; // Who is writing the review (Client or Freelancer)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient; // Who is being reviewed
    
    // ==========================================
    // Rating Fields
    // ==========================================
    
    @Min(value = 1, message = "Overall rating must be at least 1")
    @Max(value = 5, message = "Overall rating cannot exceed 5")
    @Column(nullable = false)
    private Integer overallRating; // 1-5 stars
    
    // Detailed rating scores (for clients reviewing freelancers)
    @Min(value = 1)
    @Max(value = 5)
    @Column
    private Integer qualityRating; // Quality of work
    
    @Min(value = 1)
    @Max(value = 5)
    @Column
    private Integer communicationRating; // Responsiveness and communication
    
    @Min(value = 1)
    @Max(value = 5)
    @Column
    private Integer professionalism; // Professionalism and reliability
    
    @Min(value = 1)
    @Max(value = 5)
    @Column
    private Integer timeliness; // Meeting deadlines
    
    // ==========================================
    // Review Content
    // ==========================================
    
    @NotBlank(message = "Review text is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String reviewText; // Detailed feedback
    
    @Column(columnDefinition = "TEXT")
    private String positives; // What went well
    
    @Column(columnDefinition = "TEXT")
    private String areasForImprovement; // What could be better
    
    @Column(columnDefinition = "TEXT")
    private String wouldHireAgain; // Would you work with this person again?
    
    // ==========================================
    // Review Metadata
    // ==========================================
    
    @Column(nullable = false)
    private Boolean isPublic = true; // Whether review is visible
    
    @Column(nullable = false)
    private Boolean isAnonymous = false; // Anonymous review option
    
    @Column(nullable = false)
    private Boolean isVerified = false; // Review verified (checked for authenticity)
    
    // ==========================================
    // Moderation
    // ==========================================
    
    @Column(nullable = false)
    private Integer helpfulCount = 0; // How many found this helpful
    
    @Column(nullable = false)
    private Integer unhelpfulCount = 0; // How many found this unhelpful
    
    @Column
    private String moderationNotes; // Admin notes if flagged
    
    @Column
    private Boolean isFlagged = false; // Flagged for review
    
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
    private LocalDateTime deletedAt; // Soft delete
    
    // ==========================================
    // Helper Methods
    // ==========================================
    
    /**
     * Check if review is active
     */
    public boolean isActive() {
        return deletedAt == null && isPublic;
    }
    
    /**
     * Calculate average rating if detailed ratings are provided
     */
    public Double getAverageDetailedRating() {
        if (qualityRating == null || communicationRating == null || 
            professionalism == null || timeliness == null) {
            return null;
        }
        
        return (qualityRating + communicationRating + professionalism + timeliness) / 4.0;
    }
    
    /**
     * Mark review as helpful
     */
    public void markHelpful() {
        this.helpfulCount++;
    }
    
    /**
     * Mark review as unhelpful
     */
    public void markUnhelpful() {
        this.unhelpfulCount++;
    }
    
    /**
     * Flag review for moderation
     */
    public void flag(String reason) {
        this.isFlagged = true;
        this.moderationNotes = reason;
    }
    
    /**
     * Soft delete review
     */
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
    
    /**
     * Get helpfulness score (helpful - unhelpful)
     */
    public Integer getHelpfulnessScore() {
        return helpfulCount - unhelpfulCount;
    }
}
