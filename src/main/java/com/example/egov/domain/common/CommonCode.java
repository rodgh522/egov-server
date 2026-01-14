package com.example.egov.domain.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "COMMON_CODES")
@Getter @Setter
@NoArgsConstructor
public class CommonCode {
    @Id
    @Column(name = "CODE_ID", length = 6)
    private String codeId;

    // Group Code ID usually links to another table but keeping simple here
    @Column(name = "CLASS_CODE", length = 3)
    private String classCode;

    @Column(name = "CODE_NAME", length = 60)
    private String codeIdNm;

    @Column(name = "CODE_DESCRIPTION", length = 200)
    private String codeIdDc;

    @Column(name = "USE_AT", length = 1)
    private String useAt;

    @Column(name = "TENANT_ID", length = 20)
    private String tenantId;
}
