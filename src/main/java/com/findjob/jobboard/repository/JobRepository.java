package com.findjob.jobboard.repository;

import com.findjob.jobboard.model.Job;
import com.findjob.jobboard.model.JobStatus;
import com.findjob.jobboard.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JobRepository - Data access layer for Job entity
 */
@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    
    // ==========================================
    // Basic Queries
    // ==========================================
    
    /**
     * Find jobs by client
     */
    List<Job> findByClient(User client);
    
    /**
     * Find jobs by client paginated
     */
    Page<Job> findByClient(User client, Pageable pageable);
    
    /**
     * Find job by ID with optional
     */
    Optional<Job> findById(Long id);
    
    // ==========================================
    // Job Status Queries
    // ==========================================
    
    /**
     * Find open jobs
     */
    List<Job> findByJobStatus(JobStatus status);
    
    /**
     * Find published and open jobs
     */
    List<Job> findByJobStatusAndIsPublishedTrue(JobStatus status);
    
    /**
     * Find jobs by status paginated
     */
    Page<Job> findByJobStatusAndIsPublishedTrue(JobStatus status, Pageable pageable);
    
    /**
     * Find jobs by client and status
     */
    List<Job> findByClientAndJobStatus(User client, JobStatus status);
    
    // ==========================================
    // Search & Filter Queries
    // ==========================================
    
    /**
     * Search jobs by title or description
     */
    @Query("SELECT j FROM Job j WHERE j.isPublished = true AND (j.title LIKE %:query% OR j.description LIKE %:query%) ORDER BY j.postedAt DESC")
    Page<Job> searchByTitleOrDescription(@Param("query") String query, Pageable pageable);
    
    /**
     * Find jobs by category
     */
    List<Job> findByCategory(String category);
    
    /**
     * Find jobs by category paginated
     */
    Page<Job> findByCategory(String category, Pageable pageable);
    
    /**
     * Find jobs by experience level
     */
    @Query("SELECT j FROM Job j WHERE j.isPublished = true AND j.jobStatus = 'OPEN' AND j.experienceLevel = :level ORDER BY j.postedAt DESC")
    List<Job> findByExperienceLevel(@Param("level") String level);
    
    /**
     * Find jobs within budget range
     */
    @Query("SELECT j FROM Job j WHERE j.isPublished = true AND j.jobStatus = 'OPEN' AND j.budgetAmount BETWEEN :minBudget AND :maxBudget")
    List<Job> findByBudgetRange(@Param("minBudget") BigDecimal minBudget, @Param("maxBudget") BigDecimal maxBudget);
    
    /**
     * Find jobs posted after a certain date
     */
    List<Job> findByPostedAtAfter(LocalDateTime date);
    
    /**
     * Find featured jobs
     */
    @Query("SELECT j FROM Job j WHERE j.isPublished = true AND j.isFeatured = true AND j.jobStatus = 'OPEN' ORDER BY j.postedAt DESC")
    Page<Job> findFeaturedJobs(Pageable pageable);
    
    // ==========================================
    // Sorting & Ranking Queries
    // ==========================================
    
    /**
     * Find popular jobs by views count
     */
    @Query("SELECT j FROM Job j WHERE j.isPublished = true AND j.jobStatus = 'OPEN' ORDER BY j.viewsCount DESC")
    Page<Job> findPopularJobs(Pageable pageable);
    
    /**
     * Find recently posted jobs
     */
    @Query("SELECT j FROM Job j WHERE j.isPublished = true AND j.jobStatus = 'OPEN' ORDER BY j.postedAt DESC")
    Page<Job> findRecentJobs(Pageable pageable);
    
    /**
     * Find jobs with fewest applications (less competitive)
     */
    @Query("SELECT j FROM Job j WHERE j.isPublished = true AND j.jobStatus = 'OPEN' ORDER BY j.applicationsCount ASC")
    Page<Job> findLeastCompetitiveJobs(Pageable pageable);
    
    // ==========================================
    // Count & Statistics
    // ==========================================
    
    /**
     * Count open jobs
     */
    @Query("SELECT COUNT(j) FROM Job j WHERE j.jobStatus = 'OPEN' AND j.isPublished = true")
    long countOpenJobs();
    
    /**
     * Count jobs by client
     */
    long countByClient(User client);
    
    /**
     * Count completed jobs
     */
    @Query("SELECT COUNT(j) FROM Job j WHERE j.jobStatus = 'COMPLETED'")
    long countCompletedJobs();
    
    /**
     * Count jobs by category
     */
    long countByCategory(String category);
    
    // ==========================================
    // Assignment Queries
    // ==========================================
    
    /**
     * Find jobs assigned to freelancer
     */
    @Query("SELECT j FROM Job j WHERE j.assignedFreelancer = :freelancer AND j.jobStatus IN ('IN_PROGRESS', 'COMPLETED')")
    List<Job> findAssignedJobs(@Param("freelancer") User freelancer);
    
    /**
     * Find active contracts (assigned jobs in progress)
     */
    @Query("SELECT j FROM Job j WHERE j.assignedFreelancer = :freelancer AND j.jobStatus = 'IN_PROGRESS'")
    List<Job> findActiveContracts(@Param("freelancer") User freelancer);
    
    /**
     * Find completed contracts
     */
    @Query("SELECT j FROM Job j WHERE j.assignedFreelancer = :freelancer AND j.jobStatus = 'COMPLETED'")
    List<Job> findCompletedContracts(@Param("freelancer") User freelancer);
}
