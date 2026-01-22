package com.example.egov.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing configuration.
 *
 * Enables automatic population of audit fields in entities:
 * - @CreatedDate: Automatically sets creation timestamp
 * - @LastModifiedDate: Automatically updates modification timestamp
 * - @CreatedBy: Can be configured to set creator (future enhancement)
 * - @LastModifiedBy: Can be configured to set modifier (future enhancement)
 *
 * This works in conjunction with:
 * - @EntityListeners(AuditingEntityListener.class) on entity classes
 * - @MappedSuperclass BaseEntity with auditing fields
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // Auditing is enabled via @EnableJpaAuditing annotation
    // No additional beans required for basic timestamp auditing

    /**
     * Optional: Configure auditor provider for @CreatedBy and @LastModifiedBy
     *
     * Example:
     * @Bean
     * public AuditorAware<String> auditorProvider() {
     *     return () -> {
     *         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
     *         if (authentication == null || !authentication.isAuthenticated()) {
     *             return Optional.of("SYSTEM");
     *         }
     *         return Optional.of(authentication.getName());
     *     };
     * }
     */
}
