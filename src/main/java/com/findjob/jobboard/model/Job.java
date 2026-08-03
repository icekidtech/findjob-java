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
import java.util.HashSet;
import java.util.Set;

/**
 * Job Entity - Represents job postings created by clients
 * Stores comprehensive job information including budget, skills, and status
 */
@Entity
@Table(name = "jobs", indexes = {
    @Index(name = "idx_job_client_id", columnList = "client_id"),
    @Index(name = "idx_job_status", columnList = "job_status"),
    @Index(name = "idx_job_category", columnList = "category"),
    @Index(name = "idx_job_posted_at", columnList = "posted_at"),
    @Index(name = "idx_job_deadline", columnList = "deadline")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ==========================================
    // Client Reference
    // ==========================================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;
    
    // ==========================================
    // Job Information
    // ==========================================
    
    @NotBlank(message = "Job title is required")
    @Size(min = 5, max = 255, message = "Title must be between 5 and 255 characters")
    @Column(nullable = false, length = 255)
    private String title;
    
    @NotBlank(message = "Job description is required")
    @Size(min = 20, max = 5000, message = "Description must be between 20 and 5000 characters")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(length = 100)
    private String category; // Web Development, Mobile, Design, etc.
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobExperienceLevel experienceLevel; // ENTRY_LEVEL, INTERMEDIATE, EXPERT
    
    // ==========================================
    // Budget Information
    // ==========================================
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetType budgetType; // FIXED or HOURLY
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency = Currency.NGN; // Currency for budget (default: Nigerian Naira)
    
    @DecimalMin(value = "0.1", message = "Budget must be greater than 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal budgetAmount;
    
    // ==========================================
    // Job Duration & Deadline
    // ==========================================
    
    @Column(length = 100)
    private String duration; // 1-3 months, 3-6 months, Long term, etc.
    
    @Column
    private LocalDateTime deadline; // Project deadline
    
    // ==========================================
    // Job Status & Visibility
    // ==========================================
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private JobStatus jobStatus = JobStatus.OPEN; // OPEN, IN_PROGRESS, COMPLETED, CANCELLED
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublished = true; // Whether job is visible to freelancers
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isFeatured = false; // Highlighted in search
    
    // ==========================================
    // Job Statistics
    // ==========================================
    
    @Column(nullable = false)
    @Builder.Default
    private Integer viewsCount = 0;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer applicationsCount = 0;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer savesCount = 0;
    
    // ==========================================
    // Freelancer Selected (if any)
    // ==========================================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_freelancer_id")
    private User assignedFreelancer;
    
    @Column
    private LocalDateTime assignedAt;
    
    // ==========================================
    // Attachment
    // ==========================================
    
    @Column(length = 500)
    private String attachmentUrl; // File uploaded by client
    
    // ==========================================
    // Audit Fields
    // ==========================================
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime postedAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column
    private LocalDateTime completedAt;
    
    @Column
    private LocalDateTime cancelledAt;
    
    // ==========================================
    // Helper Methods
    // ==========================================
    
    /**
     * Check if job is open for applications
     */
    public boolean isOpenForApplications() {
        return JobStatus.OPEN.equals(jobStatus) && isPublished;
    }
    
    /**
     * Check if job has been assigned
     */
    public boolean isAssigned() {
        return assignedFreelancer != null;
    }
    
    /**
     * Increment views count
     */
    public void incrementViews() {
        this.viewsCount++;
    }
    
    /**
     * Increment applications count
     */
    public void incrementApplications() {
        this.applicationsCount++;
    }
    
    /**
     * Decrement applications count (if application withdrawn)
     */
    public void decrementApplications() {
        if (this.applicationsCount > 0) {
            this.applicationsCount--;
        }
    }
    
    /**
     * Increment saves count
     */
    public void incrementSaves() {
        this.savesCount++;
    }
    
    /**
     * Decrement saves count (if bookmark removed)
     */
    public void decrementSaves() {
        if (this.savesCount > 0) {
            this.savesCount--;
        }
    }
    
    /**
     * Mark job as completed
     */
    public void markCompleted() {
        this.jobStatus = JobStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
    
    /**
     * Mark job as cancelled
     */
    public void markCancelled() {
        this.jobStatus = JobStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }
    
    /**
     * Mark job as in progress (when freelancer assigned)
     */
    public void markInProgress(User freelancer) {
        this.assignedFreelancer = freelancer;
        this.assignedAt = LocalDateTime.now();
        this.jobStatus = JobStatus.IN_PROGRESS;
    }
    
    /**
     * Get budget as string with currency
     */
    public String getBudgetDisplay() {
        return currency.getSymbol() + budgetAmount;
    }
}
