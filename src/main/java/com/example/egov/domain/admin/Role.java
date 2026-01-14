package com.example.egov.domain.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "ROLES")
@Getter @Setter
@NoArgsConstructor
public class Role {
    @Id
    @Column(name = "ROLE_ID", length = 20)
    private String roleId;

    @Column(name = "ROLE_NAME", length = 60)
    private String roleNm;

    @Column(name = "ROLE_PATTERN", length = 300)
    private String rolePttrn;

    @Column(name = "ROLE_DESCRIPTION", length = 200)
    private String roleDc;

    @Column(name = "ROLE_TYPE", length = 80)
    private String roleTy;

    @Column(name = "ROLE_SORT_ORDER", length = 10)
    private String roleSort;

    @Column(name = "ROLE_CREATED_DATE")
    private LocalDateTime roleCreatDe;

    @Column(name = "TENANT_ID", length = 20)
    private String tenantId;

    @PrePersist
    public void prePersist() {
        this.roleCreatDe = LocalDateTime.now();
    }
}
