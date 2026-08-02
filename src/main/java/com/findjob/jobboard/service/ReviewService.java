package com.findjob.jobboard.service;

import com.findjob.jobboard.model.Job;
import com.findjob.jobboard.model.JobApplication;
import com.findjob.jobboard.model.Review;
import com.findjob.jobboard.model.User;
import com.findjob.jobboard.repository.JobRepository;
import com.findjob.jobboard.repository.ReviewRepository;
import com.findjob.jobboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ReviewService - Business logic for reviews and ratings
 * Handles review creation, management, reputation calculations, and analytics
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    
    // ==========================================
    // Review Creation
    // ==========================================
    
    /**
     * Create a new review
     * Validates job is completed and review hasn't been posted yet
     */
    public Review createReview(Long jobId, Long authorId, Long recipientId,
                               Integer overallRating, String reviewText,
                               Integer qualityRating, Integer communicationRating,
                               Integer professionalism, Integer timeliness,
                               String positives, String areasForImprovement) {
        log.info("Creating review for job {} by user {} for user {}", jobId, authorId, recipientId);
        
        // Fetch entities
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Author user not found"));
        
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("Recipient user not found"));
        
        // Validate review doesn't already exist for this job
        Optional<Review> existingReview = reviewRepository.findByAuthorAndRecipientAndJob(author, recipient, job);
        if (existingReview.isPresent()) {
            throw new IllegalArgumentException("Review already exists for this job from this user");
        }
        
        // Validate author and recipient are different
        if (authorId.equals(recipientId)) {
            throw new IllegalArgumentException("Cannot review yourself");
        }
        
        // Create review
        Review review = Review.builder()
                .job(job)
                .author(author)
                .recipient(recipient)
                .overallRating(overallRating)
                .reviewText(reviewText)
                .qualityRating(qualityRating)
                .communicationRating(communicationRating)
                .professionalism(professionalism)
                .timeliness(timeliness)
                .positives(positives)
                .areasForImprovement(areasForImprovement)
                .isPublic(true)
                .isAnonymous(false)
                .isVerified(false)
                .build();
        
        review = reviewRepository.save(review);
        log.info("Review created successfully with ID: {}", review.getId());
        
        // Update recipient's reputation score
        updateUserReputationFromReviews(recipient);
        
        return review;
    }
    
    /**
     * Create anonymous review
     */
    public Review createAnonymousReview(Long jobId, Long authorId, Long recipientId,
                                        Integer overallRating, String reviewText) {
        Review review = createReview(jobId, authorId, recipientId, overallRating, reviewText, 
                                     null, null, null, null, null, null);
        review.setIsAnonymous(true);
        return reviewRepository.save(review);
    }
    
    // ==========================================
    // Retrieve Reviews
    // ==========================================
    
    /**
     * Get all active reviews for a user
     */
    public List<Review> getReviewsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return reviewRepository.findByRecipientAndIsPublicTrueAndDeletedAtNull(user);
    }
    
    /**
     * Get reviews with pagination
     */
    public Page<Review> getReviewsForUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return reviewRepository.findByRecipientAndIsPublicTrueAndDeletedAtNull(user, pageable);
    }
    
    /**
     * Get recent reviews for a user
     */
    public List<Review> getRecentReviewsForUser(Long userId, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Pageable pageable = PageRequest.of(0, limit);
        return reviewRepository.findByRecipientAndIsPublicTrueAndDeletedAtNullOrderByCreatedAtDesc(user, pageable);
    }
    
    /**
     * Get reviews for a specific job
     */
    public List<Review> getReviewsForJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        
        return reviewRepository.findByJobAndIsPublicTrueAndDeletedAtNull(job);
    }
    
    /**
     * Get reviews written by a user
     */
    public List<Review> getReviewsWrittenByUser(Long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return reviewRepository.findByAuthor(author);
    }
    
    /**
     * Get a specific review
     */
    public Optional<Review> getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId);
    }
    
    /**
     * Get most helpful reviews for a user
     */
    public List<Review> getMostHelpfulReviews(Long userId, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Pageable pageable = PageRequest.of(0, limit);
        return reviewRepository.getMostHelpfulReviews(user, pageable);
    }
    
    // ==========================================
    // Review Management
    // ==========================================
    
    /**
     * Update a review
     */
    public Review updateReview(Long reviewId, String reviewText, 
                               Integer qualityRating, Integer communicationRating,
                               Integer professionalism, Integer timeliness) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        
        if (!review.isActive()) {
            throw new IllegalArgumentException("Cannot update inactive review");
        }
        
        review.setReviewText(reviewText);
        if (qualityRating != null) review.setQualityRating(qualityRating);
        if (communicationRating != null) review.setCommunicationRating(communicationRating);
        if (professionalism != null) review.setProfessionalism(professionalism);
        if (timeliness != null) review.setTimeliness(timeliness);
        
        review = reviewRepository.save(review);
        updateUserReputationFromReviews(review.getRecipient());
        
        return review;
    }
    
    /**
     * Delete review (soft delete)
     */
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        
        // Only author can delete their own review
        if (!review.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("Only author can delete this review");
        }
        
        review.delete();
        reviewRepository.save(review);
        
        // Update recipient's reputation
        updateUserReputationFromReviews(review.getRecipient());
        log.info("Review {} deleted by user {}", reviewId, userId);
    }
    
    /**
     * Toggle review visibility
     */
    public void toggleReviewVisibility(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        
        // Only author can toggle visibility
        if (!review.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("Only author can toggle visibility");
        }
        
        review.setIsPublic(!review.getIsPublic());
        reviewRepository.save(review);
    }
    
    // ==========================================
    // Helpful/Unhelpful
    // ==========================================
    
    /**
     * Mark review as helpful
     */
    public void markAsHelpful(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        
        review.markHelpful();
        reviewRepository.save(review);
    }
    
    /**
     * Mark review as unhelpful
     */
    public void markAsUnhelpful(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        
        review.markUnhelpful();
        reviewRepository.save(review);
    }
    
    // ==========================================
    // Moderation
    // ==========================================
    
    /**
     * Flag review for moderation
     */
    public void flagReview(Long reviewId, String reason) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        
        review.flag(reason);
        reviewRepository.save(review);
        log.warn("Review {} flagged for moderation: {}", reviewId, reason);
    }
    
    /**
     * Verify review (admin)
     */
    public void verifyReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        
        review.setIsVerified(true);
        reviewRepository.save(review);
    }
    
    /**
     * Get flagged reviews for moderation
     */
    public Page<Review> getFlaggedReviews(Pageable pageable) {
        return reviewRepository.findByIsFlaggedTrue(pageable);
    }
    
    // ==========================================
    // Rating Statistics
    // ==========================================
    
    /**
     * Get average rating for a user
     */
    public Double getAverageRating(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return reviewRepository.getAverageRating(user);
    }
    
    /**
     * Get detailed rating statistics for a user
     */
    public Map<String, Double> getDetailedRatingStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Map<String, Double> stats = new HashMap<>();
        stats.put("overall", reviewRepository.getAverageRating(user));
        stats.put("quality", reviewRepository.getAverageQualityRating(user));
        stats.put("communication", reviewRepository.getAverageCommunicationRating(user));
        stats.put("professionalism", reviewRepository.getAverageProfessionalism(user));
        stats.put("timeliness", reviewRepository.getAverageTimeliness(user));
        
        return stats;
    }
    
    /**
     * Get rating distribution for a user
     */
    public Map<Integer, Long> getRatingDistribution(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Map<Integer, Long> distribution = new HashMap<>();
        distribution.put(5, reviewRepository.countFiveStarReviews(user));
        distribution.put(4, reviewRepository.countFourStarReviews(user));
        distribution.put(3, reviewRepository.countThreeStarReviews(user));
        distribution.put(2, reviewRepository.countTwoStarReviews(user));
        distribution.put(1, reviewRepository.countOneStarReviews(user));
        
        return distribution;
    }
    
    /**
     * Count total reviews for a user
     */
    public long getReviewCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return reviewRepository.countByRecipientAndIsPublicTrueAndDeletedAtNull(user);
    }
    
    // ==========================================
    // Reputation Update
    // ==========================================
    
    /**
     * Update user's reputation score based on average rating
     */
    @Transactional
    public void updateUserReputationFromReviews(User user) {
        Double averageRating = reviewRepository.getAverageRating(user);
        
        if (averageRating != null) {
            user.setReputationScore(averageRating);
            user.updateTier(); // Update tier based on new reputation
            
            userRepository.save(user);
            log.info("Updated reputation for user {} from reviews to {}", user.getId(), averageRating);
        }
    }
    
    // ==========================================
    // Search
    // ==========================================
    
    /**
     * Search reviews by content
     */
    public List<Review> searchReviews(Long userId, String searchTerm) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return reviewRepository.searchReviews(user, searchTerm);
    }
    
    // ==========================================
    // Approval/Eligibility
    // ==========================================
    
    /**
     * Check if user can review another user
     * Condition: Job must be completed and parties must have worked together
     */
    public boolean canReviewUser(Long jobId, Long authorId, Long recipientId) {
        try {
            Job job = jobRepository.findById(jobId).orElse(null);
            User author = userRepository.findById(authorId).orElse(null);
            User recipient = userRepository.findById(recipientId).orElse(null);
            
            if (job == null || author == null || recipient == null) {
                return false;
            }
            
            // Check if job is completed
            if (!job.getJobStatus().equals(com.findjob.jobboard.model.JobStatus.COMPLETED)) {
                return false;
            }
            
            // Check if review already exists
            Optional<Review> existing = reviewRepository.findByAuthorAndRecipientAndJob(author, recipient, job);
            
            return existing.isEmpty() && !authorId.equals(recipientId);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get reviews from last 30 days
     */
    public List<Review> getRecentReviews(Long userId, int days) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return reviewRepository.getRecentReviews(user, since);
    }
}
