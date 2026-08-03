# File Upload Configuration - Local Storage

## Overview
CV/Resume files are now stored locally on the server instead of using Cloudinary. This makes the application suitable for VPS hosting with no external dependencies for file storage.

## Storage Structure

Files are organized as follows:
```
./uploads/
├── cv/              # CV/Resume files from job applications
│   └── [uuid].pdf
└── portfolio/       # Profile pictures and portfolio files
    └── [uuid].jpg
```

## Configuration

### application.properties
```properties
# Maximum file upload size
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Local file upload directory
file.upload.dir=./uploads/
```

### WebConfig
The application is configured to serve uploaded files from the `/uploads` endpoint:
- Files are accessible at: `http://localhost:8080/uploads/cv/[filename]`
- Static resource handler configured in `WebConfig.java`

## Services

### FileStorageService
Located at: `src/main/java/com/findjob/jobboard/service/FileStorageService.java`

Features:
- **saveCVFile()** - Saves CV/Resume files (PDF, DOC, DOCX)
- **savePortfolioFile()** - Saves image files (JPG, PNG, GIF)
- **deleteFile()** - Removes files from storage
- File validation by type and extension
- Unique filename generation using UUID

## Supported File Types

### CV/Resume Files
- PDF (.pdf)
- Microsoft Word (.doc, .docx)

### Portfolio/Profile Images
- JPEG (.jpg, .jpeg)
- PNG (.png)
- GIF (.gif)

## Usage in Controllers

### JobController - Application Submission
```java
@Autowired
private FileStorageService fileStorageService;

// In submitApplication() method:
if (cvFileUrl != null && !cvFileUrl.isEmpty()) {
    try {
        cvFileUrl_str = fileStorageService.saveCVFile(cvFileUrl);
    } catch (Exception e) {
        // Handle error
    }
}
```

## VPS Deployment

When deploying to a VPS:

1. **Ensure uploads directory exists and is writable:**
   ```bash
   mkdir -p /var/www/findjob/uploads
   chmod 755 /var/www/findjob/uploads
   ```

2. **Update application.properties if needed:**
   ```properties
   file.upload.dir=/var/www/findjob/uploads/
   ```

3. **Serve with Nginx reverse proxy:**
   ```nginx
   location /uploads/ {
       alias /var/www/findjob/uploads/;
       expires 30d;
       add_header Cache-Control "public, immutable";
   }
   ```

4. **Regular backups:**
   - Include the `uploads/` directory in your backup strategy
   - Consider archiving old uploads periodically

## Security Considerations

1. **File Validation**: Only whitelisted MIME types and extensions are accepted
2. **Unique Filenames**: Uses UUID to prevent filename collisions and path traversal attacks
3. **Upload Size Limits**: 10MB max per file via `spring.servlet.multipart.max-file-size`
4. **Gitignore**: `uploads/` directory is in `.gitignore` - user files won't be committed to git

## Troubleshooting

### File Upload Fails
- Check file type is supported (PDF, DOC, DOCX for CV; JPG, PNG, GIF for images)
- Verify file size is under 10MB
- Ensure `uploads/` directory exists and is writable

### Files Not Accessible
- Check WebConfig resource handler is configured correctly
- Verify uploads directory is at the correct path
- Check file permissions on the server

### Disk Space Issues
- Implement file cleanup policies for old uploads
- Monitor disk usage regularly
- Consider archiving uploads periodically

## Related Files
- `src/main/java/com/findjob/jobboard/service/FileStorageService.java`
- `src/main/java/com/findjob/jobboard/config/WebConfig.java`
- `src/main/java/com/findjob/jobboard/controller/JobController.java`
- `src/main/resources/application.properties`
- `.gitignore`
