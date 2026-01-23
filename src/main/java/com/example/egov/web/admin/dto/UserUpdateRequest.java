package com.example.egov.web.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * User update request DTO
 */
@Getter
@Setter
public class UserUpdateRequest {

    @Size(max = 100, message = "User name must not exceed 100 characters")
    private String userName;

    @Email(message = "Invalid email format")
    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String phone;

    private String groupId;

    private String branchId;

    private String positionId;

    private String managerId;

    @Size(max = 15)
    private String statusCode;

    @Pattern(regexp = "^[YN]$", message = "useAt must be 'Y' or 'N'")
    private String useAt;

    private List<String> roleIds;
}
