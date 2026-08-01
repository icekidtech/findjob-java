package com.findjob.jobboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.findjob.jobboard.dto.LoginRequest;
import com.findjob.jobboard.dto.RegisterRequest;
import com.findjob.jobboard.model.User;
import com.findjob.jobboard.model.UserRole;
import com.findjob.jobboard.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * AuthController - Handles authentication related endpoints
 * Manages user registration, login, and logout
 */
@Controller
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // ==========================================
    // Login Endpoints
    // ==========================================
    
    /**
     * Display login page
     */
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("title", "Login");
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }
    
    /**
     * Handle login form submission
     */
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginRequest loginRequest,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Please check your input");
            return "auth/login";
        }
        
        try {
            // Find user by email
            User user = userService.findByEmail(loginRequest.getEmail());
            
            if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                model.addAttribute("error", "Invalid email or password");
                return "auth/login";
            }
            
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            redirectAttributes.addFlashAttribute("success", "Login successful! Welcome back.");
            
            // Redirect to dashboard
            return "redirect:/dashboard";
            
        } catch (Exception e) {
            model.addAttribute("error", "Login failed: " + e.getMessage());
            return "auth/login";
        }
    }
    
    // ==========================================
    // Registration Endpoints
    // ==========================================
    
    /**
     * Display registration page
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("title", "Sign Up");
        model.addAttribute("registerRequest", new RegisterRequest());
        model.addAttribute("roles", UserRole.values());
        return "auth/register";
    }
    
    /**
     * Handle registration form submission
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterRequest registerRequest,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Please check your input");
            return "auth/register";
        }
        
        try {
            // Check if email already exists
            if (userService.findByEmail(registerRequest.getEmail()) != null) {
                model.addAttribute("error", "Email already registered");
                return "auth/register";
            }
            
            // Validate passwords match
            if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
                model.addAttribute("error", "Passwords do not match");
                return "auth/register";
            }
            
            // Validate password strength
            if (registerRequest.getPassword().length() < 8) {
                model.addAttribute("error", "Password must be at least 8 characters");
                return "auth/register";
            }
            
            // Create new user
            User newUser = new User();
            newUser.setEmail(registerRequest.getEmail());
            newUser.setFirstName(registerRequest.getFirstName());
            newUser.setLastName(registerRequest.getLastName());
            newUser.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
            newUser.setUserRole(UserRole.valueOf(registerRequest.getRole()));
            newUser.setIsActive(true);
            newUser.setIsVerified(false);
            newUser.setReputationScore(0.0);
            newUser.setTierLevel("BEGINNER");
            newUser.setTotalProjects(0);
            
            // Save user
            User savedUser = userService.save(newUser);
            
            // Auto-login user after registration
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    registerRequest.getEmail(),
                    registerRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            redirectAttributes.addFlashAttribute("success", 
                "Account created successfully! Welcome to FindJob.");
            
            // Redirect to complete profile
            return "redirect:/profile/complete";
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Invalid role selected");
            return "auth/register";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "auth/register";
        }
    }
    
    // ==========================================
    // Logout
    // ==========================================
    
    /**
     * Handle logout
     */
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        SecurityContextHolder.clearContext();
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "You have been logged out successfully.");
        return "redirect:/";
    }
    
    // ==========================================
    // Password Reset (Placeholder)
    // ==========================================
    
    /**
     * Display forgot password page
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("title", "Forgot Password");
        return "auth/forgot-password";
    }
    
    /**
     * Handle forgot password submission
     */
    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String email,
                                      RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(email);
            
            if (user == null) {
                redirectAttributes.addFlashAttribute("warning", 
                    "If an account exists with this email, password reset instructions have been sent.");
            } else {
                // TODO: Send password reset email
                redirectAttributes.addFlashAttribute("success", 
                    "Password reset instructions have been sent to your email.");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "An error occurred: " + e.getMessage());
        }
        
        return "redirect:/auth/login";
    }
}
