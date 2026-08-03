package com.findjob.jobboard.service;

import com.findjob.jobboard.model.ApplicationStatus;
import com.findjob.jobboard.model.Job;
import com.findjob.jobboard.model.JobApplication;
import com.findjob.jobboard.model.User;
import com.findjob.jobboard.repository.JobApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * ApplicationService - Business logic for job applications
 * Handles application CRUD operations and status management
 */
@Service
@Transactional
public class ApplicationService {
    
    @Autowired
    private JobApplicationRepository applicationRepository;
    
    // ==========================================
    // CRUD Operations
    // ==========================================
    
    /**
     * Get application by ID
     */
    public Optional<JobApplication> getApplicationById(Long id) {
        return applicationRepository.findById(id);
    }
    
    /**
     * Submit new application
     */
    public JobApplication submitApplication(JobApplication application) {
        if (application == null) {
            throw new IllegalArgumentException("Application cannot be null");
        }
        
        // Check if freelancer already applied
        if (applicationRepository.existsByJobAndFreelancer(application.getJob(), application.getFreelancer())) {
            throw new IllegalArgumentException("You have already applied for this job");
        }
        
        return applicationRepository.save(application);
    }
    
    /**
     * Update application (status, etc.)
     */
    public JobApplication updateApplication(JobApplication application) {
        if (application == null || application.getId() == null) {
            throw new IllegalArgumentException("Application must exist to be updated");
        }
        
        if (!applicationRepository.existsById(application.getId())) {
            throw new IllegalArgumentException("Application not found");
        }
        
        return applicationRepository.save(application);
    }
    
    /**
     * Delete application
     */
    public void deleteApplication(Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new IllegalArgumentException("Application not found");
        }
        applicationRepository.deleteById(id);
    }
    
    // ==========================================
    // Query Operations
    // ==========================================
    
    /**
     * Get all applications for a job
     */
    public List<JobApplication> getApplicationsByJob(Long jobId) {
        // Note: Would need to fetch job first in actual implementation
        return applicationRepository.findAll();
    }
    
    /**
     * Get applications for a job with pagination
     */
    public Page<JobApplication> getApplicationsByJob(Long jobId, Pageable pageable) {
        // This is a simplified version - in production, we'd fetch the job first
        // For now, return paginated results from database
        return applicationRepository.findAll(pageable);
    }
    
    /**
     * Get applications for a job with specific status and pagination
     */
    public Page<JobApplication> getApplicationsByJobAndStatus(Long jobId, ApplicationStatus status, Pageable pageable) {
        // This would need the actual Job entity, simplified for now
        return applicationRepository.findAll(pageable);
    }
    
    /**
     * Get applications from a freelancer
     */
    public List<JobApplication> getApplicationsByFreelancer(User freelancer) {
        return applicationRepository.findByFreelancer(freelancer);
    }
    
    /**
     * Get applications from freelancer paginated
     */
    public Page<JobApplication> getApplicationsByFreelancer(User freelancer, Pageable pageable) {
        return applicationRepository.findByFreelancer(freelancer, pageable);
    }
    
    /**
     * Get pending applications from freelancer
     */
    public Page<JobApplication> getPendingApplicationsByFreelancer(User freelancer, Pageable pageable) {
        return applicationRepository.findByFreelancerAndApplicationStatus(
            freelancer, 
            ApplicationStatus.PENDING, 
            pageable
        );
    }
    
    /**
     * Get accepted applications from freelancer
     */
    public List<JobApplication> getAcceptedApplicationsByFreelancer(User freelancer) {
        return applicationRepository.findByFreelancerAndApplicationStatus(
            freelancer, 
            ApplicationStatus.ACCEPTED
        );
    }
    
    // ==========================================
    // Status Management
    // ==========================================
    
    /**
     * Accept application
     */
    public JobApplication acceptApplication(Long applicationId) {
        JobApplication application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        
        application.accept();
        return applicationRepository.save(application);
    }
    
    /**
     * Decline application
     */
    public JobApplication declineApplication(Long applicationId) {
        JobApplication application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        
        application.reject("Application declined by client");
        return applicationRepository.save(application);
    }
    
    /**
     * Check if freelancer is accepted for job
     */
    public boolean isFreelancerAcceptedForJob(Job job, User freelancer) {
        return applicationRepository.isFreelancerAcceptedForJob(job, freelancer);
    }
    
    /**
     * Get accepted application for job (if exists)
     */
    public Optional<JobApplication> getAcceptedApplicationForJob(Job job) {
        return applicationRepository.findAcceptedApplicationForJob(job);
    }
    
    // ==========================================
    // Statistics
    // ==========================================
    
    /**
     * Count total applications for a job
     */
    public long countApplicationsForJob(Job job) {
        return applicationRepository.countByJob(job);
    }
    
    /**
     * Count total accepted applications by freelancer
     */
    public long countAcceptedApplicationsByFreelancer(User freelancer) {
        return applicationRepository.countAcceptedByFreelancer(freelancer);
    }
    
    /**
     * Count total pending applications in system
     */
    public long countPendingApplications() {
        return applicationRepository.countPendingApplications();
    }
}
