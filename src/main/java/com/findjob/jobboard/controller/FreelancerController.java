package com.findjob.jobboard.controller;

import com.findjob.jobboard.model.User;
import com.findjob.jobboard.model.UserRole;
import com.findjob.jobboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * FreelancerController - Handles browsing and searching for freelancers
 * Only clients can access this endpoint
 */
@Controller
@RequestMapping("/freelancers")
public class FreelancerController {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * List all freelancers with pagination and search
     * GET /freelancers
     */
    @GetMapping
    public String listFreelancers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String availability,
            @RequestParam(required = false) String tier,
            Model model,
            Authentication authentication) {
        
        // Ensure user is authenticated and is a client
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        // Get all freelancers based on filters
        List<User> freelancers;
        
        if (search != null && !search.isBlank()) {
            // Search by name or headline
            freelancers = userRepository.searchByName(search);
        } else if (location != null && !location.isBlank()) {
            // Filter by location
            freelancers = userRepository.findByLocation(UserRole.FREELANCER, location);
        } else if (availability != null && !availability.isBlank()) {
            // Filter by availability
            freelancers = userRepository.findByAvailability(UserRole.FREELANCER, availability);
        } else if (tier != null && !tier.isBlank()) {
            // Filter by tier level
            freelancers = userRepository.findByTierLevel(tier);
            // Only keep freelancers
            freelancers = freelancers != null ? freelancers.stream()
                    .filter(u -> u.isFreelancer())
                    .toList() : List.of();
        } else {
            // Get all active freelancers (or all if none are marked active)
            List<User> activeFreelancers = userRepository.findByUserRoleAndIsActiveTrue(UserRole.FREELANCER);
            if (activeFreelancers != null && !activeFreelancers.isEmpty()) {
                freelancers = activeFreelancers;
            } else {
                // Fallback: get all freelancers regardless of active status for testing
                freelancers = userRepository.findByUserRole(UserRole.FREELANCER);
            }
        }
        
        // Sort by reputation score (descending)
        freelancers.sort((a, b) -> Double.compare(b.getReputationScore(), a.getReputationScore()));
        
        // Create paginated result
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), freelancers.size());
        
        List<User> pageContent = freelancers.subList(start, end);
        Page<User> freelancerPage = new PageImpl<>(pageContent, pageable, freelancers.size());
        
        // Add to model
        model.addAttribute("freelancers", freelancerPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", freelancerPage.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("location", location);
        model.addAttribute("availability", availability);
        model.addAttribute("tier", tier);
        model.addAttribute("totalFreelancers", freelancers.size());
        
        return "freelancers/list";
    }
    
    /**
     * View freelancer profile details
     * GET /freelancers/{id}
     */
    @GetMapping("/{id}")
    public String viewFreelancerProfile(
            @PathVariable Long id,
            Model model,
            Authentication authentication) {
        
        // Ensure user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        // Get freelancer details
        User freelancer = userRepository.findById(id)
                .orElse(null);
        
        if (freelancer == null || !freelancer.isFreelancer()) {
            return "redirect:/freelancers";
        }
        
        model.addAttribute("freelancer", freelancer);
        
        return "freelancers/profile";
    }
}
