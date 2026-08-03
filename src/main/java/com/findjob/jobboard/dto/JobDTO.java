package com.findjob.jobboard.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
    
    @NotBlank(message = "Currency is required")
    private String currency; // USD, EUR, GBP, NGN, etc.
    
    @DecimalMin(value = "0.1", message = "Budget must be greater than 0")
    private BigDecimal budget;
    
    @NotBlank(message = "Experience level is required")
    private String experienceLevel; // BEGINNER, INTERMEDIATE, EXPERT
}
