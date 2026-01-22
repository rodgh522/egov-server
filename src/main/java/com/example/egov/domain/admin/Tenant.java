package com.example.egov.domain.admin;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Tenant entity - represents a tenant in the multi-tenant system.
 *
 * Note: This entity does NOT extend BaseEntity and does NOT have tenant
 * filtering
 * because it IS the root tenant table.
 */
@Entity
@Table(name = "TENANTS")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Tenant {

    @Id
    @Column(name = "TENANT_ID", length = 20)
    private String tenantId;

    @Column(name = "TENANT_NAME", length = 100)
    private String tenantName;

    @Column(name = "TENANT_DESCRIPTION", length = 255)
    private String tenantDescription;

    @Column(name = "USE_AT", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String useAt;

    @CreatedDate
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        if (this.useAt == null) {
            this.useAt = "Y";
        }
        if (this.createdDate == null) {
            this.createdDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
