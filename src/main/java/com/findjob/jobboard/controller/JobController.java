package com.findjob.jobboard.controller;

import com.findjob.jobboard.dto.JobDTO;
import com.findjob.jobboard.model.*;
import com.findjob.jobboard.service.JobApplicationService;
import com.findjob.jobboard.service.JobService;
import com.findjob.jobboard.service.JobViewService;
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

import com.findjob.jobboard.service.FileStorageService;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * JobController - Handles job-related endpoints
 * Manages job posting, browsing, and viewing
 */
@Controller
@RequestMapping("/jobs")
public class JobController {
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private JobApplicationService applicationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JobViewService jobViewService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    // ==========================================
    // Job Listing & Browsing
    // ==========================================
    
    /**
     * Browse all open jobs
     */
    @GetMapping
    public String browseJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobsPage;
        
        try {
            // Filter by search query
            if (search != null && !search.isEmpty()) {
                jobsPage = jobService.searchJobs(search, pageable);
                model.addAttribute("searchQuery", search);
            }
            // Filter by category
            else if (category != null && !category.isEmpty()) {
                jobsPage = jobService.getJobsByCategoryPaginated(category, pageable);
                model.addAttribute("selectedCategory", category);
            }
            // Get recent jobs
            else {
                jobsPage = jobService.getRecentJobs(pageable);
            }
            
            model.addAttribute("title", "Browse Jobs");
            model.addAttribute("jobs", jobsPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", jobsPage.getTotalPages());
            model.addAttribute("totalItems", jobsPage.getTotalElements());
            
            return "jobs/list";
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load jobs: " + e.getMessage());
            return "jobs/list";
        }
    }
    
    /**
     * View job details
     */
    @GetMapping("/{id}")
    public String viewJob(@PathVariable Long id, Authentication authentication, Model model) {
        try {
            Job job = jobService.getJobById(id);
            
            if (!job.isOpenForApplications() || !job.getIsPublished()) {
                model.addAttribute("error", "This job is no longer available");
                return "error/404";
            }
            
            // Record view (only count unique views per user)
            if (authentication != null && authentication.isAuthenticated()) {
                User user = userService.findByEmail(authentication.getName());
                boolean isNewView = jobViewService.recordView(job, user);
                
                // Only increment viewsCount if it's a new unique view
                if (isNewView) {
                    job.incrementViews();
                    jobService.updateJob(job);
                }
            } else {
                // Anonymous users always increment (no way to track)
                job.incrementViews();
                jobService.updateJob(job);
            }
            
            // Add user role to model
            String userRole = "GUEST";
            if (authentication != null && authentication.isAuthenticated()) {
                userRole = authentication.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .filter(auth -> auth.startsWith("ROLE_"))
                        .findFirst()
                        .orElse("GUEST");
            }
            
            model.addAttribute("title", job.getTitle());
            model.addAttribute("job", job);
            model.addAttribute("client", job.getClient());
            model.addAttribute("userRole", userRole);
            
            return "jobs/detail";
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Job not found");
            return "error/404";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading job: " + e.getMessage());
            return "error/error";
        }
    }
    
    // ==========================================
    // Job Posting (Client Only)
    // ==========================================
    
    /**
     * Show job posting form
     */
    @GetMapping("/post")
    public String showPostJobForm(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            User user = userService.findByEmail(authentication.getName());
            
            if (user == null || !user.isClient()) {
                model.addAttribute("error", "Only clients can post jobs");
                return "redirect:/dashboard";
            }
            
            model.addAttribute("title", "Post a Job");
            model.addAttribute("jobDTO", new JobDTO());
            model.addAttribute("budgetTypes", BudgetType.values());
            model.addAttribute("experienceLevels", JobExperienceLevel.values());
            
            return "jobs/post";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "error/error";
        }
    }
    
    /**
     * Handle job posting submission
     */
    @PostMapping("/post")
    public String postJob(
            @Valid @ModelAttribute("jobDTO") JobDTO jobDTO,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("=== JOB POSTING ATTEMPT ===");
        System.out.println("Authentication: " + (authentication != null ? authentication.getName() : "NULL"));
        System.out.println("Is Authenticated: " + (authentication != null && authentication.isAuthenticated()));
        
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("ERROR: User not authenticated!");
            return "redirect:/auth/login";
        }
        
        try {
            System.out.println("Fetching user by email: " + authentication.getName());
            User client = userService.findByEmail(authentication.getName());
            
            System.out.println("User found: " + (client != null ? client.getEmail() : "NULL"));
            if (client != null) {
                System.out.println("User ID: " + client.getId());
                System.out.println("Is Client: " + client.isClient());
            }
            
            if (client == null || !client.isClient()) {
                System.out.println("ERROR: User is null or not a client!");
                redirectAttributes.addFlashAttribute("error", "Only clients can post jobs");
                return "redirect:/dashboard";
            }
            
            if (bindingResult.hasErrors()) {
                System.out.println("ERROR: Binding result has errors!");
                bindingResult.getAllErrors().forEach(error -> 
                    System.out.println("  - " + error.getDefaultMessage())
                );
                model.addAttribute("title", "Post a Job");
                model.addAttribute("budgetTypes", BudgetType.values());
                model.addAttribute("experienceLevels", JobExperienceLevel.values());
                return "jobs/post";
            }
            
            System.out.println("Creating job with title: " + jobDTO.getTitle());
            System.out.println("Budget: " + jobDTO.getBudget() + " " + jobDTO.getCurrency() + " (" + jobDTO.getBudgetType() + ")");
            System.out.println("Experience Level: " + jobDTO.getExperienceLevel());
            
            // Create job from DTO
            Job job = Job.builder()
                .client(client)
                .title(jobDTO.getTitle())
                .description(jobDTO.getDescription())
                .category(jobDTO.getCategory())
                .budgetType(BudgetType.valueOf(jobDTO.getBudgetType()))
                .currency(Currency.valueOf(jobDTO.getCurrency()))
                .budgetAmount(jobDTO.getBudget())
                .experienceLevel(JobExperienceLevel.valueOf(jobDTO.getExperienceLevel()))
                .isPublished(true)
                .jobStatus(JobStatus.OPEN)
                .build();
            
            System.out.println("Saving job to database...");
            Job savedJob = jobService.createJob(job);
            
            System.out.println("Job saved successfully! Job ID: " + savedJob.getId());
            System.out.println("Redirecting to: /jobs/" + savedJob.getId());
            
            redirectAttributes.addFlashAttribute("success", 
                "Job posted successfully! Check out your job dashboard.");
            
            return "redirect:/jobs/" + savedJob.getId();
            
        } catch (Exception e) {
            System.out.println("ERROR during job posting: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Failed to post job: " + e.getMessage());
            model.addAttribute("title", "Post a Job");
            return "jobs/post";
        }
    }
    
    // ==========================================
    // Client Job Management
    // ==========================================
    
    /**
     * View client's job postings
     */
    @GetMapping("/my-jobs")
    public String myJobs(
            @RequestParam(defaultValue = "0") int page,
            Authentication authentication,
            Model model) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            User client = userService.findByEmail(authentication.getName());
            
            if (client == null || !client.isClient()) {
                model.addAttribute("error", "Only clients can view this page");
                return "redirect:/dashboard";
            }
            
            Pageable pageable = PageRequest.of(page, 10);
            Page<Job> jobsPage = jobService.getJobsByClientPaginated(client, pageable);
            
            model.addAttribute("title", "My Jobs");
            model.addAttribute("jobs", jobsPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", jobsPage.getTotalPages());
            
            return "jobs/my-jobs";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error loading jobs: " + e.getMessage());
            return "error/error";
        }
    }
    
    /**
     * Apply for a job (Show application form)
     */
    @GetMapping("/{jobId}/apply")
    public String showApplyForm(@PathVariable Long jobId, Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            Job job = jobService.getJobById(jobId);
            User freelancer = userService.findByEmail(authentication.getName());
            
            if (!freelancer.isFreelancer()) {
                model.addAttribute("error", "Only freelancers can apply for jobs");
                return "redirect:/jobs/" + jobId;
            }
            
            model.addAttribute("job", job);
            model.addAttribute("freelancer", freelancer);
            return "jobs/apply";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error loading job: " + e.getMessage());
            return "error/error";
        }
    }
    
    /**
     * Submit job application
     */
    @PostMapping("/{jobId}/apply")
    public String submitApplication(@PathVariable Long jobId,
                                   @RequestParam String coverLetter,
                                   @RequestParam(required = false) MultipartFile cvFileUrl,
                                   @RequestParam(required = false) String portfolioUrl,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            Job job = jobService.getJobById(jobId);
            User freelancer = userService.findByEmail(authentication.getName());
            
            if (!freelancer.isFreelancer()) {
                redirectAttributes.addFlashAttribute("error", "Only freelancers can apply for jobs");
                return "redirect:/jobs/" + jobId;
            }
            
            // Handle CV file upload locally
            String cvFileUrl_str = null;
            if (cvFileUrl != null && !cvFileUrl.isEmpty()) {
                try {
                    cvFileUrl_str = fileStorageService.saveCVFile(cvFileUrl);
                } catch (Exception e) {
                    System.err.println("Error uploading CV file: " + e.getMessage());
                    redirectAttributes.addFlashAttribute("warning", "CV file could not be uploaded, but application was submitted");
                }
            }
            
            // Create and submit application
            JobApplication application = new JobApplication();
            application.setJob(job);
            application.setFreelancer(freelancer);
            application.setCoverLetter(coverLetter);
            application.setCvFileUrl(cvFileUrl_str);
            application.setPortfolioUrl(portfolioUrl);
            
            applicationService.submitApplication(application);
            
            redirectAttributes.addFlashAttribute("success", "Application submitted successfully!");
            return "redirect:/jobs/" + jobId;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to submit application: " + e.getMessage());
            return "redirect:/jobs/" + jobId;
        }
    }
    
    public String viewApplications(
            @PathVariable Long jobId,
            Authentication authentication,
            Model model) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            Job job = jobService.getJobById(jobId);
            User client = userService.findByEmail(authentication.getName());
            
            // Verify this is the job owner
            if (!job.getClient().getId().equals(client.getId())) {
                model.addAttribute("error", "You don't have permission to view this");
                return "redirect:/jobs/" + jobId;
            }
            
            List<JobApplication> applications = applicationService.getApplicationsForJob(job);
            
            model.addAttribute("title", "Applications for " + job.getTitle());
            model.addAttribute("job", job);
            model.addAttribute("applications", applications);
            
            return "jobs/applications";
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Job not found");
            return "error/404";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading applications: " + e.getMessage());
            return "error/error";
        }
    }
    
    /**
     * Close/Cancel a job posting
     * POST /jobs/{jobId}/close
     */
    @PostMapping("/{jobId}/close")
    public String closeJob(@PathVariable Long jobId,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            User client = userService.findByEmail(authentication.getName());
            
            if (client == null || !client.isClient()) {
                redirectAttributes.addFlashAttribute("error", "Only clients can close jobs");
                return "redirect:/jobs/" + jobId;
            }
            
            // Get job and verify ownership
            Job job = jobService.getJobById(jobId);
            
            if (!job.getClient().getId().equals(client.getId())) {
                redirectAttributes.addFlashAttribute("error", "You don't have permission to close this job");
                return "redirect:/jobs/" + jobId;
            }
            
            // Close the job
            job.markCancelled();
            jobService.updateJob(job);
            
            redirectAttributes.addFlashAttribute("success", "Job closed successfully. No more applications will be accepted.");
            
            return "redirect:/jobs/my-jobs";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error closing job: " + e.getMessage());
            return "redirect:/jobs/" + jobId;
        }
    }
}
