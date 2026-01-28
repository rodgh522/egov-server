package com.example.egov.web.sales.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ActivityUpdateRequest {

    @Size(max = 20, message = "Activity type must not exceed 20 characters")
    private String activityType;

    @Size(max = 200, message = "Activity subject must not exceed 200 characters")
    private String activitySubject;

    private String activityDescription;

    @Size(max = 20, message = "Activity status must not exceed 20 characters")
    private String activityStatus;

    private LocalDateTime activityDate;

    private LocalDateTime dueDate;

    private Integer durationMinutes;

    @Size(max = 10, message = "Priority must not exceed 10 characters")
    private String priority;

    @Size(max = 20, message = "Related type must not exceed 20 characters")
    private String relatedType;

    @Size(max = 20, message = "Related ID must not exceed 20 characters")
    private String relatedId;

    private String assignedUserId;

    @Pattern(regexp = "^[YN]$", message = "useAt must be Y or N")
    private String useAt;
}
