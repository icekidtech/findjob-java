package com.findjob.jobboard.controller;

import com.findjob.jobboard.model.User;
import com.findjob.jobboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * PortfolioController - Handles portfolio/work showcase pages for freelancers
 */
@Controller
@RequestMapping("/portfolio")
public class PortfolioController {
    
    @Autowired
    private UserService userService;
    
    /**
     * View my portfolio (freelancer only)
     * GET /portfolio
     */
    @GetMapping
    public String viewMyPortfolio(Authentication authentication, Model model) {
        
        // Ensure user is authenticated
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
            
            // Check if user is a freelancer
            if (!user.isFreelancer()) {
                return "redirect:/dashboard";
            }
            
            model.addAttribute("user", user);
            model.addAttribute("title", "My Portfolio");
            
            return "portfolio/index";
            
        } catch (Exception e) {
            return "redirect:/auth/login";
        }
    }
    
    /**
     * Edit portfolio information
     * GET /portfolio/edit
     */
    @GetMapping("/edit")
    public String editPortfolio(Authentication authentication, Model model) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            
            if (user == null || !user.isFreelancer()) {
                return "redirect:/dashboard";
            }
            
            model.addAttribute("user", user);
            model.addAttribute("title", "Edit Portfolio");
            
            return "portfolio/edit";
            
        } catch (Exception e) {
            return "redirect:/auth/login";
        }
    }
    
    /**
     * View freelancer's portfolio (public view)
     * GET /portfolio/{userId}
     */
    @GetMapping("/{userId}")
    public String viewFreelancerPortfolio(
            @PathVariable Long userId,
            Model model,
            Authentication authentication) {
        
        try {
            // Get freelancer's profile
            User freelancer = userService.getUserById(userId);
            
            if (freelancer == null || !freelancer.isFreelancer()) {
                return "redirect:/freelancers";
            }
            
            model.addAttribute("freelancer", freelancer);
            model.addAttribute("title", freelancer.getFullName() + "'s Portfolio");
            
            return "portfolio/view";
            
        } catch (Exception e) {
            return "redirect:/freelancers";
        }
    }
    
    /**
     * Update portfolio
     * POST /portfolio/update
     */
    @PostMapping("/update")
    public String updatePortfolio(
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) String portfolioUrl,
            @RequestParam(required = false) String experience,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            
            if (user == null || !user.isFreelancer()) {
                return "redirect:/dashboard";
            }
            
            // Update portfolio fields
            if (bio != null && !bio.isBlank()) {
                user.setBio(bio);
            }
            if (portfolioUrl != null && !portfolioUrl.isBlank()) {
                user.setPortfolioUrl(portfolioUrl);
            }
            if (experience != null && !experience.isBlank()) {
                user.setExperience(experience);
            }
            
            // Save updated user
            userService.save(user);
            
            redirectAttributes.addFlashAttribute("success", "Portfolio updated successfully!");
            return "redirect:/portfolio";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update portfolio: " + e.getMessage());
            return "redirect:/portfolio/edit";
        }
    }
}
