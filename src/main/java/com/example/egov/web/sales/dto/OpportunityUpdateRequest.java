package com.example.egov.web.sales.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class OpportunityUpdateRequest {

    @Size(max = 200, message = "Opportunity name must not exceed 200 characters")
    private String opportunityName;

    private String contactId;

    private String stageId;

    private BigDecimal amount;

    private LocalDate expectedCloseDate;

    @Size(max = 50, message = "Lead source must not exceed 50 characters")
    private String leadSource;

    private String description;

    @Size(max = 500, message = "Next step must not exceed 500 characters")
    private String nextStep;

    private String competitorInfo;

    @Size(max = 500, message = "Won reason must not exceed 500 characters")
    private String wonReason;

    @Size(max = 500, message = "Lost reason must not exceed 500 characters")
    private String lostReason;

    private String assignedUserId;

    private String branchId;

    @Pattern(regexp = "^[YN]$", message = "useAt must be Y or N")
    private String useAt;
}
