package com.findjob.jobboard.repository;

import com.findjob.jobboard.model.Job;
import com.findjob.jobboard.model.Review;
import com.findjob.jobboard.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ReviewRepository - Data access layer for Review entity
 * Handles all database operations for reviews and ratings
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // ==========================================
    // Find Reviews by Recipient
    // ==========================================
    
    /**
     * Find all reviews for a user
     */
    List<Review> findByRecipient(User recipient);
    
    /**
     * Find active reviews for a user
     */
    List<Review> findByRecipientAndIsPublicTrueAndDeletedAtNull(User recipient);
    
    /**
     * Find reviews for user with pagination
     */
    Page<Review> findByRecipientAndIsPublicTrueAndDeletedAtNull(User recipient, Pageable pageable);
    
    /**
     * Find reviews for a specific job
     */
    List<Review> findByJob(Job job);
    
    /**
     * Find active reviews for a job
     */
    List<Review> findByJobAndIsPublicTrueAndDeletedAtNull(Job job);
    
    // ==========================================
    // Find Reviews by Author
    // ==========================================
    
    /**
     * Find all reviews written by a user
     */
    List<Review> findByAuthor(User author);
    
    /**
     * Find reviews written by user for a specific recipient
     */
    Optional<Review> findByAuthorAndRecipientAndJob(User author, User recipient, Job job);
    
    // ==========================================
    // Count Reviews
    // ==========================================
    
    /**
     * Count active reviews for a user
     */
    long countByRecipientAndIsPublicTrueAndDeletedAtNull(User recipient);
    
    /**
     * Count reviews for a job
     */
    long countByJobAndIsPublicTrueAndDeletedAtNull(Job job);
    
    /**
     * Count reviews written by a user
     */
    long countByAuthor(User author);
    
    // ==========================================
    // Rating Queries
    // ==========================================
    
    /**
     * Get average rating for a user
     */
    @Query("SELECT AVG(r.overallRating) FROM Review r " +
           "WHERE r.recipient = :recipient AND r.isPublic = true AND r.deletedAt IS NULL")
    Double getAverageRating(@Param("recipient") User recipient);
    
    /**
     * Get average rating for a specific rating type
     */
    @Query("SELECT AVG(r.qualityRating) FROM Review r " +
           "WHERE r.recipient = :recipient AND r.isPublic = true AND r.deletedAt IS NULL")
    Double getAverageQualityRating(@Param("recipient") User recipient);
    
    @Query("SELECT AVG(r.communicationRating) FROM Review r " +
           "WHERE r.recipient = :recipient AND r.isPublic = true AND r.deletedAt IS NULL")
    Double getAverageCommunicationRating(@Param("recipient") User recipient);
    
    @Query("SELECT AVG(r.professionalism) FROM Review r " +
           "WHERE r.recipient = :recipient AND r.isPublic = true AND r.deletedAt IS NULL")
    Double getAverageProfessionalism(@Param("recipient") User recipient);
    
    @Query("SELECT AVG(r.timeliness) FROM Review r " +
           "WHERE r.recipient = :recipient AND r.isPublic = true AND r.deletedAt IS NULL")
    Double getAverageTimeliness(@Param("recipient") User recipient);
    
    // ==========================================
    // Rating Distribution
    // ==========================================
    
    /**
     * Get count of 5-star reviews
     */
    @Query("SELECT COUNT(r) FROM Review r " +
           "WHERE r.recipient = :recipient AND r.overallRating = 5 " +
           "AND r.isPublic = true AND r.deletedAt IS NULL")
    long countFiveStarReviews(@Param("recipient") User recipient);
    
    /**
     * Get count of 4-star reviews
     */
    @Query("SELECT COUNT(r) FROM Review r " +
           "WHERE r.recipient = :recipient AND r.overallRating = 4 " +
           "AND r.isPublic = true AND r.deletedAt IS NULL")
    long countFourStarReviews(@Param("recipient") User recipient);
    
    /**
     * Get count of 3-star reviews
     */
    @Query("SELECT COUNT(r) FROM Review r " +
           "WHERE r.recipient = :recipient AND r.overallRating = 3 " +
           "AND r.isPublic = true AND r.deletedAt IS NULL")
    long countThreeStarReviews(@Param("recipient") User recipient);
    
    /**
     * Get count of 2-star reviews
     */
    @Query("SELECT COUNT(r) FROM Review r " +
           "WHERE r.recipient = :recipient AND r.overallRating = 2 " +
           "AND r.isPublic = true AND r.deletedAt IS NULL")
    long countTwoStarReviews(@Param("recipient") User recipient);
    
    /**
     * Get count of 1-star reviews
     */
    @Query("SELECT COUNT(r) FROM Review r " +
           "WHERE r.recipient = :recipient AND r.overallRating = 1 " +
           "AND r.isPublic = true AND r.deletedAt IS NULL")
    long countOneStarReviews(@Param("recipient") User recipient);
    
    // ==========================================
    // Moderation
    // ==========================================
    
    /**
     * Find flagged reviews
     */
    List<Review> findByIsFlaggedTrue();
    
    /**
     * Find flagged reviews with pagination
     */
    Page<Review> findByIsFlaggedTrue(Pageable pageable);
    
    /**
     * Find unverified reviews
     */
    List<Review> findByIsVerifiedFalse();
    
    // ==========================================
    // Recent Reviews
    // ==========================================
    
    /**
     * Get recent reviews for a user
     */
    List<Review> findByRecipientAndIsPublicTrueAndDeletedAtNullOrderByCreatedAtDesc(User recipient, Pageable pageable);
    
    /**
     * Get reviews created after a specific date
     */
    @Query("SELECT r FROM Review r " +
           "WHERE r.recipient = :recipient AND r.createdAt >= :date " +
           "AND r.isPublic = true AND r.deletedAt IS NULL " +
           "ORDER BY r.createdAt DESC")
    List<Review> getRecentReviews(@Param("recipient") User recipient, @Param("date") LocalDateTime date);
    
    // ==========================================
    // Helpfulness
    // ==========================================
    
    /**
     * Find most helpful reviews
     */
    @Query("SELECT r FROM Review r " +
           "WHERE r.recipient = :recipient AND r.isPublic = true AND r.deletedAt IS NULL " +
           "ORDER BY (r.helpfulCount - r.unhelpfulCount) DESC")
    List<Review> getMostHelpfulReviews(@Param("recipient") User recipient, Pageable pageable);
    
    // ==========================================
    // Search
    // ==========================================
    
    /**
     * Search reviews by text content
     */
    @Query("SELECT r FROM Review r " +
           "WHERE r.recipient = :recipient AND r.isPublic = true AND r.deletedAt IS NULL " +
           "AND (LOWER(r.reviewText) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(r.positives) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(r.areasForImprovement) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Review> searchReviews(@Param("recipient") User recipient, @Param("searchTerm") String searchTerm);
}
