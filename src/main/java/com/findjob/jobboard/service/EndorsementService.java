package com.findjob.jobboard.service;

import com.findjob.jobboard.model.*;
import com.findjob.jobboard.repository.EndorsementRepository;
import com.findjob.jobboard.repository.SkillRepository;
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
 * EndorsementService - Business logic for skill endorsements
 * Handles endorsement creation, validation, management, and reputation updates
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EndorsementService {
    
    private final EndorsementRepository endorsementRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    
    // ==========================================
    // Endorsement Creation
    // ==========================================
    
    /**
     * Create a new endorsement
     * Validates endorser and endorsed user are different
     * Prevents duplicate endorsements within a month
     */
    public Endorsement createEndorsement(Long endorserId, Long endorsedUserId, Long skillId, 
                                         EndorsementType type, String message) {
        log.info("Creating endorsement from user {} for user {} skill {}", endorserId, endorsedUserId, skillId);
        
        // Fetch users and skill
        User endorser = userRepository.findById(endorserId)
                .orElseThrow(() -> new IllegalArgumentException("Endorser user not found"));
        
        User endorsedUser = userRepository.findById(endorsedUserId)
                .orElseThrow(() -> new IllegalArgumentException("Endorsed user not found"));
        
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));
        
        // Validate endorsement is not self-endorsement
        if (endorserId.equals(endorsedUserId)) {
            throw new IllegalArgumentException("User cannot endorse themselves");
        }
        
        // Check if endorsement already exists (active)
        Optional<Endorsement> existingEndorsement = endorsementRepository
                .findByEndorserAndEndorsedUserAndSkillAndIsActiveTrue(endorser, endorsedUser, skill);
        
        if (existingEndorsement.isPresent()) {
            throw new IllegalArgumentException("Active endorsement already exists for this skill");
        }
        
        // Create new endorsement
        Endorsement endorsement = Endorsement.builder()
                .endorser(endorser)
                .endorsedUser(endorsedUser)
                .skill(skill)
                .endorsementType(type)
                .message(message)
                .isActive(true)
                .expiresAt(calculateExpirationDate(type))
                .build();
        
        endorsement = endorsementRepository.save(endorsement);
        log.info("Endorsement created successfully with ID: {}", endorsement.getId());
        
        // Update endorsed user's reputation if needed
        updateUserReputationScore(endorsedUser);
        
        return endorsement;
    }
    
    /**
     * Create peer-to-peer endorsement
     */
    public Endorsement createPeerEndorsement(Long endorserId, Long endorsedUserId, Long skillId, String message) {
        return createEndorsement(endorserId, endorsedUserId, skillId, EndorsementType.PEER, message);
    }
    
    /**
     * Create client endorsement (typically after project completion)
     */
    public Endorsement createClientEndorsement(Long clientId, Long freelancerId, Long skillId, String message) {
        return createEndorsement(clientId, freelancerId, skillId, EndorsementType.CLIENT, message);
    }
    
    /**
     * Create verified endorsement (admin/system)
     */
    public Endorsement createVerifiedEndorsement(Long freelancerId, Long skillId, String message) {
        return createEndorsement(1L, freelancerId, skillId, EndorsementType.VERIFIED, message); // Assuming admin ID is 1
    }
    
    // ==========================================
    // Retrieve Endorsements
    // ==========================================
    
    /**
     * Get all active endorsements for a user
     */
    public List<Endorsement> getEndorsementsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return endorsementRepository.findByEndorsedUserAndIsActiveTrue(user);
    }
    
    /**
     * Get active endorsements for a user with pagination
     */
    public Page<Endorsement> getEndorsementsForUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return endorsementRepository.findByEndorsedUserAndIsActiveTrue(user, pageable);
    }
    
    /**
     * Get endorsements given by a user
     */
    public List<Endorsement> getEndorsementsGivenByUser(Long userId) {
        User endorser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return endorsementRepository.findByEndorser(endorser);
    }
    
    /**
     * Get endorsements for a specific skill
     */
    public List<Endorsement> getEndorsementsForUserSkill(Long userId, Long skillId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));
        
        return endorsementRepository.findByEndorsedUserAndSkillAndIsActiveTrue(user, skill);
    }
    
    /**
     * Get endorsements by type for a user
     */
    public List<Endorsement> getEndorsementsByType(Long userId, EndorsementType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return endorsementRepository.findByEndorsedUserAndEndorsementTypeAndIsActiveTrue(user, type);
    }
    
    /**
     * Get a specific endorsement by ID
     */
    public Optional<Endorsement> getEndorsementById(Long endorsementId) {
        return endorsementRepository.findById(endorsementId);
    }
    
    // ==========================================
    // Count Operations
    // ==========================================
    
    /**
     * Get total active endorsements for a user
     */
    public long getEndorsementCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return endorsementRepository.countByEndorsedUserAndIsActiveTrue(user);
    }
    
    /**
     * Get endorsement count for a specific skill
     */
    public long getSkillEndorsementCount(Long userId, Long skillId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));
        
        return endorsementRepository.countByEndorsedUserAndSkillAndIsActiveTrue(user, skill);
    }
    
    /**
     * Get endorsement count by type for a user
     */
    public long getEndorsementCountByType(Long userId, EndorsementType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return endorsementRepository.countByEndorsedUserAndEndorsementTypeAndIsActiveTrue(user, type);
    }
    
    /**
     * Get count of endorsements given by a user
     */
    public long getEndorsementsGivenCount(Long userId) {
        User endorser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return endorsementRepository.countByEndorser(endorser);
    }
    
    // ==========================================
    // Revoke/Update Endorsements
    // ==========================================
    
    /**
     * Revoke an endorsement
     */
    public void revokeEndorsement(Long endorsementId, Long userId) {
        Endorsement endorsement = endorsementRepository.findById(endorsementId)
                .orElseThrow(() -> new IllegalArgumentException("Endorsement not found"));
        
        // Only endorser can revoke
        if (!endorsement.getEndorser().getId().equals(userId)) {
            throw new IllegalArgumentException("Only endorser can revoke this endorsement");
        }
        
        if (!Boolean.TRUE.equals(endorsement.getIsActive())) {
            throw new IllegalArgumentException("Endorsement is already inactive");
        }
        
        endorsement.revoke();
        endorsementRepository.save(endorsement);
        
        // Update user reputation
        updateUserReputationScore(endorsement.getEndorsedUser());
        
        log.info("Endorsement {} revoked by user {}", endorsementId, userId);
    }
    
    /**
     * Update endorsement message
     */
    public Endorsement updateEndorsementMessage(Long endorsementId, String newMessage, Long userId) {
        Endorsement endorsement = endorsementRepository.findById(endorsementId)
                .orElseThrow(() -> new IllegalArgumentException("Endorsement not found"));
        
        // Only endorser can update
        if (!endorsement.getEndorser().getId().equals(userId)) {
            throw new IllegalArgumentException("Only endorser can update this endorsement");
        }
        
        if (!endorsement.isValid()) {
            throw new IllegalArgumentException("Cannot update inactive endorsement");
        }
        
        endorsement.setMessage(newMessage);
        return endorsementRepository.save(endorsement);
    }
    
    // ==========================================
    // Verification & Validation
    // ==========================================
    
    /**
     * Check if user can endorse another user
     * Prevents multiple endorsements of same skill within a month
     */
    public boolean canEndorse(Long endorserId, Long endorsedUserId, Long skillId) {
        User endorser = userRepository.findById(endorserId).orElse(null);
        User endorsedUser = userRepository.findById(endorsedUserId).orElse(null);
        Skill skill = skillRepository.findById(skillId).orElse(null);
        
        if (endorser == null || endorsedUser == null || skill == null) {
            return false;
        }
        
        if (endorserId.equals(endorsedUserId)) {
            return false;
        }
        
        // Check if active endorsement exists
        return !endorsementRepository.hasActiveEndorsement(endorser, endorsedUser, skill);
    }
    
    /**
     * Check if endorsement exists and is valid
     */
    public boolean hasValidEndorsement(Long endorserId, Long endorsedUserId, Long skillId) {
        User endorser = userRepository.findById(endorserId).orElse(null);
        User endorsedUser = userRepository.findById(endorsedUserId).orElse(null);
        Skill skill = skillRepository.findById(skillId).orElse(null);
        
        if (endorser == null || endorsedUser == null || skill == null) {
            return false;
        }
        
        Optional<Endorsement> endorsement = endorsementRepository
                .findByEndorserAndEndorsedUserAndSkillAndIsActiveTrue(endorser, endorsedUser, skill);
        
        return endorsement.isPresent() && endorsement.get().isValid();
    }
    
    // ==========================================
    // Reputation Updates
    // ==========================================
    
    /**
     * Update user's reputation score based on endorsements
     * Considers endorsement type and count
     */
    @Transactional
    public void updateUserReputationScore(User user) {
        long peerEndorsements = endorsementRepository.countByEndorsedUserAndEndorsementTypeAndIsActiveTrue(user, EndorsementType.PEER);
        long clientEndorsements = endorsementRepository.countByEndorsedUserAndEndorsementTypeAndIsActiveTrue(user, EndorsementType.CLIENT);
        long verifiedBadges = endorsementRepository.countByEndorsedUserAndEndorsementTypeAndIsActiveTrue(user, EndorsementType.VERIFIED);
        
        // Calculate reputation boost (this is a simple model, can be enhanced)
        // Peer endorsements: +0.1 each (max +1.0)
        // Client endorsements: +0.2 each (max +2.0)
        // Verified badges: +0.5 each (max +1.5)
        
        double reputationBoost = Math.min(peerEndorsements * 0.1, 1.0) +
                                 Math.min(clientEndorsements * 0.2, 2.0) +
                                 Math.min(verifiedBadges * 0.5, 1.5);
        
        // Cap total reputation at 5.0
        Double newReputation = Math.min(user.getReputationScore() + reputationBoost, 5.0);
        
        user.setReputationScore(newReputation);
        user.updateTier(); // Update tier based on new reputation
        
        userRepository.save(user);
        log.info("Updated reputation for user {} to {}", user.getId(), newReputation);
    }
    
    // ==========================================
    // Expiration Management
    // ==========================================
    
    /**
     * Process expired endorsements
     * Called periodically to deactivate expired endorsements
     */
    @Transactional
    public void processExpiredEndorsements() {
        LocalDateTime now = LocalDateTime.now();
        List<Endorsement> expiredEndorsements = endorsementRepository.findExpiredEndorsements(now);
        
        for (Endorsement endorsement : expiredEndorsements) {
            endorsement.revoke();
            endorsementRepository.save(endorsement);
            updateUserReputationScore(endorsement.getEndorsedUser());
        }
        
        log.info("Processed {} expired endorsements", expiredEndorsements.size());
    }
    
    /**
     * Get endorsements expiring soon (within 7 days)
     */
    public List<Endorsement> getExpiringEndorsements(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(days);
        
        return endorsementRepository.findExpiringEndorsements(now, future);
    }
    
    /**
     * Calculate expiration date based on endorsement type
     * Peer: 1 year
     * Client: Never expires
     * Verified: 2 years
     */
    private LocalDateTime calculateExpirationDate(EndorsementType type) {
        switch (type) {
            case PEER:
                return LocalDateTime.now().plusYears(1);
            case CLIENT:
                return null; // Never expires
            case VERIFIED:
                return LocalDateTime.now().plusYears(2);
            default:
                return LocalDateTime.now().plusYears(1);
        }
    }
    
    // ==========================================
    // Analytics
    // ==========================================
    
    /**
     * Get top endorsed users for a skill
     */
    public List<User> getTopEndorsedUsersForSkill(Long skillId, Pageable pageable) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));
        
        return endorsementRepository.findTopEndorsedUsersForSkill(skill, pageable);
    }
    
    /**
     * Get endorsement breakdown for a user
     * Returns map of endorsement type to count
     */
    public java.util.Map<EndorsementType, Long> getEndorsementBreakdown(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        java.util.Map<EndorsementType, Long> breakdown = new java.util.HashMap<>();
        
        for (EndorsementType type : EndorsementType.values()) {
            long count = endorsementRepository.countByEndorsedUserAndEndorsementTypeAndIsActiveTrue(user, type);
            breakdown.put(type, count);
        }
        
        return breakdown;
    }
    
    /**
     * Get most endorsed skills for a user
     */
    public List<Skill> getMostEndorsedSkills(Long userId) {
        List<Endorsement> endorsements = getEndorsementsForUser(userId);
        
        return endorsements.stream()
                .collect(Collectors.groupingBy(
                    Endorsement::getSkill,
                    Collectors.counting()
                ))
                .entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(java.util.Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
