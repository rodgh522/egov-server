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
@Table(name = "GROUPS")
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter
@Setter
@NoArgsConstructor
public class Group extends BaseEntity {

    @Id
    @Column(name = "GROUP_ID", length = 20)
    private String groupId;

    @Column(name = "GROUP_NAME", length = 100, nullable = false)
    private String groupName;

    @Column(name = "GROUP_CODE", length = 20, nullable = false)
    private String groupCode;

    @Column(name = "GROUP_DESCRIPTION", length = 255)
    private String groupDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BRANCH_ID", nullable = false)
    private Branch branch;

    @Column(name = "USE_AT", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String useAt = "Y";
}
