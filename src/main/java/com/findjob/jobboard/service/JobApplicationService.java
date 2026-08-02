package com.findjob.jobboard.service;

import com.findjob.jobboard.model.*;
import com.findjob.jobboard.repository.JobApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JobApplicationService - Business logic for job applications/bids
 */
@Service
@Transactional
public class JobApplicationService {
    
    @Autowired
    private JobApplicationRepository jobApplicationRepository;
    
    @Autowired
    private JobService jobService;
    
    // ==========================================
    // Application CRUD Operations
    // ==========================================
    
    /**
     * Submit job application
     */
    public JobApplication submitApplication(JobApplication application) {
        // Check if freelancer already applied
        if (jobApplicationRepository.existsByJobAndFreelancer(application.getJob(), application.getFreelancer())) {
            throw new IllegalArgumentException("You have already applied to this job");
        }
        
        // Check if job is open for applications
        if (!application.getJob().isOpenForApplications()) {
            throw new IllegalArgumentException("This job is no longer accepting applications");
        }
        
        JobApplication saved = jobApplicationRepository.save(application);
        
        // Increment applications count on job
        Job job = application.getJob();
        job.incrementApplications();
        jobService.updateJob(job);
        
        return saved;
    }
    
    /**
     * Get application by ID
     */
    public JobApplication getApplicationById(Long id) {
        return jobApplicationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Application not found with id: " + id));
    }
    
    /**
     * Get application between job and freelancer
     */
    public Optional<JobApplication> getApplication(Job job, User freelancer) {
        return jobApplicationRepository.findByJobAndFreelancer(job, freelancer);
    }
    
    /**
     * Update application
     */
    public JobApplication updateApplication(JobApplication application) {
        if (!jobApplicationRepository.existsById(application.getId())) {
            throw new IllegalArgumentException("Application not found");
        }
        return jobApplicationRepository.save(application);
    }
    
    /**
     * Delete application (hard delete)
     */
    public void deleteApplication(Long id) {
        jobApplicationRepository.deleteById(id);
    }
    
    /**
     * Get all applications
     */
    public List<JobApplication> getAllApplications() {
        return jobApplicationRepository.findAll();
    }
    
    // ==========================================
    // Application Filtering & Search
    // ==========================================
    
    /**
     * Get applications for a job
     */
    public List<JobApplication> getApplicationsForJob(Job job) {
        return jobApplicationRepository.findByJob(job);
    }
    
    /**
     * Get applications for a job paginated
     */
    public Page<JobApplication> getApplicationsForJobPaginated(Job job, Pageable pageable) {
        return jobApplicationRepository.findByJob(job, pageable);
    }
    
    /**
     * Get applications from freelancer
     */
    public List<JobApplication> getApplicationsByFreelancer(User freelancer) {
        return jobApplicationRepository.findByFreelancer(freelancer);
    }
    
    /**
     * Get applications from freelancer paginated
     */
    public Page<JobApplication> getApplicationsByFreelancerPaginated(User freelancer, Pageable pageable) {
        return jobApplicationRepository.findByFreelancer(freelancer, pageable);
    }
    
    /**
     * Get pending applications for a job
     */
    public List<JobApplication> getPendingApplicationsForJob(Job job) {
        return jobApplicationRepository.findByJobAndApplicationStatus(job, ApplicationStatus.PENDING);
    }
    
    /**
     * Get pending applications for freelancer
     */
    public List<JobApplication> getPendingApplicationsByFreelancer(User freelancer) {
        return jobApplicationRepository.findByFreelancerAndApplicationStatus(freelancer, ApplicationStatus.PENDING);
    }
    
    /**
     * Get pending applications for freelancer paginated
     */
    public Page<JobApplication> getPendingApplicationsByFreelancerPaginated(User freelancer, Pageable pageable) {
        return jobApplicationRepository.findByFreelancerAndApplicationStatus(freelancer, ApplicationStatus.PENDING, pageable);
    }
    
    /**
     * Get accepted applications
     */
    public List<JobApplication> getAcceptedApplications() {
        return jobApplicationRepository.findAcceptedApplications();
    }
    
    /**
     * Get rejected applications
     */
    public List<JobApplication> getRejectedApplications() {
        return jobApplicationRepository.findRejectedApplications();
    }
    
    /**
     * Get applications ordered by freelancer rating
     */
    public List<JobApplication> getApplicationsOrderedByRating(Job job) {
        return jobApplicationRepository.findApplicationsOrderedByFreelancerRating(job);
    }
    
    // ==========================================
    // Application Status Management
    // ==========================================
    
    /**
     * Accept application (client action)
     */
    public JobApplication acceptApplication(Long applicationId) {
        JobApplication application = getApplicationById(applicationId);
        
        if (!application.isPending()) {
            throw new IllegalArgumentException("Only pending applications can be accepted");
        }
        
        // Accept the application
        application.accept();
        JobApplication saved = jobApplicationRepository.save(application);
        
        // Assign freelancer to job
        Job job = application.getJob();
        jobService.assignFreelancer(job.getId(), application.getFreelancer());
        
        return saved;
    }
    
    /**
     * Reject application (client action)
     */
    public JobApplication rejectApplication(Long applicationId, String feedback) {
        JobApplication application = getApplicationById(applicationId);
        
        if (!application.isPending()) {
            throw new IllegalArgumentException("Only pending applications can be rejected");
        }
        
        application.reject(feedback);
        return jobApplicationRepository.save(application);
    }
    
    /**
     * Withdraw application (freelancer action)
     */
    public JobApplication withdrawApplication(Long applicationId) {
        JobApplication application = getApplicationById(applicationId);
        
        if (!application.isPending()) {
            throw new IllegalArgumentException("Only pending applications can be withdrawn");
        }
        
        application.withdraw();
        JobApplication saved = jobApplicationRepository.save(application);
        
        // Decrement applications count on job
        Job job = application.getJob();
        job.decrementApplications();
        jobService.updateJob(job);
        
        return saved;
    }
    
    // ==========================================
    // Statistics & Counters
    // ==========================================
    
    /**
     * Count applications for job
     */
    public long countApplicationsForJob(Job job) {
        return jobApplicationRepository.countByJob(job);
    }
    
    /**
     * Count applications from freelancer
     */
    public long countApplicationsByFreelancer(User freelancer) {
        return jobApplicationRepository.countByFreelancer(freelancer);
    }
    
    /**
     * Count accepted applications by freelancer
     */
    public long countAcceptedApplicationsByFreelancer(User freelancer) {
        return jobApplicationRepository.countAcceptedByFreelancer(freelancer);
    }
    
    /**
     * Count total pending applications
     */
    public long countPendingApplications() {
        return jobApplicationRepository.countPendingApplications();
    }
    
    /**
     * Check if freelancer has pending applications
     */
    public boolean hasPendingApplications(User freelancer) {
        return countAcceptedApplicationsByFreelancer(freelancer) > 0;
    }
    
    /**
     * Get accepted application for job (if exists)
     */
    public Optional<JobApplication> getAcceptedApplicationForJob(Job job) {
        return jobApplicationRepository.findAcceptedApplicationForJob(job);
    }
    
    /**
     * Check if freelancer is hired for job
     */
    public boolean isFreelancerAcceptedForJob(Job job, User freelancer) {
        return jobApplicationRepository.isFreelancerAcceptedForJob(job, freelancer);
    }
    
    /**
     * Get recent applications since date
     */
    public List<JobApplication> getRecentApplications(LocalDateTime since) {
        return jobApplicationRepository.findRecentApplications(since);
    }
    
    /**
     * Get applications by date range
     */
    public List<JobApplication> getApplicationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return jobApplicationRepository.findApplicationsByDateRange(startDate, endDate);
    }
}
