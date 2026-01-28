package com.example.egov.domain.sales;

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
import java.time.LocalDateTime;

@Entity
@Table(name = "LEADS")
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter
@Setter
@NoArgsConstructor
public class Lead extends BaseEntity {

    @Id
    @Column(name = "LEAD_ID", length = 20)
    private String leadId;

    @Column(name = "LEAD_NAME", length = 200, nullable = false)
    private String leadName;

    @Column(name = "COMPANY_NAME", length = 200)
    private String companyName;

    @Column(name = "CONTACT_NAME", length = 100)
    private String contactName;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "PHONE", length = 30)
    private String phone;

    @Column(name = "LEAD_SOURCE", length = 50)
    private String leadSource;

    @Column(name = "LEAD_STATUS", length = 20)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String leadStatus = "NEW";

    @Column(name = "LEAD_SCORE")
    private Integer leadScore = 0;

    @Column(name = "INDUSTRY", length = 50)
    private String industry;

    @Column(name = "ESTIMATED_REVENUE", precision = 18, scale = 2)
    private BigDecimal estimatedRevenue;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNED_USER_ID")
    private User assignedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONVERTED_CUSTOMER_ID")
    private Customer convertedCustomer;

    @Column(name = "CONVERTED_DATE")
    private LocalDateTime convertedDate;

    @Column(name = "USE_AT", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String useAt = "Y";

    @Column(name = "CREATED_BY", length = 20)
    private String createdBy;
}
