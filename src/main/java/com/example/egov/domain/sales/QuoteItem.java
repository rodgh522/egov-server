package com.example.egov.domain.sales;

import com.example.egov.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;

@Entity
@Table(name = "QUOTE_ITEMS")
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter
@Setter
@NoArgsConstructor
public class QuoteItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUOTE_ITEM_ID")
    private Long quoteItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUOTE_ID", nullable = false)
    private Quote quote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @Column(name = "ITEM_ORDER")
    private Integer itemOrder = 0;

    @Column(name = "QUANTITY")
    private Integer quantity = 1;

    @Column(name = "UNIT_PRICE", precision = 18, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "DISCOUNT_RATE", precision = 5, scale = 2)
    private BigDecimal discountRate = BigDecimal.ZERO;

    @Column(name = "DISCOUNT_AMOUNT", precision = 18, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "TAX_RATE", precision = 5, scale = 2)
    private BigDecimal taxRate = new BigDecimal("10.00");

    @Column(name = "TAX_AMOUNT", precision = 18, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "TOTAL_AMOUNT", precision = 18, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;
}
