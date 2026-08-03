package com.findjob.jobboard.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Message Entity - Direct messaging between users
 * Enables real-time communication between freelancers and clients
 */
@Entity
@Table(name = "messages", indexes = {
    @Index(name = "idx_message_sender_id", columnList = "sender_id"),
    @Index(name = "idx_message_recipient_id", columnList = "recipient_id"),
    @Index(name = "idx_message_conversation", columnList = "sender_id,recipient_id"),
    @Index(name = "idx_message_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ==========================================
    // User References
    // ==========================================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // User sending the message
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient; // User receiving the message
    
    // ==========================================
    // Message Content
    // ==========================================
    
    @NotBlank(message = "Message content is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // Message text
    
    // ==========================================
    // Message Status
    // ==========================================
    
    @Column(nullable = false)
    private Boolean isRead = false; // Whether message has been read
    
    @Column
    private LocalDateTime readAt; // When message was read
    
    // ==========================================
    // Message Attachments
    // ==========================================
    
    @Column(columnDefinition = "TEXT")
    private String attachmentUrl; // URL to attached file (if any)
    
    @Column
    private String attachmentType; // Type of attachment (image, document, etc.)
    
    @Column
    private Long attachmentSize; // Size in bytes
    
    // ==========================================
    // Job Context (Optional)
    // ==========================================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job; // Related job (if conversation is about a job)
    
    // ==========================================
    // Message Type
    // ==========================================
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType = MessageType.TEXT;
    
    // ==========================================
    // Audit Fields
    // ==========================================
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime deletedAt; // Soft delete
    
    // ==========================================
    // Helper Methods
    // ==========================================
    
    /**
     * Mark message as read
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
    
    /**
     * Check if message is active (not deleted)
     */
    public boolean isActive() {
        return deletedAt == null;
    }
    
    /**
     * Soft delete message
     */
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
    
    /**
     * Check if message has attachment
     */
    public boolean hasAttachment() {
        return attachmentUrl != null && !attachmentUrl.isEmpty();
    }
}
