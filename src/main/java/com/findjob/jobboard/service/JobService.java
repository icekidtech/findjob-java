package com.findjob.jobboard.service;

import com.findjob.jobboard.model.*;
import com.findjob.jobboard.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * JobService - Business logic for job management
 */
@Service
@Transactional
public class JobService {
    
    @Autowired
    private JobRepository jobRepository;
    
    // ==========================================
    // Job CRUD Operations
    // ==========================================
    
    /**
     * Create new job posting
     */
    public Job createJob(Job job) {
        if (job.getClient() == null) {
            throw new IllegalArgumentException("Job must have a client");
        }
        return jobRepository.save(job);
    }
    
    /**
     * Get job by ID
     */
    public Job getJobById(Long id) {
        return jobRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Job not found with id: " + id));
    }
    
    /**
     * Update job
     */
    public Job updateJob(Job job) {
        if (!jobRepository.existsById(job.getId())) {
            throw new IllegalArgumentException("Job not found");
        }
        return jobRepository.save(job);
    }
    
    /**
     * Delete job (soft delete - mark as cancelled)
     */
    public void deleteJob(Long id) {
        Job job = getJobById(id);
        job.markCancelled();
        jobRepository.save(job);
    }
    
    /**
     * Get all jobs
     */
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }
    
    // ==========================================
    // Job Filtering & Search
    // ==========================================
    
    /**
     * Get jobs by client
     */
    public List<Job> getJobsByClient(User client) {
        return jobRepository.findByClient(client);
    }
    
    /**
     * Get jobs by client paginated
     */
    public Page<Job> getJobsByClientPaginated(User client, Pageable pageable) {
        return jobRepository.findByClient(client, pageable);
    }
    
    /**
     * Get open jobs
     */
    public List<Job> getOpenJobs() {
        return jobRepository.findByJobStatusAndIsPublishedTrue(JobStatus.OPEN);
    }
    
    /**
     * Get open jobs paginated
     */
    public Page<Job> getOpenJobsPaginated(Pageable pageable) {
        return jobRepository.findByJobStatusAndIsPublishedTrue(JobStatus.OPEN, pageable);
    }
    
    /**
     * Get jobs by category
     */
    public List<Job> getJobsByCategory(String category) {
        return jobRepository.findByCategory(category);
    }
    
    /**
     * Get jobs by category paginated
     */
    public Page<Job> getJobsByCategoryPaginated(String category, Pageable pageable) {
        return jobRepository.findByCategory(category, pageable);
    }
    
    /**
     * Search jobs
     */
    public Page<Job> searchJobs(String query, Pageable pageable) {
        return jobRepository.searchByTitleOrDescription(query, pageable);
    }
    
    /**
     * Get jobs within budget range
     */
    public List<Job> getJobsByBudgetRange(BigDecimal minBudget, BigDecimal maxBudget) {
        return jobRepository.findByBudgetRange(minBudget, maxBudget);
    }
    
    /**
     * Get recently posted jobs
     */
    public Page<Job> getRecentJobs(Pageable pageable) {
        return jobRepository.findRecentJobs(pageable);
    }
    
    /**
     * Get popular jobs
     */
    public Page<Job> getPopularJobs(Pageable pageable) {
        return jobRepository.findPopularJobs(pageable);
    }
    
    /**
     * Get featured jobs
     */
    public Page<Job> getFeaturedJobs(Pageable pageable) {
        return jobRepository.findFeaturedJobs(pageable);
    }
    
    /**
     * Get least competitive jobs
     */
    public Page<Job> getLeastCompetitiveJobs(Pageable pageable) {
        return jobRepository.findLeastCompetitiveJobs(pageable);
    }
    
    // ==========================================
    // Job Management
    // ==========================================
    
    /**
     * Assign freelancer to job
     */
    public Job assignFreelancer(Long jobId, User freelancer) {
        Job job = getJobById(jobId);
        job.markInProgress(freelancer);
        return jobRepository.save(job);
    }
    
    /**
     * Complete job
     */
    public Job completeJob(Long jobId) {
        Job job = getJobById(jobId);
        job.markCompleted();
        return jobRepository.save(job);
    }
    
    /**
     * Cancel job
     */
    public Job cancelJob(Long jobId) {
        Job job = getJobById(jobId);
        job.markCancelled();
        return jobRepository.save(job);
    }
    
    /**
     * Publish job
     */
    public Job publishJob(Long jobId) {
        Job job = getJobById(jobId);
        job.setIsPublished(true);
        return jobRepository.save(job);
    }
    
    /**
     * Unpublish job
     */
    public Job unpublishJob(Long jobId) {
        Job job = getJobById(jobId);
        job.setIsPublished(false);
        return jobRepository.save(job);
    }
    
    /**
     * Feature job
     */
    public Job featureJob(Long jobId) {
        Job job = getJobById(jobId);
        job.setIsFeatured(true);
        return jobRepository.save(job);
    }
    
    /**
     * Unfeature job
     */
    public Job unfeatureJob(Long jobId) {
        Job job = getJobById(jobId);
        job.setIsFeatured(false);
        return jobRepository.save(job);
    }
    
    // ==========================================
    // Statistics & Counters
    // ==========================================
    
    /**
     * Increment views count
     */
    public void incrementViews(Long jobId) {
        Job job = getJobById(jobId);
        job.incrementViews();
        jobRepository.save(job);
    }
    
    /**
     * Get jobs assigned to freelancer
     */
    public List<Job> getAssignedJobs(User freelancer) {
        return jobRepository.findAssignedJobs(freelancer);
    }
    
    /**
     * Get active contracts for freelancer
     */
    public List<Job> getActiveContracts(User freelancer) {
        return jobRepository.findActiveContracts(freelancer);
    }
    
    /**
     * Get completed contracts for freelancer
     */
    public List<Job> getCompletedContracts(User freelancer) {
        return jobRepository.findCompletedContracts(freelancer);
    }
    
    /**
     * Count open jobs
     */
    public long countOpenJobs() {
        return jobRepository.countOpenJobs();
    }
    
    /**
     * Count jobs by client
     */
    public long countJobsByClient(User client) {
        return jobRepository.countByClient(client);
    }
    
    /**
     * Count completed jobs
     */
    public long countCompletedJobs() {
        return jobRepository.countCompletedJobs();
    }
    
    /**
     * Count jobs in category
     */
    public long countJobsByCategory(String category) {
        return jobRepository.countByCategory(category);
    }
}
