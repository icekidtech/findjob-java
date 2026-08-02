package com.findjob.jobboard.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * ApplicationDTO - Data Transfer Object for Job Application submission
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {
    
    @NotBlank(message = "Cover letter is required")
    @Size(min = 10, max = 2000, message = "Cover letter must be between 10 and 2000 characters")
    private String coverLetter;
    
    @DecimalMin(value = "0.1", message = "Proposed budget must be greater than 0")
    private BigDecimal proposedBudget;
    
    private String proposedTimeline;
    
    private String attachmentUrl;
}
