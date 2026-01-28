package com.example.egov.web.sales.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SalesTargetUpdateRequest {

    private Integer targetYear;

    private Integer targetMonth;

    private BigDecimal targetAmount;

    private BigDecimal achievedAmount;

    @Size(max = 20, message = "Target type must not exceed 20 characters")
    private String targetType;

    private String userId;

    private String branchId;
}
