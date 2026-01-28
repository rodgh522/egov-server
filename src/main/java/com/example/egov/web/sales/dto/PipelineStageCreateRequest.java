package com.example.egov.web.sales.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PipelineStageCreateRequest {

    @NotBlank(message = "Stage name is required")
    @Size(max = 100, message = "Stage name must not exceed 100 characters")
    private String stageName;

    @NotBlank(message = "Stage code is required")
    @Size(max = 50, message = "Stage code must not exceed 50 characters")
    private String stageCode;

    private Integer stageOrder;

    private Integer probability;

    @Size(max = 7, message = "Stage color must not exceed 7 characters")
    private String stageColor;

    @Pattern(regexp = "^[YN]$", message = "isWon must be Y or N")
    private String isWon = "N";

    @Pattern(regexp = "^[YN]$", message = "isLost must be Y or N")
    private String isLost = "N";

    @Pattern(regexp = "^[YN]$", message = "useAt must be Y or N")
    private String useAt = "Y";
}
