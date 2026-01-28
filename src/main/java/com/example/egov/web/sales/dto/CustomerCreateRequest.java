package com.example.egov.web.sales.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CustomerCreateRequest {

    @NotBlank(message = "Customer name is required")
    @Size(max = 200, message = "Customer name must not exceed 200 characters")
    private String customerName;

    @NotBlank(message = "Customer code is required")
    @Size(max = 50, message = "Customer code must not exceed 50 characters")
    private String customerCode;

    @Size(max = 20, message = "Customer type must not exceed 20 characters")
    private String customerType;

    @Size(max = 50, message = "Industry must not exceed 50 characters")
    private String industry;

    @Size(max = 20, message = "Company size must not exceed 20 characters")
    private String companySize;

    @Size(max = 255, message = "Website must not exceed 255 characters")
    private String website;

    @Size(max = 30, message = "Phone must not exceed 30 characters")
    private String phone;

    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    private BigDecimal annualRevenue;

    private Integer employeeCount;

    private String assignedUserId;

    private String branchId;

    @Pattern(regexp = "^[YN]$", message = "useAt must be Y or N")
    private String useAt = "Y";
}
