package com.findjob.jobboard.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JobApplication Entity - Represents a freelancer's application/bid for a job
 * Tracks proposal details and application status
 */
@Entity
@Table(name = "job_applications", indexes = {
    @Index(name = "idx_app_job_id", columnList = "job_id"),
    @Index(name = "idx_app_freelancer_id", columnList = "freelancer_id"),
    @Index(name = "idx_app_status", columnList = "application_status"),
    @Index(name = "idx_app_applied_at", columnList = "applied_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ==========================================
    // References
    // ==========================================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "freelancer_id", nullable = false)
    private User freelancer;
    
    // ==========================================
    // Application Details
    // ==========================================
    
    @NotBlank(message = "Cover letter is required")
    @Size(min = 10, max = 2000, message = "Cover letter must be between 10 and 2000 characters")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String coverLetter; // Freelancer's proposal message
    
    @DecimalMin(value = "0.1", message = "Proposed budget must be greater than 0")
    @Column(precision = 12, scale = 2)
    private BigDecimal proposedBudget; // What freelancer will charge
    
    @Column(length = 100)
    private String proposedTimeline; // e.g., "2 weeks", "1 month"
    
    @Column(length = 500)
    private String attachmentUrl; // Portfolio or work sample URL
    
    // ==========================================
    // Application Status
    // ==========================================
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus applicationStatus = ApplicationStatus.PENDING;
    // PENDING: Waiting for client review
    // ACCEPTED: Client accepted this proposal
    // REJECTED: Client declined this proposal
    // WITHDRAWN: Freelancer withdrew the application
    
    @Column
    private LocalDateTime reviewedAt; // When client reviewed
    
    @Column(columnDefinition = "TEXT")
    private String clientFeedback; // Client's feedback on rejection
    
    // ==========================================
    // Audit Fields
    // ==========================================
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime appliedAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column
    private LocalDateTime acceptedAt;
    
    @Column
    private LocalDateTime rejectedAt;
    
    @Column
    private LocalDateTime withdrawnAt;
    
    // ==========================================
    // Helper Methods
    // ==========================================
    
    /**
     * Accept the application
     */
    public void accept() {
        this.applicationStatus = ApplicationStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
        this.reviewedAt = LocalDateTime.now();
    }
    
    /**
     * Reject the application
     */
    public void reject(String feedback) {
        this.applicationStatus = ApplicationStatus.REJECTED;
        this.rejectedAt = LocalDateTime.now();
        this.reviewedAt = LocalDateTime.now();
        this.clientFeedback = feedback;
    }
    
    /**
     * Withdraw the application
     */
    public void withdraw() {
        this.applicationStatus = ApplicationStatus.WITHDRAWN;
        this.withdrawnAt = LocalDateTime.now();
    }
    
    /**
     * Check if application is pending
     */
    public boolean isPending() {
        return ApplicationStatus.PENDING.equals(applicationStatus);
    }
    
    /**
     * Check if application is accepted
     */
    public boolean isAccepted() {
        return ApplicationStatus.ACCEPTED.equals(applicationStatus);
    }
}
