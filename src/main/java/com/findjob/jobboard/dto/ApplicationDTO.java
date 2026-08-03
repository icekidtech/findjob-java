package com.findjob.jobboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    
    private String cvFileUrl;
    
    private String portfolioUrl;
}
