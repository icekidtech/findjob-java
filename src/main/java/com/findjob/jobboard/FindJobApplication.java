package com.findjob.jobboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * FindJobApplication - Main entry point for the FindJob Spring Boot application
 * 
 * A comprehensive job board platform connecting freelancers with clients.
 * Features include:
 * - User authentication and profile management
 * - Job posting and bidding system
 * - Skill endorsement system
 * - Review and rating system
 * - Direct messaging between users
 * - Payment processing
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
@EnableScheduling
public class FindJobApplication {

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationManager.class)
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Application entry point
     */
    public static void main(String[] args) {
        SpringApplication.run(FindJobApplication.class, args);
    }

}
