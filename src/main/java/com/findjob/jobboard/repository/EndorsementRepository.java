package com.findjob.jobboard.repository;

import com.findjob.jobboard.model.Endorsement;
import com.findjob.jobboard.model.EndorsementType;
import com.findjob.jobboard.model.Skill;
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
 * EndorsementRepository - Data access layer for Endorsement entity
 * Handles all database operations for skill endorsements
 */
@Repository
public interface EndorsementRepository extends JpaRepository<Endorsement, Long> {
    
    // ==========================================
    // Find Endorsements by User
    // ==========================================
    
    /**
     * Find all endorsements given by a user
     */
    List<Endorsement> findByEndorser(User endorser);
    
    /**
     * Find all endorsements received by a user
     */
    List<Endorsement> findByEndorsedUser(User endorsedUser);
    
    /**
     * Find active endorsements received by a user
     */
    List<Endorsement> findByEndorsedUserAndIsActiveTrue(User endorsedUser);
    
    /**
     * Find endorsements received by user with pagination
     */
    Page<Endorsement> findByEndorsedUserAndIsActiveTrue(User endorsedUser, Pageable pageable);
    
    // ==========================================
    // Find by Skill
    // ==========================================
    
    /**
     * Find all endorsements for a specific skill by a user
     */
    List<Endorsement> findByEndorsedUserAndSkill(User endorsedUser, Skill skill);
    
    /**
     * Find active endorsements for a specific skill by a user
     */
    List<Endorsement> findByEndorsedUserAndSkillAndIsActiveTrue(User endorsedUser, Skill skill);
    
    /**
     * Find endorsements by skill and type
     */
    List<Endorsement> findBySkillAndEndorsementTypeAndIsActiveTrue(Skill skill, EndorsementType type);
    
    // ==========================================
    // Find by Endorsement Type
    // ==========================================
    
    /**
     * Find endorsements of specific type received by user
     */
    List<Endorsement> findByEndorsedUserAndEndorsementType(User endorsedUser, EndorsementType type);
    
    /**
     * Find active endorsements of specific type received by user
     */
    List<Endorsement> findByEndorsedUserAndEndorsementTypeAndIsActiveTrue(User endorsedUser, EndorsementType type);
    
    // ==========================================
    // Find Specific Endorsement
    // ==========================================
    
    /**
     * Find endorsement by endorser, endorsed user, and skill (unique constraint check)
     */
    Optional<Endorsement> findByEndorserAndEndorsedUserAndSkill(User endorser, User endorsedUser, Skill skill);
    
    /**
     * Find active endorsement by endorser, endorsed user, and skill
     */
    Optional<Endorsement> findByEndorserAndEndorsedUserAndSkillAndIsActiveTrue(User endorser, User endorsedUser, Skill skill);
    
    // ==========================================
    // Count Endorsements
    // ==========================================
    
    /**
     * Count active endorsements for a user
     */
    long countByEndorsedUserAndIsActiveTrue(User endorsedUser);
    
    /**
     * Count endorsements for a specific skill by user
     */
    long countByEndorsedUserAndSkillAndIsActiveTrue(User endorsedUser, Skill skill);
    
    /**
     * Count endorsements of specific type for a user
     */
    long countByEndorsedUserAndEndorsementTypeAndIsActiveTrue(User endorsedUser, EndorsementType type);
    
    /**
     * Count endorsements given by a user
     */
    long countByEndorser(User endorser);
    
    // ==========================================
    // Check Endorsement Status
    // ==========================================
    
    /**
     * Check if user has endorsed another user for a skill in the last month
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
           "FROM Endorsement e " +
           "WHERE e.endorser = :endorser AND e.endorsedUser = :endorsedUser AND e.skill = :skill " +
           "AND e.isActive = true AND e.createdAt >= :monthAgo")
    boolean hasRecentEndorsement(@Param("endorser") User endorser, 
                                 @Param("endorsedUser") User endorsedUser,
                                 @Param("skill") Skill skill,
                                 @Param("monthAgo") LocalDateTime monthAgo);
    
    /**
     * Check if endorsement exists and is active
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
           "FROM Endorsement e " +
           "WHERE e.endorser = :endorser AND e.endorsedUser = :endorsedUser AND e.skill = :skill " +
           "AND e.isActive = true")
    boolean hasActiveEndorsement(@Param("endorser") User endorser,
                                 @Param("endorsedUser") User endorsedUser,
                                 @Param("skill") Skill skill);
    
    // ==========================================
    // Custom Queries
    // ==========================================
    
    /**
     * Find top endorsed users for a skill
     */
    @Query("SELECT e.endorsedUser FROM Endorsement e " +
           "WHERE e.skill = :skill AND e.isActive = true " +
           "GROUP BY e.endorsedUser " +
           "ORDER BY COUNT(e) DESC")
    List<User> findTopEndorsedUsersForSkill(@Param("skill") Skill skill, Pageable pageable);
    
    /**
     * Find endorsements expiring soon
     */
    @Query("SELECT e FROM Endorsement e " +
           "WHERE e.isActive = true AND e.expiresAt IS NOT NULL " +
           "AND e.expiresAt BETWEEN :now AND :futureDate " +
           "ORDER BY e.expiresAt ASC")
    List<Endorsement> findExpiringEndorsements(@Param("now") LocalDateTime now,
                                               @Param("futureDate") LocalDateTime futureDate);
    
    /**
     * Find expired endorsements to deactivate
     */
    @Query("SELECT e FROM Endorsement e " +
           "WHERE e.isActive = true AND e.expiresAt IS NOT NULL " +
           "AND e.expiresAt < :now")
    List<Endorsement> findExpiredEndorsements(@Param("now") LocalDateTime now);
}
