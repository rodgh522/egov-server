package com.example.egov.web.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupCreateRequest {

    @NotBlank(message = "Group ID is required")
    @Size(max = 20, message = "Group ID must not exceed 20 characters")
    private String groupId;

    @NotBlank(message = "Group name is required")
    @Size(max = 100, message = "Group name must not exceed 100 characters")
    private String groupName;

    @NotBlank(message = "Group code is required")
    @Size(max = 20, message = "Group code must not exceed 20 characters")
    private String groupCode;

    @Size(max = 255, message = "Group description must not exceed 255 characters")
    private String groupDescription;

    @NotBlank(message = "Branch ID is required")
    @Size(max = 20, message = "Branch ID must not exceed 20 characters")
    private String branchId;

    @Size(max = 20, message = "Tenant ID must not exceed 20 characters")
    private String tenantId;

    @Pattern(regexp = "^[YN]$", message = "useAt must be Y or N")
    private String useAt = "Y";
}
