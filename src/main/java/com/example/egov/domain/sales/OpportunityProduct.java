package com.example.egov.domain.sales;

import com.example.egov.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;

@Entity
@Table(name = "OPPORTUNITY_PRODUCTS")
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter
@Setter
@NoArgsConstructor
public class OpportunityProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OPP_PRODUCT_ID")
    private Long oppProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPPORTUNITY_ID", nullable = false)
    private Opportunity opportunity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @Column(name = "QUANTITY")
    private Integer quantity = 1;

    @Column(name = "UNIT_PRICE", precision = 18, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "DISCOUNT_RATE", precision = 5, scale = 2)
    private BigDecimal discountRate = BigDecimal.ZERO;

    @Column(name = "DISCOUNT_AMOUNT", precision = 18, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "TOTAL_AMOUNT", precision = 18, scale = 2, nullable = false)
    private BigDecimal totalAmount;
}
