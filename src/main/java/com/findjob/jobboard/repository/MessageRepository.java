package com.findjob.jobboard.repository;

import com.findjob.jobboard.model.Message;
import com.findjob.jobboard.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * MessageRepository - Data access layer for Message entity
 * Handles all database operations for direct messaging
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // ==========================================
    // Find Messages by Conversation
    // ==========================================
    
    /**
     * Find all messages in a conversation between two users
     */
    List<Message> findBySenderAndRecipientAndDeletedAtNullOrderByCreatedAtDesc(User sender, User recipient);
    
    /**
     * Find all messages in a conversation (both directions)
     */
    @Query("SELECT m FROM Message m " +
           "WHERE ((m.sender = :user1 AND m.recipient = :user2) OR (m.sender = :user2 AND m.recipient = :user1)) " +
           "AND m.deletedAt IS NULL " +
           "ORDER BY m.createdAt DESC")
    List<Message> findConversation(@Param("user1") User user1, @Param("user2") User user2);
    
    /**
     * Find conversation with pagination
     */
    @Query("SELECT m FROM Message m " +
           "WHERE ((m.sender = :user1 AND m.recipient = :user2) OR (m.sender = :user2 AND m.recipient = :user1)) " +
           "AND m.deletedAt IS NULL " +
           "ORDER BY m.createdAt DESC")
    Page<Message> findConversation(@Param("user1") User user1, @Param("user2") User user2, Pageable pageable);
    
    // ==========================================
    // Find Received Messages
    // ==========================================
    
    /**
     * Find all messages received by a user
     */
    List<Message> findByRecipientAndDeletedAtNullOrderByCreatedAtDesc(User recipient);
    
    /**
     * Find unread messages received by a user
     */
    List<Message> findByRecipientAndIsReadFalseAndDeletedAtNull(User recipient);

    /**
     * Find unread messages in a specific conversation
     */
    List<Message> findBySenderAndRecipientAndIsReadFalseAndDeletedAtNull(User sender, User recipient);
    
    /**
     * Find unread messages count
     */
    long countByRecipientAndIsReadFalseAndDeletedAtNull(User recipient);
    
    /**
     * Find received messages with pagination
     */
    Page<Message> findByRecipientAndDeletedAtNullOrderByCreatedAtDesc(User recipient, Pageable pageable);
    
    // ==========================================
    // Find Sent Messages
    // ==========================================
    
    /**
     * Find all messages sent by a user
     */
    List<Message> findBySenderAndDeletedAtNullOrderByCreatedAtDesc(User sender);
    
    /**
     * Find sent messages with pagination
     */
    Page<Message> findBySenderAndDeletedAtNullOrderByCreatedAtDesc(User sender, Pageable pageable);
    
    // ==========================================
    // Find Messages by Job
    // ==========================================
    
    /**
     * Find messages related to a specific job
     */
    List<Message> findByJobAndDeletedAtNullOrderByCreatedAtDesc(User recipient);
    
    // ==========================================
    // Find Single Message
    // ==========================================
    
    /**
     * Find a specific message by ID
     */
    Optional<Message> findByIdAndDeletedAtNull(Long id);
    
    // ==========================================
    // Count Operations
    // ==========================================
    
    /**
     * Count total messages in a conversation
     */
    @Query("SELECT COUNT(m) FROM Message m " +
           "WHERE ((m.sender = :user1 AND m.recipient = :user2) OR (m.sender = :user2 AND m.recipient = :user1)) " +
           "AND m.deletedAt IS NULL")
    long countConversation(@Param("user1") User user1, @Param("user2") User user2);
    
    /**
     * Count unread messages from specific sender
     */
    long countBySenderAndRecipientAndIsReadFalseAndDeletedAtNull(User sender, User recipient);
    
    /**
     * Count total messages sent by user
     */
    long countBySenderAndDeletedAtNull(User sender);
    
    /**
     * Count total messages received by user
     */
    long countByRecipientAndDeletedAtNull(User recipient);
    
    // ==========================================
    // Search and Filter
    // ==========================================
    
    /**
     * Search messages by content
     */
    @Query("SELECT m FROM Message m " +
           "WHERE ((m.sender = :user1 AND m.recipient = :user2) OR (m.sender = :user2 AND m.recipient = :user1)) " +
           "AND m.deletedAt IS NULL " +
           "AND LOWER(m.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY m.createdAt DESC")
    List<Message> searchInConversation(@Param("user1") User user1, @Param("user2") User user2, @Param("searchTerm") String searchTerm);
    
    /**
     * Find messages with attachments in conversation
     */
    @Query("SELECT m FROM Message m " +
           "WHERE ((m.sender = :user1 AND m.recipient = :user2) OR (m.sender = :user2 AND m.recipient = :user1)) " +
           "AND m.deletedAt IS NULL " +
           "AND m.attachmentUrl IS NOT NULL " +
           "ORDER BY m.createdAt DESC")
    List<Message> findAttachmentsInConversation(@Param("user1") User user1, @Param("user2") User user2);
    
    // ==========================================
    // Recent Messages & Statistics
    // ==========================================
    
    /**
     * Find last message in conversation
     */
    @Query("SELECT m FROM Message m " +
           "WHERE ((m.sender = :user1 AND m.recipient = :user2) OR (m.sender = :user2 AND m.recipient = :user1)) " +
           "AND m.deletedAt IS NULL " +
           "ORDER BY m.createdAt DESC " +
           "LIMIT 1")
    Optional<Message> findLastMessage(@Param("user1") User user1, @Param("user2") User user2);
    
    /**
     * Find messages created after a specific date
     */
    List<Message> findByRecipientAndCreatedAtAfterAndDeletedAtNullOrderByCreatedAtDesc(User recipient, LocalDateTime date);
    
    /**
     * Find active conversations (users who have messaged)
     */
    @Query("SELECT DISTINCT CASE WHEN m.sender = :user THEN m.recipient ELSE m.sender END " +
           "FROM Message m " +
           "WHERE (m.sender = :user OR m.recipient = :user) " +
           "AND m.deletedAt IS NULL " +
           "ORDER BY m.createdAt DESC")
    List<User> findActiveConversationPartners(@Param("user") User user, Pageable pageable);
    
    /**
     * Find conversations with unread messages
     */
    @Query("SELECT DISTINCT CASE WHEN m.sender = :user THEN m.recipient ELSE m.sender END " +
           "FROM Message m " +
           "WHERE m.recipient = :user " +
           "AND m.isRead = false " +
           "AND m.deletedAt IS NULL")
    List<User> findConversationsWithUnreadMessages(@Param("user") User user);
}
