package com.example.egov.web.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchCreateRequest {

    @NotBlank(message = "Branch name is required")
    @Size(max = 100, message = "Branch name must not exceed 100 characters")
    private String branchName;

    @NotBlank(message = "Branch code is required")
    @Size(max = 20, message = "Branch code must not exceed 20 characters")
    private String branchCode;

    @Size(max = 255, message = "Branch address must not exceed 255 characters")
    private String branchAddress;

    @Size(max = 20, message = "Branch phone must not exceed 20 characters")
    private String branchPhone;

    private String parentBranchId;

    @Size(max = 20, message = "Tenant ID must not exceed 20 characters")
    private String tenantId;

    @Pattern(regexp = "^[YN]$", message = "useAt must be Y or N")
    private String useAt = "Y";
}
