package com.findjob.jobboard.controller;

import com.findjob.jobboard.dto.ApplicationDTO;
import com.findjob.jobboard.model.Job;
import com.findjob.jobboard.model.JobApplication;
import com.findjob.jobboard.model.ApplicationStatus;
import com.findjob.jobboard.model.User;
import com.findjob.jobboard.service.JobApplicationService;
import com.findjob.jobboard.service.JobService;
import com.findjob.jobboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * ApplicationController - Handles job application endpoints
 * Manages bidding, acceptance, rejection, and withdrawal
 */
@Controller
@RequestMapping("/applications")
public class ApplicationController {
    
    @Autowired
    private JobApplicationService applicationService;
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private UserService userService;
    
    // ==========================================
    // Submit Application
    // ==========================================
    
    /**
     * Show application form for a job
     */
    @GetMapping("/apply/{jobId}")
    public String showApplicationForm(
            @PathVariable Long jobId,
            Authentication authentication,
            Model model) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            Job job = jobService.getJobById(jobId);
            User freelancer = userService.findByEmail(authentication.getName());
            
            if (freelancer == null || !freelancer.isFreelancer()) {
                model.addAttribute("error", "Only freelancers can apply for jobs");
                return "redirect:/jobs/" + jobId;
            }
            
            if (!job.isOpenForApplications()) {
                model.addAttribute("error", "This job is no longer accepting applications");
                return "redirect:/jobs/" + jobId;
            }
            
            // Check if already applied
            Optional<JobApplication> existingApp = applicationService.getApplication(job, freelancer);
            if (existingApp.isPresent()) {
                model.addAttribute("error", "You have already applied to this job");
                return "redirect:/jobs/" + jobId;
            }
            
            model.addAttribute("title", "Apply for: " + job.getTitle());
            model.addAttribute("job", job);
            model.addAttribute("applicationDTO", new ApplicationDTO());
            
            return "applications/apply";
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Job not found");
            return "error/404";
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "error/error";
        }
    }
    
    /**
     * Submit job application
     */
    @PostMapping("/apply/{jobId}")
    public String submitApplication(
            @PathVariable Long jobId,
            @Valid @ModelAttribute("applicationDTO") ApplicationDTO applicationDTO,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            Job job = jobService.getJobById(jobId);
            User freelancer = userService.findByEmail(authentication.getName());
            
            if (freelancer == null || !freelancer.isFreelancer()) {
                redirectAttributes.addFlashAttribute("error", "Only freelancers can apply");
                return "redirect:/jobs/" + jobId;
            }
            
            if (bindingResult.hasErrors()) {
                model.addAttribute("title", "Apply for: " + job.getTitle());
                model.addAttribute("job", job);
                return "applications/apply";
            }
            
            // Create application
            JobApplication application = JobApplication.builder()
                .job(job)
                .freelancer(freelancer)
                .coverLetter(applicationDTO.getCoverLetter())
                .proposedBudget(applicationDTO.getProposedBudget())
                .proposedTimeline(applicationDTO.getProposedTimeline())
                .attachmentUrl(applicationDTO.getAttachmentUrl())
                .applicationStatus(ApplicationStatus.PENDING)
                .build();
            
            JobApplication saved = applicationService.submitApplication(application);
            
            redirectAttributes.addFlashAttribute("success", 
                "Application submitted! Wait for the client's response.");
            
            return "redirect:/applications/my-applications";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/jobs/" + jobId;
        } catch (Exception e) {
            model.addAttribute("error", "Failed to submit application: " + e.getMessage());
            return "error/error";
        }
    }
    
    // ==========================================
    // Freelancer View Applications
    // ==========================================
    
    /**
     * View freelancer's applications
     */
    @GetMapping("/my-applications")
    public String myApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status,
            Authentication authentication,
            Model model) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            User freelancer = userService.findByEmail(authentication.getName());
            
            if (freelancer == null || !freelancer.isFreelancer()) {
                model.addAttribute("error", "Only freelancers can view this page");
                return "redirect:/dashboard";
            }
            
            Pageable pageable = PageRequest.of(page, 10);
            Page<JobApplication> applicationsPage;
            
            if (status != null && !status.isEmpty()) {
                ApplicationStatus appStatus = ApplicationStatus.valueOf(status.toUpperCase());
                applicationsPage = applicationService.getPendingApplicationsByFreelancerPaginated(freelancer, pageable);
                model.addAttribute("selectedStatus", status);
            } else {
                applicationsPage = applicationService.getApplicationsByFreelancerPaginated(freelancer, pageable);
            }
            
            model.addAttribute("title", "My Applications");
            model.addAttribute("applications", applicationsPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", applicationsPage.getTotalPages());
            
            return "applications/my-applications";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error loading applications: " + e.getMessage());
            return "error/error";
        }
    }
    
    /**
     * Withdraw application
     */
    @PostMapping("/{applicationId}/withdraw")
    public String withdrawApplication(
            @PathVariable Long applicationId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            JobApplication application = applicationService.getApplicationById(applicationId);
            User freelancer = userService.findByEmail(authentication.getName());
            
            // Verify ownership
            if (!application.getFreelancer().getId().equals(freelancer.getId())) {
                redirectAttributes.addFlashAttribute("error", "You don't have permission to withdraw this");
                return "redirect:/applications/my-applications";
            }
            
            applicationService.withdrawApplication(applicationId);
            
            redirectAttributes.addFlashAttribute("success", "Application withdrawn successfully");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/applications/my-applications";
    }
    
    // ==========================================
    // Client Review Applications
    // ==========================================
    
    /**
     * View application details
     */
    @GetMapping("/{applicationId}")
    public String viewApplication(
            @PathVariable Long applicationId,
            Authentication authentication,
            Model model) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            JobApplication application = applicationService.getApplicationById(applicationId);
            User client = userService.findByEmail(authentication.getName());
            
            // Verify this is the job owner
            if (!application.getJob().getClient().getId().equals(client.getId())) {
                model.addAttribute("error", "You don't have permission to view this");
                return "error/404";
            }
            
            model.addAttribute("title", "Application Details");
            model.addAttribute("application", application);
            model.addAttribute("job", application.getJob());
            model.addAttribute("freelancer", application.getFreelancer());
            
            return "applications/view";
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Application not found");
            return "error/404";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading application: " + e.getMessage());
            return "error/error";
        }
    }
    
    /**
     * Accept application
     */
    @PostMapping("/{applicationId}/accept")
    public String acceptApplication(
            @PathVariable Long applicationId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            JobApplication application = applicationService.getApplicationById(applicationId);
            User client = userService.findByEmail(authentication.getName());
            
            // Verify this is the job owner
            if (!application.getJob().getClient().getId().equals(client.getId())) {
                redirectAttributes.addFlashAttribute("error", "You don't have permission");
                return "redirect:/dashboard";
            }
            
            applicationService.acceptApplication(applicationId);
            
            redirectAttributes.addFlashAttribute("success", 
                "Application accepted! Freelancer " + application.getFreelancer().getFullName() + " is now hired.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/jobs/" + application.getJob().getId() + "/applications";
    }
    
    /**
     * Reject application
     */
    @PostMapping("/{applicationId}/reject")
    public String rejectApplication(
            @PathVariable Long applicationId,
            @RequestParam(required = false) String feedback,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            JobApplication application = applicationService.getApplicationById(applicationId);
            User client = userService.findByEmail(authentication.getName());
            
            // Verify this is the job owner
            if (!application.getJob().getClient().getId().equals(client.getId())) {
                redirectAttributes.addFlashAttribute("error", "You don't have permission");
                return "redirect:/dashboard";
            }
            
            applicationService.rejectApplication(applicationId, feedback != null ? feedback : "");
            
            redirectAttributes.addFlashAttribute("success", "Application rejected");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/jobs/" + application.getJob().getId() + "/applications";
    }
}
