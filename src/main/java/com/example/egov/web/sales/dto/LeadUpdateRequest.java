package com.example.egov.web.sales.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LeadUpdateRequest {

    @Size(max = 200, message = "Lead name must not exceed 200 characters")
    private String leadName;

    @Size(max = 200, message = "Company name must not exceed 200 characters")
    private String companyName;

    @Size(max = 100, message = "Contact name must not exceed 100 characters")
    private String contactName;

    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 30, message = "Phone must not exceed 30 characters")
    private String phone;

    @Size(max = 50, message = "Lead source must not exceed 50 characters")
    private String leadSource;

    @Size(max = 20, message = "Lead status must not exceed 20 characters")
    private String leadStatus;

    private Integer leadScore;

    @Size(max = 50, message = "Industry must not exceed 50 characters")
    private String industry;

    private BigDecimal estimatedRevenue;

    private String description;

    private String assignedUserId;

    @Pattern(regexp = "^[YN]$", message = "useAt must be Y or N")
    private String useAt;
}
