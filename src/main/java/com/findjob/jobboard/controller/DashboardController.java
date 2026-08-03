package com.findjob.jobboard.controller;

import com.findjob.jobboard.model.User;
import com.findjob.jobboard.model.Job;
import com.findjob.jobboard.service.UserService;
import com.findjob.jobboard.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * DashboardController - Handles dashboard pages for authenticated users
 * Redirects to role-specific dashboards (Freelancer/Client)
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JobRepository jobRepository;
    
    /**
     * Main dashboard route - redirects to appropriate dashboard based on user role
     */
    @GetMapping
    public String dashboard(Authentication authentication, Model model) {
        
        // Check if user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            // Get current user
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            
            if (user == null) {
                return "redirect:/auth/login";
            }
            
            // Add user to model
            model.addAttribute("user", user);
            model.addAttribute("title", "Dashboard");
            
            // Redirect to role-specific dashboard
            if (user.isFreelancer()) {
                return "dashboard/freelancer";
            } else if (user.isClient()) {
                // Get client's posted jobs
                List<Job> postedJobs = jobRepository.findByClient(user);
                model.addAttribute("postedJobs", postedJobs);
                model.addAttribute("activeJobsCount", (int) postedJobs.stream().filter(j -> j.getJobStatus().name().equals("OPEN")).count());
                return "dashboard/client";
            } else {
                return "redirect:/";
            }
            
        } catch (Exception e) {
            return "redirect:/auth/login";
        }
    }
    
    /**
     * Freelancer-specific dashboard
     */
    @GetMapping("/freelancer")
    public String freelancerDashboard(Authentication authentication, Model model) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            
            if (user == null) {
                return "redirect:/auth/login";
            }
            
            // Check if user is actually a freelancer
            if (!user.isFreelancer()) {
                return "redirect:/dashboard/client";
            }
            
            model.addAttribute("user", user);
            model.addAttribute("title", "Freelancer Dashboard");
            
            return "dashboard/freelancer";
            
        } catch (Exception e) {
            return "redirect:/auth/login";
        }
    }
    
    /**
     * Client-specific dashboard
     */
    @GetMapping("/client")
    public String clientDashboard(Authentication authentication, Model model) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            
            if (user == null) {
                return "redirect:/auth/login";
            }
            
            // Check if user is actually a client
            if (!user.isClient()) {
                return "redirect:/dashboard/freelancer";
            }
            
            // Get client's posted jobs
            List<Job> postedJobs = jobRepository.findByClient(user);
            
            model.addAttribute("user", user);
            model.addAttribute("postedJobs", postedJobs);
            model.addAttribute("activeJobsCount", (int) postedJobs.stream().filter(j -> j.getJobStatus().name().equals("OPEN")).count());
            model.addAttribute("title", "Client Dashboard");
            
            return "dashboard/client";
            
        } catch (Exception e) {
            return "redirect:/auth/login";
        }
    }
}
