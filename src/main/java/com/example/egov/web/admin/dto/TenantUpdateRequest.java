package com.example.egov.web.admin.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TenantUpdateRequest {

    @Size(max = 100, message = "Tenant name must not exceed 100 characters")
    private String tenantName;

    @Size(max = 255, message = "Tenant description must not exceed 255 characters")
    private String tenantDescription;

    @Pattern(regexp = "^[YN]$", message = "useAt must be Y or N")
    private String useAt;
}
