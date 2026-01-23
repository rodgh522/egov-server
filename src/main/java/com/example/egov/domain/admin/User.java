package com.example.egov.domain.admin;

import com.example.egov.domain.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BRANCH_ID")
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_ID")
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MANAGER_ID")
    @JsonBackReference
    private User manager;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserRole> userRoles = new ArrayList<>();

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
