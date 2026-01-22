package com.example.egov.domain.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * CommonCode entity - represents common codes for the system.
 *
 * Common codes are used for dropdown lists and system-wide code management.
 * Tenant filtering is applied via Hibernate filter.
 */
@Entity
@Table(name = "COMMON_CODES")

@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter
@Setter
@NoArgsConstructor
public class CommonCode extends BaseEntity {

    @Id
    @Column(name = "CODE_ID", length = 6)
    private String codeId;

    @Column(name = "CLASS_CODE", length = 3)
    private String classCode;

    @Column(name = "CODE_NAME", length = 60)
    private String codeName;

    @Column(name = "CODE_DESCRIPTION", length = 200)
    private String codeDescription;

    @Column(name = "USE_AT", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String useAt;

    @PrePersist
    public void prePersist() {
        if (this.useAt == null) {
            this.useAt = "Y";
        }
    }
}
