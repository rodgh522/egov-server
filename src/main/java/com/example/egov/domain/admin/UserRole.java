package com.example.egov.domain.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "USER_ROLES")
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ROLE_ID")
    private Long userRoleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ESNTL_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;

    @Column(name = "ASSIGNED_AT")
    @CreatedDate
    private LocalDateTime assignedAt;

    @Column(name = "ASSIGNED_BY", length = 20)
    private String assignedBy;

    @Column(name = "IS_PRIMARY", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String isPrimary = "N";

    @Column(name = "TENANT_ID", length = 20, nullable = false)
    private String tenantId;
}
