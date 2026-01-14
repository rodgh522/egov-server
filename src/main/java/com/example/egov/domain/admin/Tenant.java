package com.example.egov.domain.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "TENANTS")
@Getter @Setter
@NoArgsConstructor
public class Tenant {

    @Id
    @Column(name = "TENANT_ID", length = 20)
    private String tenantId;

    @Column(name = "TENANT_NAME", length = 100)
    private String tenantNm;

    @Column(name = "TENANT_DESCRIPTION", length = 255)
    private String tenantDc; // Description

    @Column(name = "USE_AT", length = 1)
    private String useAt;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createDe;

    @PrePersist
    public void prePersist() {
        this.createDe = LocalDateTime.now();
        if (this.useAt == null) this.useAt = "Y";
    }
}
