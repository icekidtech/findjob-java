package com.findjob.jobboard.service;

import com.findjob.jobboard.model.User;
import com.findjob.jobboard.model.UserRole;
import com.findjob.jobboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * UserService - Business logic for user management
 * Handles user CRUD operations, profile management, and reputation updates
 */
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // ==========================================
    // User CRUD Operations
    // ==========================================
    
    /**
     * Get user by ID
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }
    
    /**
     * Get user by ID (optional)
     */
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Get user by email
     */
    public User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }
    
    /**
     * Get user by email (optional)
     */
    public Optional<User> findByEmailOptional(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Create/Save new user
     */
    public User save(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        return userRepository.save(user);
    }
    
    /**
     * Update existing user
     */
    public User update(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User not found with id: " + user.getId());
        }
        return userRepository.save(user);
    }
    
    /**
     * Delete user by ID
     */
    public void deleteUser(Long id) {
        User user = getUserById(id);
        user.setIsActive(false);
        userRepository.save(user);
    }
    
    /**
     * Permanently delete user
     */
    public void permanentlyDeleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get all active users
     */
    public List<User> getAllActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }
    
    // ==========================================
    // Profile Management
    // ==========================================
    
    /**
     * Complete user profile (after registration)
     */
    public User completeProfile(Long userId, User profileData) {
        User user = getUserById(userId);
        
        // Update common fields
        if (profileData.getHeadline() != null) {
            user.setHeadline(profileData.getHeadline());
        }
        if (profileData.getBio() != null) {
            user.setBio(profileData.getBio());
        }
        if (profileData.getLocation() != null) {
            user.setLocation(profileData.getLocation());
        }
        if (profileData.getPhone() != null) {
            user.setPhone(profileData.getPhone());
        }
        if (profileData.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(profileData.getProfilePictureUrl());
        }
        
        // Update role-specific fields
        if (user.isFreelancer()) {
            if (profileData.getHourlyRate() != null) {
                user.setHourlyRate(profileData.getHourlyRate());
            }
            if (profileData.getAvailability() != null) {
                user.setAvailability(profileData.getAvailability());
            }
            if (profileData.getExperience() != null) {
                user.setExperience(profileData.getExperience());
            }
        } else if (user.isClient()) {
            if (profileData.getCompanyName() != null) {
                user.setCompanyName(profileData.getCompanyName());
            }
            if (profileData.getCompanyDescription() != null) {
                user.setCompanyDescription(profileData.getCompanyDescription());
            }
            if (profileData.getCompanyWebsite() != null) {
                user.setCompanyWebsite(profileData.getCompanyWebsite());
            }
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Update user profile
     */
    public User updateProfile(Long userId, User profileData) {
        return completeProfile(userId, profileData);
    }
    
    /**
     * Check if profile is complete
     */
    public boolean isProfileComplete(Long userId) {
        User user = getUserById(userId);
        return user.isProfileComplete();
    }
    
    // ==========================================
    // Authentication & Security
    // ==========================================
    
    /**
     * Verify user password
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * Change user password
     */
    public User changePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);
        
        if (!verifyPassword(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
    
    /**
     * Reset password (for forgot password feature)
     */
    public User resetPassword(Long userId, String newPassword) {
        User user = getUserById(userId);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
    
    /**
     * Verify user email
     */
    public User verifyEmail(Long userId) {
        User user = getUserById(userId);
        user.setIsVerified(true);
        return userRepository.save(user);
    }
    
    // ==========================================
    // Reputation & Tier System
    // ==========================================
    
    /**
     * Update user reputation score
     */
    public User updateReputationScore(Long userId, Double averageRating) {
        User user = getUserById(userId);
        user.recalculateReputation(averageRating);
        return userRepository.save(user);
    }
    
    /**
     * Update tier level based on current stats
     */
    public User updateTierLevel(Long userId) {
        User user = getUserById(userId);
        user.updateTier();
        return userRepository.save(user);
    }
    
    /**
     * Increment completed projects count
     */
    public User incrementProjectCount(Long userId) {
        User user = getUserById(userId);
        user.setTotalProjects(user.getTotalProjects() + 1);
        user.updateTier();
        return userRepository.save(user);
    }
    
    /**
     * Increment review count
     */
    public User incrementReviewCount(Long userId) {
        User user = getUserById(userId);
        user.setTotalReviews(user.getTotalReviews() + 1);
        return userRepository.save(user);
    }
    
    /**
     * Get top-rated users
     */
    public List<User> getTopRatedUsers(UserRole role, int limit) {
        return userRepository.findTopRatedByRole(role, limit);
    }
    
    /**
     * Find users by tier level
     */
    public List<User> getUsersByTier(String tierLevel) {
        return userRepository.findByTierLevel(tierLevel);
    }
    
    /**
     * Find users by minimum reputation
     */
    public List<User> getUsersByMinimumRating(UserRole role, Double minRating) {
        return userRepository.findByMinimumRating(role, minRating);
    }
    
    // ==========================================
    // Search & Discovery
    // ==========================================
    
    /**
     * Search users by name or headline
     */
    public List<User> searchUsers(String query) {
        return userRepository.searchByName(query);
    }
    
    /**
     * Find freelancers by location
     */
    public List<User> findFreelancersByLocation(String location) {
        return userRepository.findByLocation(UserRole.FREELANCER, location);
    }
    
    /**
     * Find freelancers by availability
     */
    public List<User> findFreelancersByAvailability(String availability) {
        return userRepository.findByAvailability(UserRole.FREELANCER, availability);
    }
    
    /**
     * Get all freelancers
     */
    public List<User> getAllFreelancers() {
        return userRepository.findByUserRoleAndIsActiveTrue(UserRole.FREELANCER);
    }
    
    /**
     * Get all clients
     */
    public List<User> getAllClients() {
        return userRepository.findByUserRoleAndIsActiveTrue(UserRole.CLIENT);
    }
    
    /**
     * Get users with incomplete profiles
     */
    public List<User> getUsersWithIncompleteProfiles() {
        return userRepository.findUsersWithIncompleteProfiles();
    }
    
    // ==========================================
    // Statistics
    // ==========================================
    
    /**
     * Get total freelancer count
     */
    public long getTotalFreelancers() {
        return userRepository.countFreelancers();
    }
    
    /**
     * Get total client count
     */
    public long getTotalClients() {
        return userRepository.countClients();
    }
    
    /**
     * Get total verified users
     */
    public long getTotalVerifiedUsers() {
        return userRepository.countVerifiedUsers();
    }
    
    /**
     * Get total users
     */
    public long getTotalUsers() {
        return userRepository.count();
    }
    
    // ==========================================
    // Account Status
    // ==========================================
    
    /**
     * Activate user account
     */
    public User activateUser(Long userId) {
        User user = getUserById(userId);
        user.setIsActive(true);
        return userRepository.save(user);
    }
    
    /**
     * Deactivate user account
     */
    public User deactivateUser(Long userId) {
        User user = getUserById(userId);
        user.setIsActive(false);
        return userRepository.save(user);
    }
    
    /**
     * Check if user account is active
     */
    public boolean isUserActive(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(User::getIsActive).orElse(false);
    }
}
