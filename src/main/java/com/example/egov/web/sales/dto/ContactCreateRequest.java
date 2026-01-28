package com.example.egov.web.sales.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactCreateRequest {

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "Contact name is required")
    @Size(max = 100, message = "Contact name must not exceed 100 characters")
    private String contactName;

    @Size(max = 100, message = "Contact title must not exceed 100 characters")
    private String contactTitle;

    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String department;

    @Size(max = 30, message = "Phone must not exceed 30 characters")
    private String phone;

    @Size(max = 30, message = "Mobile must not exceed 30 characters")
    private String mobile;

    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Pattern(regexp = "^[YN]$", message = "isPrimary must be Y or N")
    private String isPrimary = "N";

    @Pattern(regexp = "^[YN]$", message = "useAt must be Y or N")
    private String useAt = "Y";
}
