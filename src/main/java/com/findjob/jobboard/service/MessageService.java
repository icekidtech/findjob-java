package com.findjob.jobboard.service;

import com.findjob.jobboard.dto.MessageDTO;
import com.findjob.jobboard.model.Message;
import com.findjob.jobboard.model.MessageType;
import com.findjob.jobboard.model.User;
import com.findjob.jobboard.repository.MessageRepository;
import com.findjob.jobboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MessageService - Business logic for direct messaging
 * Handles message creation, retrieval, status management, and conversation handling
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    
    // ==========================================
    // Send Message
    // ==========================================
    
    /**
     * Send a text message to another user
     */
    public Message sendMessage(Long senderId, Long recipientId, String content) {
        return sendMessage(senderId, recipientId, content, MessageType.TEXT, null, null, null, null);
    }
    
    /**
     * Send a message with optional attachment
     */
    public Message sendMessage(Long senderId, Long recipientId, String content, 
                              MessageType messageType, String attachmentUrl, 
                              String attachmentType, Long attachmentSize, Long jobId) {
        log.info("Sending message from user {} to user {}", senderId, recipientId);
        
        // Fetch users
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender user not found"));
        
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("Recipient user not found"));
        
        // Validate users are different
        if (senderId.equals(recipientId)) {
            throw new IllegalArgumentException("User cannot send message to themselves");
        }
        
        // Create message
        Message message = Message.builder()
                .sender(sender)
                .recipient(recipient)
                .content(content)
                .messageType(messageType)
                .attachmentUrl(attachmentUrl)
                .attachmentType(attachmentType)
                .attachmentSize(attachmentSize)
                .isRead(false)
                .build();
        
        if (jobId != null) {
            // Set job context if provided
            // message.setJob(jobRepository.findById(jobId).orElse(null));
        }
        
        message = messageRepository.save(message);
        log.info("Message sent successfully with ID: {}", message.getId());
        
        return message;
    }
    
    // ==========================================
    // Get Messages
    // ==========================================
    
    /**
     * Get conversation between two users
     */
    public List<Message> getConversation(Long userId1, Long userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new IllegalArgumentException("User 1 not found"));
        
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new IllegalArgumentException("User 2 not found"));
        
        return messageRepository.findConversation(user1, user2);
    }
    
    /**
     * Get conversation with pagination
     */
    public Page<Message> getConversation(Long userId1, Long userId2, Pageable pageable) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new IllegalArgumentException("User 1 not found"));
        
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new IllegalArgumentException("User 2 not found"));
        
        return messageRepository.findConversation(user1, user2, pageable);
    }
    
    /**
     * Get all received messages
     */
    public List<Message> getReceivedMessages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return messageRepository.findByRecipientAndDeletedAtNullOrderByCreatedAtDesc(user);
    }
    
    /**
     * Get received messages with pagination
     */
    public Page<Message> getReceivedMessages(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return messageRepository.findByRecipientAndDeletedAtNullOrderByCreatedAtDesc(user, pageable);
    }
    
    /**
     * Get all sent messages
     */
    public List<Message> getSentMessages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return messageRepository.findBySenderAndDeletedAtNullOrderByCreatedAtDesc(user);
    }
    
    /**
     * Get sent messages with pagination
     */
    public Page<Message> getSentMessages(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return messageRepository.findBySenderAndDeletedAtNullOrderByCreatedAtDesc(user, pageable);
    }
    
    /**
     * Get unread messages for a user
     */
    public List<Message> getUnreadMessages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return messageRepository.findByRecipientAndIsReadFalseAndDeletedAtNull(user);
    }
    
    /**
     * Get a specific message
     */
    public Optional<Message> getMessage(Long messageId) {
        return messageRepository.findByIdAndDeletedAtNull(messageId);
    }
    
    // ==========================================
    // Mark as Read
    // ==========================================
    
    /**
     * Mark a single message as read
     */
    public Message markAsRead(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
        
        // Verify user is the recipient
        if (!message.getRecipient().getId().equals(userId)) {
            throw new IllegalArgumentException("Only recipient can mark message as read");
        }
        
        if (!Boolean.TRUE.equals(message.getIsRead())) {
            message.markAsRead();
            messageRepository.save(message);
            log.info("Message {} marked as read", messageId);
        }
        
        return message;
    }
    
    /**
     * Mark all messages from sender as read
     */
    public void markConversationAsRead(Long userId, Long senderId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        
        List<Message> unreadMessages = messageRepository
                .findBySenderAndRecipientAndIsReadFalseAndDeletedAtNull(sender, user);
        
        for (Message message : unreadMessages) {
            message.markAsRead();
        }
        
        if (!unreadMessages.isEmpty()) {
            messageRepository.saveAll(unreadMessages);
            log.info("Marked {} messages as read", unreadMessages.size());
        }
    }
    
    // ==========================================
    // Delete Message
    // ==========================================
    
    /**
     * Delete (soft delete) a message
     */
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
        
        // Verify user is sender or recipient
        if (!message.getSender().getId().equals(userId) && !message.getRecipient().getId().equals(userId)) {
            throw new IllegalArgumentException("User cannot delete this message");
        }
        
        message.delete();
        messageRepository.save(message);
        log.info("Message {} deleted by user {}", messageId, userId);
    }
    
    // ==========================================
    // Search and Filter
    // ==========================================
    
    /**
     * Search in conversation
     */
    public List<Message> searchInConversation(Long userId1, Long userId2, String searchTerm) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new IllegalArgumentException("User 1 not found"));
        
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new IllegalArgumentException("User 2 not found"));
        
        return messageRepository.searchInConversation(user1, user2, searchTerm);
    }
    
    /**
     * Get attachments in conversation
     */
    public List<Message> getAttachmentsInConversation(Long userId1, Long userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new IllegalArgumentException("User 1 not found"));
        
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new IllegalArgumentException("User 2 not found"));
        
        return messageRepository.findAttachmentsInConversation(user1, user2);
    }
    
    // ==========================================
    // Conversation Management
    // ==========================================
    
    /**
     * Get all active conversations for a user
     */
    public List<User> getActiveConversations(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return messageRepository.findActiveConversationPartners(user, pageable);
    }
    
    /**
     * Get conversations with unread messages
     */
    public List<User> getConversationsWithUnreadMessages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return messageRepository.findConversationsWithUnreadMessages(user);
    }
    
    /**
     * Get unread message count for a user
     */
    public long getUnreadMessageCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return messageRepository.countByRecipientAndIsReadFalseAndDeletedAtNull(user);
    }
    
    /**
     * Get unread count from specific sender
     */
    public long getUnreadCountFromSender(Long userId, Long senderId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));
        
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        
        return messageRepository.countBySenderAndRecipientAndIsReadFalseAndDeletedAtNull(sender, user);
    }
    
    /**
     * Get conversation count
     */
    public long getConversationMessageCount(Long userId1, Long userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new IllegalArgumentException("User 1 not found"));
        
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new IllegalArgumentException("User 2 not found"));
        
        return messageRepository.countConversation(user1, user2);
    }
    
    /**
     * Get last message in conversation
     */
    public Optional<Message> getLastMessage(Long userId1, Long userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new IllegalArgumentException("User 1 not found"));
        
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new IllegalArgumentException("User 2 not found"));
        
        return messageRepository.findLastMessage(user1, user2);
    }
    
    // ==========================================
    // DTO Conversion
    // ==========================================
    
    /**
     * Convert Message entity to MessageDTO
     */
    public MessageDTO convertToDTO(Message message, Long currentUserId) {
        return MessageDTO.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFirstName() + " " + message.getSender().getLastName())
                .recipientId(message.getRecipient().getId())
                .recipientName(message.getRecipient().getFirstName() + " " + message.getRecipient().getLastName())
                .content(message.getContent())
                .isRead(message.getIsRead())
                .readAt(message.getReadAt())
                .attachmentUrl(message.getAttachmentUrl())
                .attachmentType(message.getAttachmentType())
                .attachmentSize(message.getAttachmentSize())
                .jobId(message.getJob() != null ? message.getJob().getId() : null)
                .jobTitle(message.getJob() != null ? message.getJob().getTitle() : null)
                .messageType(message.getMessageType())
                .createdAt(message.getCreatedAt())
                .isOwn(message.getSender().getId().equals(currentUserId))
                .build();
    }
}
