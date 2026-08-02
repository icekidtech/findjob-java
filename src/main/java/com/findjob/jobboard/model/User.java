package com.findjob.jobboard.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * User Entity - Represents both Freelancers and Clients
 * Stores comprehensive user information including profile, reputation, and skills
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email", unique = true),
    @Index(name = "idx_role", columnList = "user_role"),
    @Index(name = "idx_tier", columnList = "tier_level"),
    @Index(name = "idx_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ==========================================
    // Basic Information
    // ==========================================
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false, length = 255)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Column(nullable = false, length = 255)
    private String passwordHash;
    
    @NotBlank(message = "First name is required")
    @Column(nullable = false, length = 100)
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Column(nullable = false, length = 100)
    private String lastName;
    
    @Column(length = 200)
    private String headline; // e.g., "Senior Full Stack Developer"
    
    // ==========================================
    // Profile Information
    // ==========================================
    
    @Column(columnDefinition = "TEXT")
    private String bio; // Detailed user description
    
    @Column(length = 150)
    private String location; // City, Country
    
    @Column(length = 20)
    private String phone;
    
    @Column(length = 500)
    private String profilePictureUrl; // URL to profile picture
    
    @Column(length = 500)
    private String portfolioUrl; // Link to portfolio website
    
    // ==========================================
    // User Role & Status
    // ==========================================
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole; // FREELANCER or CLIENT
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(nullable = false)
    private Boolean isVerified = false; // Email verified
    
    // ==========================================
    // Reputation & Tier System
    // ==========================================
    
    @Column(precision = 3, scale = 2, nullable = false)
    private Double reputationScore = 0.0; // 1.0 to 5.0
    
    @Column(length = 20, nullable = false)
    private String tierLevel = "BEGINNER"; // BEGINNER, INTERMEDIATE, EXPERT, TOP_RATED
    
    @Column(nullable = false)
    private Integer totalProjects = 0; // Completed projects count
    
    @Column(nullable = false)
    private Integer totalReviews = 0; // Number of reviews received
    
    // ==========================================
    // Freelancer Specific Fields
    // ==========================================
    
    @Column(precision = 10, scale = 2)
    private BigDecimal hourlyRate; // For freelancers
    
    @Column(length = 50)
    private String availability; // FULL_TIME, PART_TIME, AVAILABLE, NOT_AVAILABLE
    
    @Column(columnDefinition = "TEXT")
    private String experience; // Years of experience summary
    
    // ==========================================
    // Client Specific Fields
    // ==========================================
    
    @Column(length = 255)
    private String companyName; // For client users
    
    @Column(length = 500)
    private String companyDescription;
    
    @Column(length = 255)
    private String companyWebsite;
    
    // ==========================================
    // Audit Fields
    // ==========================================
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(length = 255)
    private String lastLoginAt; // Last login timestamp
    
    // ==========================================
    // Utility Methods
    // ==========================================
    
    /**
     * Get user's full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Check if user is a freelancer
     */
    public boolean isFreelancer() {
        return UserRole.FREELANCER.equals(userRole);
    }
    
    /**
     * Check if user is a client
     */
    public boolean isClient() {
        return UserRole.CLIENT.equals(userRole);
    }
    
    /**
     * Check if profile is complete
     */
    public boolean isProfileComplete() {
        if (isFreelancer()) {
            return headline != null && !headline.isBlank() && 
                   bio != null && !bio.isBlank() &&
                   location != null && !location.isBlank();
        } else {
            return companyName != null && !companyName.isBlank() &&
                   companyDescription != null && !companyDescription.isBlank();
        }
    }
    
    /**
     * Get average rating (reputation score)
     */
    public Double getAverageRating() {
        return reputationScore;
    }
    
    /**
     * Update tier level based on projects and reputation
     */
    public void updateTier() {
        if (totalProjects >= 200 && reputationScore >= 4.5) {
            tierLevel = "TOP_RATED";
        } else if (totalProjects >= 150 && reputationScore >= 4.2) {
            tierLevel = "EXPERT";
        } else if (totalProjects >= 50 && reputationScore >= 4.0) {
            tierLevel = "INTERMEDIATE";
        } else {
            tierLevel = "BEGINNER";
        }
    }
    
    /**
     * Calculate reputation score based on reviews
     */
    public void recalculateReputation(Double averageRating) {
        this.reputationScore = averageRating != null ? averageRating : 0.0;
        updateTier();
    }
}
