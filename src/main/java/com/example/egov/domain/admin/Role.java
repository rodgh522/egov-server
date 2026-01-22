package com.example.egov.domain.admin;

import com.example.egov.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

/**
 * Role entity - represents a role in the RBAC system.
 *
 * Roles are assigned to users and grant permissions.
 * Tenant filtering is applied via Hibernate filter.
 */
@Entity
@Table(name = "ROLES")

@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter
@Setter
@NoArgsConstructor
public class Role extends BaseEntity {

    @Id
    @Column(name = "ROLE_ID", length = 20)
    private String roleId;

    @Column(name = "ROLE_NAME", length = 100)
    private String roleName;

    @Column(name = "ROLE_DESCRIPTION", length = 255)
    private String roleDescription;

    @Column(name = "ROLE_TYPE", length = 80)
    private String roleType;

    @Column(name = "ROLE_SORT_ORDER", length = 10)
    private String roleSortOrder;
}
