package com.findjob.jobboard.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache Configuration for the FindJob application
 * 
 * Configures an in-memory cache manager for application-wide caching support.
 * This can be extended to use Redis or other cache backends in production.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Defines an in-memory cache manager using ConcurrentMapCache.
     * This is suitable for development and single-instance deployments.
     * 
     * For production with multiple instances, consider using Redis:
     * @Bean
     * public CacheManager cacheManager() {
     *     return RedisCacheManager.create(connectionFactory);
     * }
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "users",
            "jobs",
            "skills",
            "endorsements",
            "reviews",
            "profiles",
            "applications",
            "messages"
        );
    }
}
