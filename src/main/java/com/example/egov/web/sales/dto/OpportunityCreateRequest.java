package com.example.egov.web.sales.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class OpportunityCreateRequest {

    @NotBlank(message = "Opportunity name is required")
    @Size(max = 200, message = "Opportunity name must not exceed 200 characters")
    private String opportunityName;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    private String contactId;

    @NotBlank(message = "Stage ID is required")
    private String stageId;

    private BigDecimal amount;

    private LocalDate expectedCloseDate;

    @Size(max = 50, message = "Lead source must not exceed 50 characters")
    private String leadSource;

    private String description;

    @Size(max = 500, message = "Next step must not exceed 500 characters")
    private String nextStep;

    private String competitorInfo;

    private String assignedUserId;

    private String branchId;

    @Pattern(regexp = "^[YN]$", message = "useAt must be Y or N")
    private String useAt = "Y";
}
