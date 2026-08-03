package com.findjob.jobboard.service;

import com.findjob.jobboard.model.Job;
import com.findjob.jobboard.model.SavedJob;
import com.findjob.jobboard.model.User;
import com.findjob.jobboard.repository.SavedJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * SavedJobService - Business logic for managing saved jobs
 */
@Service
@Transactional
public class SavedJobService {
    
    @Autowired
    private SavedJobRepository savedJobRepository;
    
    /**
     * Save a job for a freelancer
     */
    public SavedJob saveJob(User freelancer, Job job) {
        if (freelancer == null || job == null) {
            throw new IllegalArgumentException("Freelancer and Job cannot be null");
        }
        
        // Check if already saved
        if (isJobSaved(freelancer, job)) {
            throw new IllegalArgumentException("Job is already saved");
        }
        
        SavedJob savedJob = SavedJob.builder()
                .freelancer(freelancer)
                .job(job)
                .build();
        
        return savedJobRepository.save(savedJob);
    }
    
    /**
     * Unsave a job for a freelancer
     */
    public void unsaveJob(User freelancer, Job job) {
        savedJobRepository.deleteByFreelancerAndJob(freelancer, job);
    }
    
    /**
     * Check if a job is saved by a freelancer
     */
    public boolean isJobSaved(User freelancer, Job job) {
        return savedJobRepository.existsByFreelancerAndJob(freelancer, job);
    }
    
    /**
     * Get all saved jobs for a freelancer
     */
    public List<SavedJob> getSavedJobs(User freelancer) {
        return savedJobRepository.findByFreelancer(freelancer);
    }
    
    /**
     * Get saved jobs for a freelancer (paginated)
     */
    public Page<SavedJob> getSavedJobsPaginated(User freelancer, Pageable pageable) {
        return savedJobRepository.findByFreelancer(freelancer, pageable);
    }
    
    /**
     * Count saved jobs for a freelancer
     */
    public long countSavedJobs(User freelancer) {
        return savedJobRepository.countByFreelancer(freelancer);
    }
    
    /**
     * Get how many freelancers saved a job
     */
    public long countJobSaves(Job job) {
        return savedJobRepository.countByJob(job);
    }
    
    /**
     * Get a saved job record by ID
     */
    public Optional<SavedJob> getSavedJobById(Long id) {
        return savedJobRepository.findById(id);
    }
    
    /**
     * Toggle save status (save if not saved, unsave if saved)
     */
    public boolean toggleSaveJob(User freelancer, Job job) {
        if (isJobSaved(freelancer, job)) {
            unsaveJob(freelancer, job);
            return false; // Unsaved
        } else {
            saveJob(freelancer, job);
            return true; // Saved
        }
    }
}
