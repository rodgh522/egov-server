package com.example.egov.web.sales.dto;

import com.example.egov.domain.sales.QuoteItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteItemResponse {

    private Long quoteItemId;
    private String productId;
    private String productCode;
    private String productName;
    private Integer itemOrder;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountRate;
    private BigDecimal discountAmount;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String description;

    public static QuoteItemResponse from(QuoteItem item) {
        if (item == null) {
            return null;
        }

        return QuoteItemResponse.builder()
                .quoteItemId(item.getQuoteItemId())
                .productId(item.getProduct() != null ? item.getProduct().getProductId() : null)
                .productCode(item.getProduct() != null ? item.getProduct().getProductCode() : null)
                .productName(item.getProduct() != null ? item.getProduct().getProductName() : null)
                .itemOrder(item.getItemOrder())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .discountRate(item.getDiscountRate())
                .discountAmount(item.getDiscountAmount())
                .taxRate(item.getTaxRate())
                .taxAmount(item.getTaxAmount())
                .totalAmount(item.getTotalAmount())
                .description(item.getDescription())
                .build();
    }

    public static List<QuoteItemResponse> fromList(List<QuoteItem> items) {
        if (items == null) {
            return null;
        }
        return items.stream()
                .map(QuoteItemResponse::from)
                .collect(Collectors.toList());
    }
}
