package com.example.egov.domain.admin;

import com.example.egov.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * User entity - represents a user in the system.
 *
 * Users belong to a tenant, branch, group, and position.
 * They can have multiple roles via USER_ROLES junction table.
 * Tenant filtering is applied via Hibernate filter.
 */
@Entity
@Table(name = "USERS")

@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @Column(name = "ESNTL_ID", length = 20)
    private String id;

    @Column(name = "USER_ID", length = 20, unique = true, nullable = false)
    private String userId;

    @Column(name = "PASSWORD", length = 255, nullable = false)
    private String password;

    @Column(name = "USER_NAME", length = 100, nullable = false)
    private String userName;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "PHONE", length = 20)
    private String phone;

    @Column(name = "GROUP_ID", length = 20)
    private String groupId;

    @Column(name = "BRANCH_ID", length = 20)
    private String branchId;

    @Column(name = "POSITION_ID", length = 20)
    private String positionId;

    @Column(name = "MANAGER_ID", length = 20)
    private String managerId;

    @Column(name = "STATUS_CODE", length = 15)
    private String statusCode;

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
