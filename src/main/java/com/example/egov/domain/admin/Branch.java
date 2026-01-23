package com.example.egov.domain.admin;

import com.example.egov.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "BRANCHES")
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter
@Setter
@NoArgsConstructor
public class Branch extends BaseEntity {

    @Id
    @Column(name = "BRANCH_ID", length = 20)
    private String branchId;

    @Column(name = "BRANCH_NAME", length = 100, nullable = false)
    private String branchName;

    @Column(name = "BRANCH_CODE", length = 20, unique = true, nullable = false)
    private String branchCode;

    @Column(name = "BRANCH_ADDRESS", length = 255)
    private String branchAddress;

    @Column(name = "BRANCH_PHONE", length = 20)
    private String branchPhone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_BRANCH_ID")
    private Branch parentBranch;

    @Column(name = "USE_AT", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String useAt = "Y";
}
