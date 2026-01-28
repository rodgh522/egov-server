package com.example.egov.domain.sales;

import com.example.egov.domain.admin.Branch;
import com.example.egov.domain.admin.User;
import com.example.egov.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "SALES_TARGETS")
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter
@Setter
@NoArgsConstructor
public class SalesTarget extends BaseEntity {

    @Id
    @Column(name = "TARGET_ID", length = 20)
    private String targetId;

    @Column(name = "TARGET_YEAR", nullable = false)
    private Integer targetYear;

    @Column(name = "TARGET_MONTH")
    private Integer targetMonth;

    @Column(name = "TARGET_AMOUNT", precision = 18, scale = 2, nullable = false)
    private BigDecimal targetAmount;

    @Column(name = "ACHIEVED_AMOUNT", precision = 18, scale = 2)
    private BigDecimal achievedAmount = BigDecimal.ZERO;

    @Column(name = "TARGET_TYPE", length = 20)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String targetType = "REVENUE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BRANCH_ID")
    private Branch branch;
}
