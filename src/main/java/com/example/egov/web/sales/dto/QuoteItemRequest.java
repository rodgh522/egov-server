package com.example.egov.web.sales.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class QuoteItemRequest {

    @NotBlank(message = "Product ID is required")
    private String productId;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal discountRate;

    private BigDecimal taxRate;

    private String description;
}
