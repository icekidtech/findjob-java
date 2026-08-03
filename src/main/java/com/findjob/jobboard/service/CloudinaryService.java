package com.findjob.jobboard.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * CloudinaryService - Handles file uploads to Cloudinary
 */
@Service
public class CloudinaryService {
    
    private final Cloudinary cloudinary;
    
    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }
    
    /**
     * Upload a file to Cloudinary
     * @param file The file to upload
     * @param folder The folder in Cloudinary to store the file
     * @return The URL of the uploaded file
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "resource_type", "auto",
                        "use_filename", true,
                        "unique_filename", true
                )
        );
        
        return (String) uploadResult.get("secure_url");
    }
    
    /**
     * Upload CV/Resume file
     */
    public String uploadCVFile(MultipartFile file) throws IOException {
        return uploadFile(file, "findjob/cv");
    }
    
    /**
     * Delete a file from Cloudinary by URL
     */
    public boolean deleteFile(String fileUrl) {
        try {
            // Extract public_id from URL
            String publicId = extractPublicId(fileUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error deleting file from Cloudinary: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract public_id from Cloudinary URL
     */
    private String extractPublicId(String url) {
        try {
            // URL format: https://res.cloudinary.com/cloud-name/image/upload/v123/folder/filename.ext
            String[] parts = url.split("/");
            if (parts.length >= 2) {
                String filename = parts[parts.length - 1];
                // Remove extension
                return filename.substring(0, filename.lastIndexOf('.'));
            }
        } catch (Exception e) {
            System.err.println("Error extracting public_id: " + e.getMessage());
        }
        return null;
    }
}
