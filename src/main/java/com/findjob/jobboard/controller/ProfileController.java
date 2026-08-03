package com.findjob.jobboard.controller;

import com.findjob.jobboard.model.User;
import com.findjob.jobboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * ProfileController - Handles user profile operations
 * Manages profile completion, viewing, and editing
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {
    
    @Autowired
    private UserService userService;
    
    // ==========================================
    // Profile Completion (After Registration)
    // ==========================================
    
    /**
     * Display profile completion page for new users
     */
    @GetMapping("/complete")
    public String showCompleteProfileForm(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        // Check if profile already complete
        if (user.isProfileComplete()) {
            return "redirect:/profile/" + user.getId();
        }
        
        model.addAttribute("title", "Complete Your Profile");
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getUserRole().name());
        return "profile/complete";
    }
    
    /**
     * Handle profile completion submission
     */
    @PostMapping("/complete")
    public String completeProfile(@ModelAttribute("user") User profileData,
                                 Authentication authentication,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            
            if (user == null) {
                return "redirect:/auth/login";
            }
            
            // Update profile
            userService.completeProfile(user.getId(), profileData);
            
            redirectAttributes.addFlashAttribute("success", 
                "Profile completed successfully! Welcome to FindJob.");
            
            return "redirect:/profile/" + user.getId();
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to complete profile: " + e.getMessage());
            return "profile/complete";
        }
    }
    
    // ==========================================
    // View Profile
    // ==========================================
    
    /**
     * View user profile (public or private)
     */
    @GetMapping("/{userId}")
    public String viewProfile(@PathVariable Long userId,
                             Authentication authentication,
                             Model model) {
        try {
            User user = userService.getUserById(userId);
            
            if (user == null || !user.getIsActive()) {
                model.addAttribute("error", "This profile is no longer available");
                return "error/404";
            }
            
            // Determine if viewing own profile
            boolean isOwnProfile = false;
            if (authentication != null && authentication.isAuthenticated()) {
                User currentUser = userService.findByEmail(authentication.getName());
                isOwnProfile = (currentUser != null && currentUser.getId().equals(userId));
            }
            
            model.addAttribute("title", user.getFullName() + " - Profile");
            model.addAttribute("user", user);
            model.addAttribute("isOwnProfile", isOwnProfile);
            
            return "profile/view";
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "User not found");
            return "error/404";
        }
    }
    
    /**
     * View current user's own profile
     */
    @GetMapping
    public String viewOwnProfile(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        User user = userService.findByEmail(authentication.getName());
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        return "redirect:/profile/" + user.getId();
    }
    
    // ==========================================
    // Edit Profile
    // ==========================================
    
    /**
     * Display edit profile form
     */
    @GetMapping("/edit/{userId}")
    public String showEditProfileForm(@PathVariable Long userId,
                                     Authentication authentication,
                                     Model model) {
        
        try {
            User user = userService.getUserById(userId);
            
            if (user == null) {
                model.addAttribute("error", "User not found");
                return "error/404";
            }
            
            // Verify user owns this profile
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/auth/login";
            }
            
            User currentUser = userService.findByEmail(authentication.getName());
            if (!currentUser.getId().equals(userId)) {
                model.addAttribute("error", "You don't have permission to edit this profile");
                return "redirect:/profile/" + userId;
            }
            
            model.addAttribute("title", "Edit Profile");
            model.addAttribute("user", user);
            model.addAttribute("userRole", user.getUserRole().name());
            
            return "profile/edit";
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "User not found");
            return "error/404";
        }
    }
    
    /**
     * Handle profile update submission
     */
    @PostMapping("/edit/{userId}")
    public String updateProfile(@PathVariable Long userId,
                               @ModelAttribute("user") User profileData,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        
        try {
            // Verify user owns this profile
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/auth/login";
            }
            
            User currentUser = userService.findByEmail(authentication.getName());
            if (!currentUser.getId().equals(userId)) {
                redirectAttributes.addFlashAttribute("error", 
                    "You don't have permission to edit this profile");
                return "redirect:/profile/" + userId;
            }
            
            // Update profile
            userService.updateProfile(userId, profileData);
            
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
            
            return "redirect:/profile/" + userId;
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
            return "redirect:/profile/edit/" + userId;
        }
    }
    
    /**
     * Edit profile form (without ID - uses current user)
     */
    @GetMapping("/edit")
    public String showEditOwnProfile(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        User user = userService.findByEmail(authentication.getName());
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        return "redirect:/profile/edit/" + user.getId();
    }
    
    /**
     * Update own profile (without ID)
     */
    @PostMapping("/edit")
    public String updateOwnProfile(@ModelAttribute("user") User profileData,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        User user = userService.findByEmail(authentication.getName());
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        return updateProfile(user.getId(), profileData, authentication, redirectAttributes);
    }
    
    // ==========================================
    // Profile Actions
    // ==========================================
    
    /**
     * Change password
     */
    @GetMapping("/change-password")
    public String showChangePasswordForm(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("title", "Change Password");
        return "profile/change-password";
    }
    
    /**
     * Handle password change
     */
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                Authentication authentication,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            // Validate inputs
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "New passwords do not match");
                return "profile/change-password";
            }
            
            if (newPassword.length() < 8) {
                model.addAttribute("error", "Password must be at least 8 characters");
                return "profile/change-password";
            }
            
            User user = userService.findByEmail(authentication.getName());
            if (user == null) {
                return "redirect:/auth/login";
            }
            
            // Change password
            userService.changePassword(user.getId(), currentPassword, newPassword);
            
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
            return "redirect:/profile/" + user.getId();
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "profile/change-password";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to change password: " + e.getMessage());
            return "profile/change-password";
        }
    }
    
    /**
     * Deactivate account
     */
    @PostMapping("/deactivate")
    public String deactivateAccount(Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        try {
            User user = userService.findByEmail(authentication.getName());
            if (user == null) {
                return "redirect:/auth/login";
            }
            
            userService.deactivateUser(user.getId());
            
            // Clear security context (logout)
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
            
            redirectAttributes.addFlashAttribute("success", 
                "Your account has been deactivated. You can reactivate it by logging in again.");
            
            return "redirect:/";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Failed to deactivate account: " + e.getMessage());
            return "redirect:/profile";
        }
    }
}
