package com.example.egov.web.admin.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * User creation request DTO
 */
@Getter
@Setter
public class UserCreateRequest {

    @NotBlank(message = "User ID is required")
    @Size(min = 4, max = 20, message = "User ID must be 4-20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "User ID must contain only alphanumeric and underscore")
    private String userId;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be 8-100 characters")
    private String password;

    @NotBlank(message = "User name is required")
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

    private List<String> roleIds;
}
