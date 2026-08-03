package com.findjob.jobboard.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * FileStorageService - Handles local file storage for CV/Resume uploads
 * Stores files in the configured upload directory
 */
@Service
public class FileStorageService {
    
    @Value("${file.upload.dir:uploads/}")
    private String uploadDir;
    
    /**
     * Save CV/Resume file locally
     * Returns the relative URL path to access the file
     */
    public String saveCVFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        
        // Validate file type (only PDF and common document formats)
        String contentType = file.getContentType();
        if (!isValidCVFileType(contentType, file.getOriginalFilename())) {
            throw new IllegalArgumentException("Invalid file type. Only PDF, DOC, DOCX allowed.");
        }
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir, "cv");
        Files.createDirectories(uploadPath);
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID() + "." + fileExtension;
        
        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.write(filePath, file.getBytes());
        
        // Return relative URL path (e.g., "/uploads/cv/uuid-123.pdf")
        return "/uploads/cv/" + uniqueFilename;
    }
    
    /**
     * Save portfolio/profile picture file locally
     * Returns the relative URL path to access the file
     */
    public String savePortfolioFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        
        // Validate file type (only images)
        String contentType = file.getContentType();
        if (!isValidImageFileType(contentType)) {
            throw new IllegalArgumentException("Invalid file type. Only JPG, PNG, GIF allowed.");
        }
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir, "portfolio");
        Files.createDirectories(uploadPath);
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID() + "." + fileExtension;
        
        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.write(filePath, file.getBytes());
        
        // Return relative URL path (e.g., "/uploads/portfolio/uuid-456.jpg")
        return "/uploads/portfolio/" + uniqueFilename;
    }
    
    /**
     * Delete file from local storage
     */
    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        
        // Remove leading slash if present
        String relativePath = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;
        Path filePath = Paths.get(relativePath);
        
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
    
    /**
     * Check if file type is valid for CV/Resume
     */
    private boolean isValidCVFileType(String contentType, String filename) {
        if (contentType == null || filename == null) {
            return false;
        }
        
        String lowerFilename = filename.toLowerCase();
        return (contentType.equals("application/pdf") || 
                contentType.equals("application/msword") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) &&
               (lowerFilename.endsWith(".pdf") || 
                lowerFilename.endsWith(".doc") || 
                lowerFilename.endsWith(".docx"));
    }
    
    /**
     * Check if file type is valid for images
     */
    private boolean isValidImageFileType(String contentType) {
        if (contentType == null) {
            return false;
        }
        
        return contentType.startsWith("image/") && 
               (contentType.equals("image/jpeg") || 
                contentType.equals("image/png") || 
                contentType.equals("image/gif"));
    }
    
    /**
     * Extract file extension
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "bin";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
