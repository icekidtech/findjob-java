package com.findjob.jobboard.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * JobView Entity - Tracks unique views of a job by users
 * Prevents counting the same user viewing the same job multiple times
 */
@Entity
@Table(name = "job_views", indexes = {
    @Index(name = "idx_job_id", columnList = "job_id"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_viewed_at", columnList = "viewed_at"),
    @Index(name = "idx_job_user", columnList = "job_id, user_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobView {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ==========================================
    // Relationships
    // ==========================================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // ==========================================
    // Metadata
    // ==========================================
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime viewedAt;
    
    @Column
    private Integer viewCount = 1; // Number of times this user viewed this job
}
