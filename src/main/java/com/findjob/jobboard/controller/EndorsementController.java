package com.findjob.jobboard.controller;

import com.findjob.jobboard.dto.EndorsementDTO;
import com.findjob.jobboard.model.Endorsement;
import com.findjob.jobboard.model.EndorsementType;
import com.findjob.jobboard.service.EndorsementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * EndorsementController - Handles endorsement-related endpoints
 * Supports creating, retrieving, managing, and revoking endorsements
 */
@RestController
@RequestMapping("/api/endorsements")
@RequiredArgsConstructor
@Slf4j
public class EndorsementController {
    
    private final EndorsementService endorsementService;
    
    // ==========================================
    // Create Endorsement
    // ==========================================
    
    /**
     * POST /api/endorsements
     * Create a new endorsement
     */
    @PostMapping
    public ResponseEntity<?> createEndorsement(
            @RequestParam Long endorserId,
            @RequestParam Long endorsedUserId,
            @RequestParam Long skillId,
            @RequestParam EndorsementType type,
            @RequestParam(required = false) String message) {
        try {
            Endorsement endorsement = endorsementService.createEndorsement(
                    endorserId, endorsedUserId, skillId, type, message);
            
            EndorsementDTO dto = convertToDTO(endorsement);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error creating endorsement", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating endorsement");
        }
    }
    
    /**
     * POST /api/endorsements/peer
     * Create peer endorsement
     */
    @PostMapping("/peer")
    public ResponseEntity<?> createPeerEndorsement(
            @RequestParam Long endorserId,
            @RequestParam Long endorsedUserId,
            @RequestParam Long skillId,
            @RequestParam(required = false) String message) {
        try {
            Endorsement endorsement = endorsementService.createPeerEndorsement(
                    endorserId, endorsedUserId, skillId, message);
            
            EndorsementDTO dto = convertToDTO(endorsement);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * POST /api/endorsements/client
     * Create client endorsement
     */
    @PostMapping("/client")
    public ResponseEntity<?> createClientEndorsement(
            @RequestParam Long clientId,
            @RequestParam Long freelancerId,
            @RequestParam Long skillId,
            @RequestParam(required = false) String message) {
        try {
            Endorsement endorsement = endorsementService.createClientEndorsement(
                    clientId, freelancerId, skillId, message);
            
            EndorsementDTO dto = convertToDTO(endorsement);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    // ==========================================
    // Retrieve Endorsements
    // ==========================================
    
    /**
     * GET /api/endorsements/user/{userId}
     * Get all endorsements for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserEndorsements(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Endorsement> endorsements = endorsementService.getEndorsementsForUser(userId, pageable);
            
            List<EndorsementDTO> dtos = endorsements.getContent().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/endorsements/given/{userId}
     * Get endorsements given by a user
     */
    @GetMapping("/given/{userId}")
    public ResponseEntity<?> getEndorsementsGiven(
            @PathVariable Long userId) {
        try {
            List<Endorsement> endorsements = endorsementService.getEndorsementsGivenByUser(userId);
            
            List<EndorsementDTO> dtos = endorsements.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/endorsements/{id}
     * Get endorsement by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getEndorsement(@PathVariable Long id) {
        return endorsementService.getEndorsementById(id)
                .map(endorsement -> ResponseEntity.ok(convertToDTO(endorsement)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * GET /api/endorsements/user/{userId}/skill/{skillId}
     * Get endorsements for a specific skill
     */
    @GetMapping("/user/{userId}/skill/{skillId}")
    public ResponseEntity<?> getUserSkillEndorsements(
            @PathVariable Long userId,
            @PathVariable Long skillId) {
        try {
            List<Endorsement> endorsements = endorsementService.getEndorsementsForUserSkill(userId, skillId);
            
            List<EndorsementDTO> dtos = endorsements.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/endorsements/user/{userId}/type/{type}
     * Get endorsements by type
     */
    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<?> getEndorsementsByType(
            @PathVariable Long userId,
            @PathVariable EndorsementType type) {
        try {
            List<Endorsement> endorsements = endorsementService.getEndorsementsByType(userId, type);
            
            List<EndorsementDTO> dtos = endorsements.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    // ==========================================
    // Count Operations
    // ==========================================
    
    /**
     * GET /api/endorsements/user/{userId}/count
     * Get total endorsement count for user
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<?> getEndorsementCount(@PathVariable Long userId) {
        try {
            long count = endorsementService.getEndorsementCount(userId);
            return ResponseEntity.ok(java.util.Map.of("count", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/endorsements/user/{userId}/skill/{skillId}/count
     * Get endorsement count for specific skill
     */
    @GetMapping("/user/{userId}/skill/{skillId}/count")
    public ResponseEntity<?> getSkillEndorsementCount(
            @PathVariable Long userId,
            @PathVariable Long skillId) {
        try {
            long count = endorsementService.getSkillEndorsementCount(userId, skillId);
            return ResponseEntity.ok(java.util.Map.of("count", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/endorsements/user/{userId}/count-breakdown
     * Get breakdown of endorsements by type
     */
    @GetMapping("/user/{userId}/count-breakdown")
    public ResponseEntity<?> getEndorsementBreakdown(@PathVariable Long userId) {
        try {
            java.util.Map<EndorsementType, Long> breakdown = endorsementService.getEndorsementBreakdown(userId);
            return ResponseEntity.ok(breakdown);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    // ==========================================
    // Revoke Endorsement
    // ==========================================
    
    /**
     * DELETE /api/endorsements/{id}
     * Revoke endorsement
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> revokeEndorsement(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            endorsementService.revokeEndorsement(id, userId);
            return ResponseEntity.ok(java.util.Map.of("message", "Endorsement revoked successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * PATCH /api/endorsements/{id}/message
     * Update endorsement message
     */
    @PatchMapping("/{id}/message")
    public ResponseEntity<?> updateEndorsementMessage(
            @PathVariable Long id,
            @RequestParam String message,
            @RequestParam Long userId) {
        try {
            Endorsement endorsement = endorsementService.updateEndorsementMessage(id, message, userId);
            return ResponseEntity.ok(convertToDTO(endorsement));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    // ==========================================
    // Verification
    // ==========================================
    
    /**
     * GET /api/endorsements/can-endorse
     * Check if user can endorse another user
     */
    @GetMapping("/can-endorse")
    public ResponseEntity<?> canEndorse(
            @RequestParam Long endorserId,
            @RequestParam Long endorsedUserId,
            @RequestParam Long skillId) {
        boolean canEndorse = endorsementService.canEndorse(endorserId, endorsedUserId, skillId);
        return ResponseEntity.ok(java.util.Map.of("canEndorse", canEndorse));
    }
    
    /**
     * GET /api/endorsements/has-valid
     * Check if valid endorsement exists
     */
    @GetMapping("/has-valid")
    public ResponseEntity<?> hasValidEndorsement(
            @RequestParam Long endorserId,
            @RequestParam Long endorsedUserId,
            @RequestParam Long skillId) {
        boolean hasValid = endorsementService.hasValidEndorsement(endorserId, endorsedUserId, skillId);
        return ResponseEntity.ok(java.util.Map.of("hasValid", hasValid));
    }
    
    // ==========================================
    // Analytics
    // ==========================================
    
    /**
     * GET /api/endorsements/user/{userId}/most-endorsed-skills
     * Get most endorsed skills for user
     */
    @GetMapping("/user/{userId}/most-endorsed-skills")
    public ResponseEntity<?> getMostEndorsedSkills(@PathVariable Long userId) {
        try {
            var skills = endorsementService.getMostEndorsedSkills(userId);
            return ResponseEntity.ok(skills);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    // ==========================================
    // Helper Methods
    // ==========================================
    
    /**
     * Convert Endorsement entity to DTO
     */
    private EndorsementDTO convertToDTO(Endorsement endorsement) {
        return EndorsementDTO.builder()
                .id(endorsement.getId())
                .endorserId(endorsement.getEndorser().getId())
                .endorserName(endorsement.getEndorser().getFullName())
                .endorserProfile(endorsement.getEndorser().getProfilePictureUrl())
                .endorsedUserId(endorsement.getEndorsedUser().getId())
                .endorsedUserName(endorsement.getEndorsedUser().getFullName())
                .skillId(endorsement.getSkill().getId())
                .skillName(endorsement.getSkill().getName())
                .endorsementType(endorsement.getEndorsementType())
                .message(endorsement.getMessage())
                .isActive(endorsement.getIsActive())
                .isValid(endorsement.isValid())
                .createdAt(endorsement.getCreatedAt())
                .expiresAt(endorsement.getExpiresAt())
                .revokedAt(endorsement.getRevokedAt())
                .build();
    }
}
