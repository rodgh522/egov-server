package com.example.egov.domain.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "ROLE_PERMISSIONS")
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_PERMISSION_ID")
    private Long rolePermissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERMISSION_ID", nullable = false)
    private Permission permission;

    @Column(name = "GRANTED_AT")
    @CreatedDate
    private LocalDateTime grantedAt;

    @Column(name = "GRANTED_BY", length = 20)
    private String grantedBy;

    @Column(name = "TENANT_ID", length = 20, nullable = false)
    private String tenantId;
}
