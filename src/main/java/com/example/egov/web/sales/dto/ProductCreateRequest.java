package com.example.egov.web.sales.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductCreateRequest {

    @NotBlank(message = "Product code is required")
    @Size(max = 50, message = "Product code must not exceed 50 characters")
    private String productCode;

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must not exceed 200 characters")
    private String productName;

    @Size(max = 50, message = "Product category must not exceed 50 characters")
    private String productCategory;

    @Size(max = 20, message = "Product type must not exceed 20 characters")
    private String productType;

    private String description;

    @NotNull(message = "Unit price is required")
    private BigDecimal unitPrice;

    private BigDecimal costPrice;

    @Size(max = 3, message = "Currency must not exceed 3 characters")
    private String currency;

    private BigDecimal taxRate;

    private Integer stockQuantity;

    @Pattern(regexp = "^[YN]$", message = "isActive must be Y or N")
    private String isActive = "Y";

    @Pattern(regexp = "^[YN]$", message = "useAt must be Y or N")
    private String useAt = "Y";
}
