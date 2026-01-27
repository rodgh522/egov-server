package com.example.egov.web.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PositionCreateRequest {

    @NotBlank(message = "Position ID is required")
    @Size(max = 20, message = "Position ID must not exceed 20 characters")
    private String positionId;

    @NotBlank(message = "Position name is required")
    @Size(max = 100, message = "Position name must not exceed 100 characters")
    private String positionName;

    @NotBlank(message = "Position code is required")
    @Size(max = 20, message = "Position code must not exceed 20 characters")
    private String positionCode;

    private Integer positionLevel;

    @Size(max = 255, message = "Position description must not exceed 255 characters")
    private String positionDescription;

    @Size(max = 20, message = "Tenant ID must not exceed 20 characters")
    private String tenantId;

    @Pattern(regexp = "^[YN]$", message = "useAt must be Y or N")
    private String useAt = "Y";
}
