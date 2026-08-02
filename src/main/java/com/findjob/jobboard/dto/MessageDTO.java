package com.findjob.jobboard.dto;

import com.findjob.jobboard.model.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * MessageDTO - Data Transfer Object for Message
 * Used for API responses and requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    
    private Long id;
    
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    
    private Long recipientId;
    private String recipientName;
    
    private String content;
    
    private Boolean isRead;
    private LocalDateTime readAt;
    
    private String attachmentUrl;
    private String attachmentType;
    private Long attachmentSize;
    
    private Long jobId;
    private String jobTitle;
    
    private MessageType messageType;
    
    private LocalDateTime createdAt;
    
    private Boolean isOwn; // Whether current user is sender
}
