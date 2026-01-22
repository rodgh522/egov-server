package com.example.egov.domain.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base entity class for all domain entities requiring auditing and multi-tenancy support.
 *
 * All entities extending this class will automatically have:
 * - TENANT_ID for multi-tenant isolation
 * - CREATED_DATE for record creation timestamp
 * - UPDATED_DATE for record modification timestamp
 *
 * JPA Auditing is enabled via @EntityListeners(AuditingEntityListener.class)
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

    /**
     * Tenant ID for multi-tenant isolation.
     * This field is required for all tenant-scoped data.
     */
    @Column(name = "TENANT_ID", length = 20, nullable = false)
    private String tenantId;

    /**
     * Timestamp when the entity was created.
     * Automatically populated by JPA Auditing.
     */
    @CreatedDate
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /**
     * Timestamp when the entity was last updated.
     * Automatically updated by JPA Auditing.
     */
    @LastModifiedDate
    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;

    /**
     * Pre-persist hook to set default values before entity is persisted.
     */
    @PrePersist
    protected void onCreate() {
        if (this.createdDate == null) {
            this.createdDate = LocalDateTime.now();
        }
    }

    /**
     * Pre-update hook to set updated timestamp before entity is updated.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
