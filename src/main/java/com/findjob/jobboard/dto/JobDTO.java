package com.findjob.jobboard.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JobDTO - Data Transfer Object for Job creation/updates
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {
    
    @NotBlank(message = "Job title is required")
    @Size(min = 5, max = 255, message = "Title must be between 5 and 255 characters")
    private String title;
    
    @NotBlank(message = "Job description is required")
    @Size(min = 20, max = 5000, message = "Description must be between 20 and 5000 characters")
    private String description;
    
    private String category;
    
    @NotBlank(message = "Budget type is required")
    private String budgetType; // FIXED or HOURLY
    
    @DecimalMin(value = "0.1", message = "Minimum budget must be greater than 0")
    private BigDecimal budgetMin;
    
    @DecimalMin(value = "0.1", message = "Maximum budget must be greater than 0")
    private BigDecimal budgetMax;
    
    @NotBlank(message = "Experience level is required")
    private String experienceLevel; // ENTRY_LEVEL, INTERMEDIATE, EXPERT
    
    private String duration; // 1-3 months, 3-6 months, etc.
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime deadline;
    
    private String attachmentUrl;
}
