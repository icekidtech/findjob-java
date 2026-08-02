package com.findjob.jobboard.service;

import com.findjob.jobboard.model.Skill;
import com.findjob.jobboard.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * SkillService - Business logic for skill management
 */
@Service
@Transactional
public class SkillService {
    
    @Autowired
    private SkillRepository skillRepository;
    
    // ==========================================
    // Skill CRUD Operations
    // ==========================================
    
    /**
     * Create new skill
     */
    public Skill createSkill(Skill skill) {
        if (skillRepository.existsByName(skill.getName())) {
            throw new IllegalArgumentException("Skill already exists: " + skill.getName());
        }
        return skillRepository.save(skill);
    }
    
    /**
     * Get skill by ID
     */
    public Skill getSkillById(Long id) {
        return skillRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Skill not found with id: " + id));
    }
    
    /**
     * Get skill by name
     */
    public Optional<Skill> findByName(String name) {
        return skillRepository.findByNameIgnoreCase(name);
    }
    
    /**
     * Get or create skill
     */
    public Skill getOrCreateSkill(String skillName) {
        Optional<Skill> existingSkill = skillRepository.findByNameIgnoreCase(skillName);
        if (existingSkill.isPresent()) {
            return existingSkill.get();
        }
        
        Skill newSkill = Skill.builder()
            .name(skillName)
            .category("Other")
            .isVerified(false)
            .endorsementCount(0)
            .build();
        
        return skillRepository.save(newSkill);
    }
    
    /**
     * Update skill
     */
    public Skill updateSkill(Skill skill) {
        if (!skillRepository.existsById(skill.getId())) {
            throw new IllegalArgumentException("Skill not found");
        }
        return skillRepository.save(skill);
    }
    
    /**
     * Delete skill
     */
    public void deleteSkill(Long id) {
        if (!skillRepository.existsById(id)) {
            throw new IllegalArgumentException("Skill not found");
        }
        skillRepository.deleteById(id);
    }
    
    /**
     * Get all skills
     */
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }
    
    // ==========================================
    // Skill Search & Filter
    // ==========================================
    
    /**
     * Get skills by category
     */
    public List<Skill> getSkillsByCategory(String category) {
        return skillRepository.findByCategory(category);
    }
    
    /**
     * Search skills by name
     */
    public List<Skill> searchSkills(String query) {
        return skillRepository.searchByName(query);
    }
    
    /**
     * Get verified skills
     */
    public List<Skill> getVerifiedSkills() {
        return skillRepository.findByIsVerifiedTrue();
    }
    
    /**
     * Get verified skills by category
     */
    public List<Skill> getVerifiedSkillsByCategory(String category) {
        return skillRepository.findVerifiedSkillsByCategory(category);
    }
    
    /**
     * Get popular skills
     */
    public List<Skill> getPopularSkills() {
        return skillRepository.findPopularSkills();
    }
    
    /**
     * Get top N popular skills
     */
    public List<Skill> getTopPopularSkills(int limit) {
        return skillRepository.findTopPopularSkills(limit);
    }
    
    // ==========================================
    // Skill Management
    // ==========================================
    
    /**
     * Verify skill (admin action)
     */
    public Skill verifySkill(Long id) {
        Skill skill = getSkillById(id);
        skill.setIsVerified(true);
        return skillRepository.save(skill);
    }
    
    /**
     * Unverify skill (admin action)
     */
    public Skill unverifySkill(Long id) {
        Skill skill = getSkillById(id);
        skill.setIsVerified(false);
        return skillRepository.save(skill);
    }
    
    /**
     * Increment endorsement count
     */
    public void incrementEndorsements(Long skillId) {
        Skill skill = getSkillById(skillId);
        skill.incrementEndorsements();
        skillRepository.save(skill);
    }
    
    /**
     * Decrement endorsement count
     */
    public void decrementEndorsements(Long skillId) {
        Skill skill = getSkillById(skillId);
        skill.decrementEndorsements();
        skillRepository.save(skill);
    }
    
    // ==========================================
    // Statistics
    // ==========================================
    
    /**
     * Get total skill count
     */
    public long getTotalSkills() {
        return skillRepository.count();
    }
    
    /**
     * Get verified skill count
     */
    public long getVerifiedSkillCount() {
        return skillRepository.countVerifiedSkills();
    }
    
    /**
     * Get skill count by category
     */
    public long getSkillCountByCategory(String category) {
        return skillRepository.countByCategory(category);
    }
}
