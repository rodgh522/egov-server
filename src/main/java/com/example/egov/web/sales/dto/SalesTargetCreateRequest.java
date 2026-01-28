package com.example.egov.web.sales.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SalesTargetCreateRequest {

    @NotNull(message = "Target year is required")
    private Integer targetYear;

    private Integer targetMonth;

    @NotNull(message = "Target amount is required")
    private BigDecimal targetAmount;

    private BigDecimal achievedAmount;

    @Size(max = 20, message = "Target type must not exceed 20 characters")
    private String targetType;

    private String userId;

    private String branchId;
}
