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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "OPPORTUNITIES")
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter
@Setter
@NoArgsConstructor
public class Opportunity extends BaseEntity {

    @Id
    @Column(name = "OPPORTUNITY_ID", length = 20)
    private String opportunityId;

    @Column(name = "OPPORTUNITY_NAME", length = 200, nullable = false)
    private String opportunityName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTACT_ID")
    private Contact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STAGE_ID", nullable = false)
    private PipelineStage stage;

    @Column(name = "AMOUNT", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "PROBABILITY")
    private Integer probability = 0;

    @Column(name = "EXPECTED_CLOSE_DATE")
    private LocalDate expectedCloseDate;

    @Column(name = "ACTUAL_CLOSE_DATE")
    private LocalDate actualCloseDate;

    @Column(name = "LEAD_SOURCE", length = 50)
    private String leadSource;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @Column(name = "NEXT_STEP", length = 500)
    private String nextStep;

    @Column(name = "COMPETITOR_INFO", columnDefinition = "TEXT")
    private String competitorInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNED_USER_ID")
    private User assignedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BRANCH_ID")
    private Branch branch;

    @Column(name = "WON_REASON", length = 500)
    private String wonReason;

    @Column(name = "LOST_REASON", length = 500)
    private String lostReason;

    @Column(name = "USE_AT", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String useAt = "Y";

    @Column(name = "CREATED_BY", length = 20)
    private String createdBy;

    @Column(name = "UPDATED_BY", length = 20)
    private String updatedBy;

    @OneToMany(mappedBy = "opportunity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpportunityProduct> opportunityProducts = new ArrayList<>();

    @OneToMany(mappedBy = "opportunity", cascade = CascadeType.ALL)
    private List<Quote> quotes = new ArrayList<>();
}
