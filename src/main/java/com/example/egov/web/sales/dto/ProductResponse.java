package com.example.egov.web.sales.dto;

import com.example.egov.domain.sales.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private String productId;
    private String productCode;
    private String productName;
    private String productCategory;
    private String productType;
    private String description;
    private BigDecimal unitPrice;
    private BigDecimal costPrice;
    private String currency;
    private BigDecimal taxRate;
    private Integer stockQuantity;
    private String isActive;
    private String tenantId;
    private String useAt;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static ProductResponse from(Product product) {
        if (product == null) {
            return null;
        }

        return ProductResponse.builder()
                .productId(product.getProductId())
                .productCode(product.getProductCode())
                .productName(product.getProductName())
                .productCategory(product.getProductCategory())
                .productType(product.getProductType())
                .description(product.getDescription())
                .unitPrice(product.getUnitPrice())
                .costPrice(product.getCostPrice())
                .currency(product.getCurrency())
                .taxRate(product.getTaxRate())
                .stockQuantity(product.getStockQuantity())
                .isActive(product.getIsActive())
                .tenantId(product.getTenantId())
                .useAt(product.getUseAt())
                .createdDate(product.getCreatedDate())
                .updatedDate(product.getUpdatedDate())
                .build();
    }

    public static List<ProductResponse> fromList(List<Product> products) {
        if (products == null) {
            return null;
        }
        return products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }
}
