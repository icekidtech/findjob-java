package com.findjob.jobboard.repository;

import com.findjob.jobboard.model.ApplicationStatus;
import com.findjob.jobboard.model.Job;
import com.findjob.jobboard.model.JobApplication;
import com.findjob.jobboard.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JobApplicationRepository - Data access layer for JobApplication entity
 */
@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    
    // ==========================================
    // Basic Queries
    // ==========================================
    
    /**
     * Find applications for a job
     */
    List<JobApplication> findByJob(Job job);
    
    /**
     * Find applications for a job paginated
     */
    Page<JobApplication> findByJob(Job job, Pageable pageable);
    
    /**
     * Find applications from a freelancer
     */
    List<JobApplication> findByFreelancer(User freelancer);
    
    /**
     * Find applications from freelancer paginated
     */
    Page<JobApplication> findByFreelancer(User freelancer, Pageable pageable);
    
    /**
     * Find specific application
     */
    Optional<JobApplication> findByJobAndFreelancer(Job job, User freelancer);
    
    /**
     * Check if freelancer already applied to job
     */
    boolean existsByJobAndFreelancer(Job job, User freelancer);
    
    // ==========================================
    // Status Queries
    // ==========================================
    
    /**
     * Find pending applications for a job
     */
    List<JobApplication> findByJobAndApplicationStatus(Job job, ApplicationStatus status);
    
    /**
     * Find pending applications for freelancer
     */
    List<JobApplication> findByFreelancerAndApplicationStatus(User freelancer, ApplicationStatus status);
    
    /**
     * Find accepted applications
     */
    @Query("SELECT a FROM JobApplication a WHERE a.applicationStatus = 'ACCEPTED'")
    List<JobApplication> findAcceptedApplications();
    
    /**
     * Find rejected applications
     */
    @Query("SELECT a FROM JobApplication a WHERE a.applicationStatus = 'REJECTED'")
    List<JobApplication> findRejectedApplications();
    
    /**
     * Find pending applications for freelancer paginated
     */
    Page<JobApplication> findByFreelancerAndApplicationStatus(User freelancer, ApplicationStatus status, Pageable pageable);
    
    // ==========================================
    // Statistics Queries
    // ==========================================
    
    /**
     * Count applications for a job
     */
    long countByJob(Job job);
    
    /**
     * Count applications from freelancer
     */
    long countByFreelancer(User freelancer);
    
    /**
     * Count accepted applications by freelancer
     */
    @Query("SELECT COUNT(a) FROM JobApplication a WHERE a.freelancer = :freelancer AND a.applicationStatus = 'ACCEPTED'")
    long countAcceptedByFreelancer(@Param("freelancer") User freelancer);
    
    /**
     * Count total pending applications
     */
    @Query("SELECT COUNT(a) FROM JobApplication a WHERE a.applicationStatus = 'PENDING'")
    long countPendingApplications();
    
    // ==========================================
    // Time-based Queries
    // ==========================================
    
    /**
     * Find recent applications
     */
    @Query("SELECT a FROM JobApplication a WHERE a.appliedAt > :since ORDER BY a.appliedAt DESC")
    List<JobApplication> findRecentApplications(@Param("since") LocalDateTime since);
    
    /**
     * Find applications applied within time range
     */
    @Query("SELECT a FROM JobApplication a WHERE a.appliedAt BETWEEN :startDate AND :endDate ORDER BY a.appliedAt DESC")
    List<JobApplication> findApplicationsByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Find applications for job ordered by rating (if freelancer is highly rated)
     */
    @Query("SELECT a FROM JobApplication a WHERE a.job = :job ORDER BY a.freelancer.reputationScore DESC")
    List<JobApplication> findApplicationsOrderedByFreelancerRating(@Param("job") Job job);
    
    // ==========================================
    // Specialized Queries
    // ==========================================
    
    /**
     * Check if freelancer has been accepted for this job
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM JobApplication a WHERE a.job = :job AND a.freelancer = :freelancer AND a.applicationStatus = 'ACCEPTED'")
    boolean isFreelancerAcceptedForJob(@Param("job") Job job, @Param("freelancer") User freelancer);
    
    /**
     * Get the accepted application for a job (if any)
     */
    @Query("SELECT a FROM JobApplication a WHERE a.job = :job AND a.applicationStatus = 'ACCEPTED'")
    Optional<JobApplication> findAcceptedApplicationForJob(@Param("job") Job job);
}
