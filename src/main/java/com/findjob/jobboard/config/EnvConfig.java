package com.findjob.jobboard.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

/**
 * EnvConfig - Automatically loads environment variables from .env file
 * 
 * This configuration class runs at startup and loads all variables from .env file
 * into the system environment. This way, Spring Boot can read them via ${VAR_NAME}
 * placeholders in application.properties
 */
@Configuration
public class EnvConfig {
    
    static {
        // Load .env file from project root
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()  // Don't fail if .env doesn't exist
                .load();
        
        // Load all variables into system environment
        dotenv.entries().forEach(entry -> 
            System.setProperty(entry.getKey(), entry.getValue())
        );
    }
}
