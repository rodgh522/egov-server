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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "QUOTES")
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter
@Setter
@NoArgsConstructor
public class Quote extends BaseEntity {

    @Id
    @Column(name = "QUOTE_ID", length = 20)
    private String quoteId;

    @Column(name = "QUOTE_NUMBER", length = 50, nullable = false)
    private String quoteNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPPORTUNITY_ID")
    private Opportunity opportunity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTACT_ID")
    private Contact contact;

    @Column(name = "QUOTE_STATUS", length = 20)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String quoteStatus = "DRAFT";

    @Column(name = "QUOTE_DATE", nullable = false)
    private LocalDate quoteDate;

    @Column(name = "VALID_UNTIL")
    private LocalDate validUntil;

    @Column(name = "SUBTOTAL", precision = 18, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "DISCOUNT_AMOUNT", precision = 18, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "TAX_AMOUNT", precision = 18, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "TOTAL_AMOUNT", precision = 18, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "CURRENCY", length = 3)
    private String currency = "KRW";

    @Column(name = "PAYMENT_TERMS", length = 200)
    private String paymentTerms;

    @Column(name = "DELIVERY_TERMS", length = 200)
    private String deliveryTerms;

    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNED_USER_ID")
    private User assignedUser;

    @Column(name = "USE_AT", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String useAt = "Y";

    @Column(name = "CREATED_BY", length = 20)
    private String createdBy;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuoteItem> quoteItems = new ArrayList<>();
}
