package com.findjob.jobboard.repository;

import com.findjob.jobboard.model.User;
import com.findjob.jobboard.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository - Data access layer for User entity
 * Provides methods for CRUD operations and custom queries
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // ==========================================
    // Basic Queries
    // ==========================================
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by email (nullable)
     */
    User findByEmailIgnoreCase(String email);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find user by ID with optional
     */
    Optional<User> findById(Long id);
    
    // ==========================================
    // Role-Based Queries
    // ==========================================
    
    /**
     * Find all freelancers
     */
    List<User> findByUserRole(UserRole role);
    
    /**
     * Find all active freelancers
     */
    List<User> findByUserRoleAndIsActiveTrue(UserRole role);
    
    /**
     * Find all verified users
     */
    List<User> findByIsVerifiedTrue();
    
    /**
     * Find all active users
     */
    List<User> findByIsActiveTrue();
    
    // ==========================================
    // Tier & Reputation Queries
    // ==========================================
    
    /**
     * Find users by tier level (e.g., "EXPERT", "TOP_RATED")
     */
    List<User> findByTierLevel(String tierLevel);
    
    /**
     * Find top-rated freelancers by reputation score
     */
    @Query("SELECT u FROM User u WHERE u.userRole = :role AND u.isActive = true ORDER BY u.reputationScore DESC LIMIT :limit")
    List<User> findTopRatedByRole(@Param("role") UserRole role, @Param("limit") int limit);
    
    /**
     * Find freelancers with minimum reputation
     */
    @Query("SELECT u FROM User u WHERE u.userRole = :role AND u.reputationScore >= :minRating AND u.isActive = true ORDER BY u.reputationScore DESC")
    List<User> findByMinimumRating(@Param("role") UserRole role, @Param("minRating") Double minRating);
    
    /**
     * Find users by tier and minimum reputation
     */
    List<User> findByTierLevelAndReputationScoreGreaterThanEqual(String tierLevel, Double minReputation);
    
    // ==========================================
    // Search Queries
    // ==========================================
    
    /**
     * Search users by name
     */
    @Query("SELECT u FROM User u WHERE (u.firstName LIKE %:query% OR u.lastName LIKE %:query% OR u.headline LIKE %:query%) AND u.isActive = true")
    List<User> searchByName(@Param("query") String query);
    
    /**
     * Search freelancers by location
     */
    @Query("SELECT u FROM User u WHERE u.userRole = :role AND u.location LIKE %:location% AND u.isActive = true")
    List<User> findByLocation(@Param("role") UserRole role, @Param("location") String location);
    
    /**
     * Search freelancers by availability
     */
    @Query("SELECT u FROM User u WHERE u.userRole = :role AND u.availability = :availability AND u.isActive = true ORDER BY u.reputationScore DESC")
    List<User> findByAvailability(@Param("role") UserRole role, @Param("availability") String availability);
    
    // ==========================================
    // Count Queries
    // ==========================================
    
    /**
     * Count total freelancers
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.userRole = 'FREELANCER' AND u.isActive = true")
    long countFreelancers();
    
    /**
     * Count total clients
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.userRole = 'CLIENT' AND u.isActive = true")
    long countClients();
    
    /**
     * Count verified users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isVerified = true")
    long countVerifiedUsers();
    
    // ==========================================
    // Profile Completion Queries
    // ==========================================
    
    /**
     * Find users with incomplete profiles
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true AND (u.headline IS NULL OR u.bio IS NULL OR u.location IS NULL)")
    List<User> findUsersWithIncompleteProfiles();
    
    // ==========================================
    // Custom Update Queries (if needed)
    // ==========================================
    
    /**
     * Find user with all relationships loaded (for optimization)
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.id WHERE u.id = :id")
    Optional<User> findByIdWithDetails(@Param("id") Long id);
}
