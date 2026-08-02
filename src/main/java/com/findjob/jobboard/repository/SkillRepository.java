package com.findjob.jobboard.repository;

import com.findjob.jobboard.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * SkillRepository - Data access layer for Skill entity
 */
@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    
    // ==========================================
    // Basic Queries
    // ==========================================
    
    /**
     * Find skill by name
     */
    Optional<Skill> findByName(String name);
    
    /**
     * Find skill by name case-insensitive
     */
    Optional<Skill> findByNameIgnoreCase(String name);
    
    /**
     * Check if skill exists by name
     */
    boolean existsByName(String name);
    
    // ==========================================
    // Search & Filter Queries
    // ==========================================
    
    /**
     * Find skills by category
     */
    List<Skill> findByCategory(String category);
    
    /**
     * Search skills by name
     */
    @Query("SELECT s FROM Skill s WHERE s.name LIKE %:query%")
    List<Skill> searchByName(@Param("query") String query);
    
    /**
     * Find verified skills
     */
    List<Skill> findByIsVerifiedTrue();
    
    /**
     * Find verified skills by category
     */
    @Query("SELECT s FROM Skill s WHERE s.isVerified = true AND s.category = :category ORDER BY s.endorsementCount DESC")
    List<Skill> findVerifiedSkillsByCategory(@Param("category") String category);
    
    /**
     * Find popular skills by endorsement count
     */
    @Query("SELECT s FROM Skill s ORDER BY s.endorsementCount DESC")
    List<Skill> findPopularSkills();
    
    /**
     * Find top N popular skills
     */
    @Query("SELECT s FROM Skill s ORDER BY s.endorsementCount DESC LIMIT :limit")
    List<Skill> findTopPopularSkills(@Param("limit") int limit);
    
    // ==========================================
    // Count & Statistics
    // ==========================================
    
    /**
     * Count total skills
     */
    long count();
    
    /**
     * Count verified skills
     */
    @Query("SELECT COUNT(s) FROM Skill s WHERE s.isVerified = true")
    long countVerifiedSkills();
    
    /**
     * Count skills in category
     */
    long countByCategory(String category);
}
