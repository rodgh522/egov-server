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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CUSTOMERS")
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter
@Setter
@NoArgsConstructor
public class Customer extends BaseEntity {

    @Id
    @Column(name = "CUSTOMER_ID", length = 20)
    private String customerId;

    @Column(name = "CUSTOMER_NAME", length = 200, nullable = false)
    private String customerName;

    @Column(name = "CUSTOMER_CODE", length = 50, nullable = false)
    private String customerCode;

    @Column(name = "CUSTOMER_TYPE", length = 20)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String customerType = "COMPANY";

    @Column(name = "INDUSTRY", length = 50)
    private String industry;

    @Column(name = "COMPANY_SIZE", length = 20)
    private String companySize;

    @Column(name = "WEBSITE", length = 255)
    private String website;

    @Column(name = "PHONE", length = 30)
    private String phone;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "ADDRESS", length = 500)
    private String address;

    @Column(name = "ANNUAL_REVENUE", precision = 18, scale = 2)
    private BigDecimal annualRevenue;

    @Column(name = "EMPLOYEE_COUNT")
    private Integer employeeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNED_USER_ID")
    private User assignedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BRANCH_ID")
    private Branch branch;

    @Column(name = "USE_AT", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String useAt = "Y";

    @Column(name = "CREATED_BY", length = 20)
    private String createdBy;

    @Column(name = "UPDATED_BY", length = 20)
    private String updatedBy;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contact> contacts = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Opportunity> opportunities = new ArrayList<>();
}
