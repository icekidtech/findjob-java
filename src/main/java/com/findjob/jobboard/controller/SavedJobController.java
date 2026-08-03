package com.findjob.jobboard.controller;

import com.findjob.jobboard.model.Job;
import com.findjob.jobboard.model.User;
import com.findjob.jobboard.service.JobService;
import com.findjob.jobboard.service.SavedJobService;
import com.findjob.jobboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * SavedJobController - Handles saving/bookmarking jobs
 */
@Controller
@RequestMapping("/saved-jobs")
public class SavedJobController {
    
    @Autowired
    private SavedJobService savedJobService;
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private UserService userService;
    
    /**
     * View all saved jobs for current user
     */
    @GetMapping
    public String viewSavedJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Authentication authentication,
            Model model) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            User freelancer = userService.findByEmail(authentication.getName());
            
            if (freelancer == null || !freelancer.isFreelancer()) {
                model.addAttribute("error", "Only freelancers can view saved jobs");
                return "redirect:/dashboard";
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<?> savedJobsPage = savedJobService.getSavedJobsPaginated(freelancer, pageable);
            
            model.addAttribute("title", "Saved Jobs");
            model.addAttribute("savedJobs", savedJobsPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", savedJobsPage.getTotalPages());
            model.addAttribute("totalItems", savedJobsPage.getTotalElements());
            
            return "jobs/saved";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error loading saved jobs: " + e.getMessage());
            return "error/error";
        }
    }
    
    /**
     * Save a job (AJAX endpoint returns JSON)
     */
    @PostMapping("/{jobId}/save")
    @ResponseBody
    public ResponseEntity<?> saveJob(
            @PathVariable Long jobId,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(new HashMap<String, String>() {{
                put("error", "Please login first");
            }});
        }
        
        try {
            User freelancer = userService.findByEmail(authentication.getName());
            Job job = jobService.getJobById(jobId);
            
            if (!freelancer.isFreelancer()) {
                return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                    put("error", "Only freelancers can save jobs");
                }});
            }
            
            boolean isSaved = savedJobService.toggleSaveJob(freelancer, job);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("saved", isSaved);
            response.put("message", isSaved ? "Job saved successfully" : "Job removed from saved");
            response.put("saveCount", savedJobService.countSavedJobs(freelancer));
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new HashMap<String, String>() {{
                put("error", "Failed to save job: " + e.getMessage());
            }});
        }
    }
    
    /**
     * Check if a job is saved
     */
    @GetMapping("/{jobId}/check")
    @ResponseBody
    public ResponseEntity<?> checkJobSaved(
            @PathVariable Long jobId,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("saved", false);
            }});
        }
        
        try {
            User freelancer = userService.findByEmail(authentication.getName());
            Job job = jobService.getJobById(jobId);
            
            boolean isSaved = savedJobService.isJobSaved(freelancer, job);
            
            Map<String, Object> response = new HashMap<>();
            response.put("saved", isSaved);
            response.put("count", savedJobService.countSavedJobs(freelancer));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("saved", false);
                put("error", e.getMessage());
            }});
        }
    }
}
