package com.findjob.jobboard.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * SavedJob Entity - Represents a job saved/bookmarked by a freelancer
 * Allows freelancers to save jobs for later review
 */
@Entity
@Table(name = "saved_jobs", indexes = {
    @Index(name = "idx_savedjob_freelancer_id", columnList = "freelancer_id"),
    @Index(name = "idx_savedjob_job_id", columnList = "job_id"),
    @Index(name = "idx_savedjob_saved_at", columnList = "saved_at"),
    @Index(name = "idx_savedjob_freelancer_job", columnList = "freelancer_id, job_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedJob {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ==========================================
    // Relationships
    // ==========================================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "freelancer_id", nullable = false)
    private User freelancer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
    
    // ==========================================
    // Metadata
    // ==========================================
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime savedAt;
    
    @Column(length = 500)
    private String notes; // Optional notes about why saved
}
