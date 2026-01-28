package com.example.egov.domain.sales;

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
@Table(name = "PRODUCTS")
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter
@Setter
@NoArgsConstructor
public class Product extends BaseEntity {

    @Id
    @Column(name = "PRODUCT_ID", length = 20)
    private String productId;

    @Column(name = "PRODUCT_CODE", length = 50, nullable = false)
    private String productCode;

    @Column(name = "PRODUCT_NAME", length = 200, nullable = false)
    private String productName;

    @Column(name = "PRODUCT_CATEGORY", length = 50)
    private String productCategory;

    @Column(name = "PRODUCT_TYPE", length = 20)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String productType = "PRODUCT";

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @Column(name = "UNIT_PRICE", precision = 18, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "COST_PRICE", precision = 18, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "CURRENCY", length = 3)
    private String currency = "KRW";

    @Column(name = "TAX_RATE", precision = 5, scale = 2)
    private BigDecimal taxRate = new BigDecimal("10.00");

    @Column(name = "STOCK_QUANTITY")
    private Integer stockQuantity = 0;

    @Column(name = "IS_ACTIVE", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String isActive = "Y";

    @Column(name = "USE_AT", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String useAt = "Y";

    @Column(name = "CREATED_BY", length = 20)
    private String createdBy;
}
