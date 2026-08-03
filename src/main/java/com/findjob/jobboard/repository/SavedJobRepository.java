package com.findjob.jobboard.repository;

import com.findjob.jobboard.model.Job;
import com.findjob.jobboard.model.SavedJob;
import com.findjob.jobboard.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    
    /**
     * Check if a job is saved by a freelancer
     */
    boolean existsByFreelancerAndJob(User freelancer, Job job);
    
    /**
     * Get a saved job record
     */
    Optional<SavedJob> findByFreelancerAndJob(User freelancer, Job job);
    
    /**
     * Get all saved jobs for a freelancer
     */
    List<SavedJob> findByFreelancer(User freelancer);
    
    /**
     * Get saved jobs for a freelancer (paginated)
     */
    Page<SavedJob> findByFreelancer(User freelancer, Pageable pageable);
    
    /**
     * Count saved jobs for a freelancer
     */
    long countByFreelancer(User freelancer);
    
    /**
     * Get all freelancers who saved a job
     */
    List<SavedJob> findByJob(Job job);
    
    /**
     * Count how many freelancers saved a job
     */
    long countByJob(Job job);
    
    /**
     * Delete saved job
     */
    void deleteByFreelancerAndJob(User freelancer, Job job);
}
