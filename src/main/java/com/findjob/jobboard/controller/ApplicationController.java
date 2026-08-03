package com.findjob.jobboard.controller;

import com.findjob.jobboard.model.ApplicationStatus;
import com.findjob.jobboard.model.Job;
import com.findjob.jobboard.model.JobApplication;
import com.findjob.jobboard.model.User;
import com.findjob.jobboard.service.ApplicationService;
import com.findjob.jobboard.service.JobService;
import com.findjob.jobboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * ApplicationController - Handles job applications management
 * Allows clients to view, accept, and decline applications
 */
@Controller
@RequestMapping("/applications")
public class ApplicationController {
    
    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private UserService userService;
    
    /**
     * View all applications for a specific job
     * GET /applications/job/{jobId}
     */
    @GetMapping("/job/{jobId}")
    public String viewJobApplications(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            Authentication authentication,
            Model model) {
        
        try {
            // Verify user is authenticated and is a client
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/auth/login";
            }
            
            User currentUser = userService.findByEmail(authentication.getName());
            if (currentUser == null || !currentUser.isClient()) {
                return "redirect:/dashboard";
            }
            
            // Get job and verify ownership
            Job job = jobService.getJobById(jobId);
            
            if (job == null || !job.getClient().getId().equals(currentUser.getId())) {
                model.addAttribute("error", "You don't have permission to view these applications");
                return "error/403";
            }
            
            // Get applications with optional status filter
            Page<JobApplication> applications;
            Pageable pageable = PageRequest.of(page, size);
            
            if (status != null && !status.isBlank()) {
                try {
                    ApplicationStatus appStatus = ApplicationStatus.valueOf(status.toUpperCase());
                    applications = applicationService.getApplicationsByJobAndStatus(jobId, appStatus, pageable);
                } catch (IllegalArgumentException e) {
                    applications = applicationService.getApplicationsByJob(jobId, pageable);
                }
            } else {
                applications = applicationService.getApplicationsByJob(jobId, pageable);
            }
            
            model.addAttribute("job", job);
            model.addAttribute("applications", applications);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", applications.getTotalPages());
            model.addAttribute("totalApplications", applications.getTotalElements());
            model.addAttribute("statusFilter", status);
            model.addAttribute("title", job.getTitle() + " - Applications");
            
            return "applications/job-applications";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error loading applications: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * View single application details
     * GET /applications/{applicationId}
     */
    @GetMapping("/{applicationId}")
    public String viewApplicationDetail(
            @PathVariable Long applicationId,
            Authentication authentication,
            Model model) {
        
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/auth/login";
            }
            
            User currentUser = userService.findByEmail(authentication.getName());
            if (currentUser == null) {
                return "redirect:/auth/login";
            }
            
            // Get application
            JobApplication application = applicationService.getApplicationById(applicationId)
                    .orElse(null);
            
            if (application == null) {
                model.addAttribute("error", "Application not found");
                return "error/404";
            }
            
            // Verify permission (client who owns the job or the freelancer who applied)
            boolean isClientOwner = application.getJob().getClient().getId().equals(currentUser.getId());
            boolean isFreelancerApplicant = application.getFreelancer().getId().equals(currentUser.getId());
            
            if (!isClientOwner && !isFreelancerApplicant) {
                model.addAttribute("error", "You don't have permission to view this application");
                return "error/403";
            }
            
            model.addAttribute("application", application);
            model.addAttribute("isClientOwner", isClientOwner);
            model.addAttribute("isFreelancerApplicant", isFreelancerApplicant);
            model.addAttribute("title", "Application Details");
            
            return "applications/view-application";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error loading application: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Accept application
     * POST /applications/{applicationId}/accept
     */
    @PostMapping("/{applicationId}/accept")
    public String acceptApplication(
            @PathVariable Long applicationId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/auth/login";
            }
            
            User currentUser = userService.findByEmail(authentication.getName());
            if (currentUser == null || !currentUser.isClient()) {
                redirectAttributes.addFlashAttribute("error", "Only clients can accept applications");
                return "redirect:/dashboard";
            }
            
            // Get application and verify ownership
            JobApplication application = applicationService.getApplicationById(applicationId)
                    .orElse(null);
            
            if (application == null) {
                redirectAttributes.addFlashAttribute("error", "Application not found");
                return "redirect:/applications";
            }
            
            if (!application.getJob().getClient().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("error", "You don't have permission to accept this application");
                return "redirect:/applications/job/" + application.getJob().getId();
            }
            
            // Accept application
            applicationService.acceptApplication(applicationId);
            
            redirectAttributes.addFlashAttribute("success", "Application accepted! You can now contact the freelancer.");
            return "redirect:/applications/" + applicationId;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error accepting application: " + e.getMessage());
            return "redirect:/applications";
        }
    }
    
    /**
     * Decline application
     * POST /applications/{applicationId}/decline
     */
    @PostMapping("/{applicationId}/decline")
    public String declineApplication(
            @PathVariable Long applicationId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/auth/login";
            }
            
            User currentUser = userService.findByEmail(authentication.getName());
            if (currentUser == null || !currentUser.isClient()) {
                redirectAttributes.addFlashAttribute("error", "Only clients can decline applications");
                return "redirect:/dashboard";
            }
            
            // Get application and verify ownership
            JobApplication application = applicationService.getApplicationById(applicationId)
                    .orElse(null);
            
            if (application == null) {
                redirectAttributes.addFlashAttribute("error", "Application not found");
                return "redirect:/applications";
            }
            
            if (!application.getJob().getClient().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("error", "You don't have permission to decline this application");
                return "redirect:/applications/job/" + application.getJob().getId();
            }
            
            // Decline application
            applicationService.declineApplication(applicationId);
            
            redirectAttributes.addFlashAttribute("success", "Application declined.");
            return "redirect:/applications/job/" + application.getJob().getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error declining application: " + e.getMessage());
            return "redirect:/applications";
        }
    }
}
