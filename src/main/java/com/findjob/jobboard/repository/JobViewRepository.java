package com.findjob.jobboard.repository;

import com.findjob.jobboard.model.Job;
import com.findjob.jobboard.model.JobView;
import com.findjob.jobboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobViewRepository extends JpaRepository<JobView, Long> {
    
    /**
     * Check if a user has viewed a job
     */
    boolean existsByJobAndUser(Job job, User user);
    
    /**
     * Get a job view record
     */
    Optional<JobView> findByJobAndUser(Job job, User user);
    
    /**
     * Count unique viewers of a job
     */
    long countByJob(Job job);
    
    /**
     * Count total jobs viewed by a user
     */
    long countByUser(User user);
}
