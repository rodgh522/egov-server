package com.example.egov.web.admin.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupUpdateRequest {

    @Size(max = 100, message = "Group name must not exceed 100 characters")
    private String groupName;

    @Size(max = 20, message = "Group code must not exceed 20 characters")
    private String groupCode;

    @Size(max = 255, message = "Group description must not exceed 255 characters")
    private String groupDescription;

    @Size(max = 20, message = "Branch ID must not exceed 20 characters")
    private String branchId;

    @Pattern(regexp = "^[YN]$", message = "useAt must be Y or N")
    private String useAt;
}
