package com.findjob.jobboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ReviewDTO - Data Transfer Object for Review entity
 * Used for API responses and data exchange
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    
    private Long id;
    
    private Long jobId;
    private String jobTitle;
    
    private Long authorId;
    private String authorName;
    private String authorProfile;
    private Boolean isAnonymous;
    
    private Long recipientId;
    private String recipientName;
    
    // Ratings
    private Integer overallRating;
    private Integer qualityRating;
    private Integer communicationRating;
    private Integer professionalism;
    private Integer timeliness;
    private Double averageDetailedRating;
    
    // Review Content
    private String reviewText;
    private String positives;
    private String areasForImprovement;
    private String wouldHireAgain;
    
    // Metadata
    private Boolean isPublic;
    private Boolean isVerified;
    private Boolean isFlagged;
    
    // Engagement
    private Integer helpfulCount;
    private Integer unhelpfulCount;
    private Integer helpfulnessScore;
    
    // Dates
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Moderation
    private String moderationNotes;
}
