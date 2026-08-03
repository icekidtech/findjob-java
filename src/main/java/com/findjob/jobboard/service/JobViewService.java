package com.findjob.jobboard.service;

import com.findjob.jobboard.model.Job;
import com.findjob.jobboard.model.JobView;
import com.findjob.jobboard.model.User;
import com.findjob.jobboard.repository.JobViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * JobViewService - Business logic for tracking unique job views
 */
@Service
@Transactional
public class JobViewService {
    
    @Autowired
    private JobViewRepository jobViewRepository;
    
    /**
     * Record a view for a job by a user
     * Returns true if it's a new view, false if user already viewed this job
     */
    public boolean recordView(Job job, User user) {
        if (job == null || user == null) {
            return false;
        }
        
        Optional<JobView> existingView = jobViewRepository.findByJobAndUser(job, user);
        
        if (existingView.isPresent()) {
            // User already viewed this job, just update the view count
            JobView view = existingView.get();
            view.setViewCount(view.getViewCount() + 1);
            jobViewRepository.save(view);
            return false; // Not a new view
        } else {
            // New view from this user
            JobView view = JobView.builder()
                    .job(job)
                    .user(user)
                    .viewCount(1)
                    .build();
            jobViewRepository.save(view);
            return true; // New view recorded
        }
    }
    
    /**
     * Check if a user has viewed a job
     */
    public boolean hasUserViewedJob(Job job, User user) {
        return jobViewRepository.existsByJobAndUser(job, user);
    }
    
    /**
     * Get unique view count for a job
     */
    public long getUniqueViewCount(Job job) {
        return jobViewRepository.countByJob(job);
    }
    
    /**
     * Get total jobs viewed by a user
     */
    public long getTotalJobsViewedByUser(User user) {
        return jobViewRepository.countByUser(user);
    }
}
