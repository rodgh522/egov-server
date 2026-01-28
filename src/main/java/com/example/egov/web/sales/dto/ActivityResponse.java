package com.example.egov.web.sales.dto;

import com.example.egov.domain.sales.Activity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResponse {

    private String activityId;
    private String activityType;
    private String activitySubject;
    private String activityDescription;
    private String activityStatus;
    private LocalDateTime activityDate;
    private LocalDateTime dueDate;
    private LocalDateTime completedDate;
    private Integer durationMinutes;
    private String priority;
    private String relatedType;
    private String relatedId;
    private String assignedUserId;
    private String assignedUserName;
    private String tenantId;
    private String useAt;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static ActivityResponse from(Activity activity) {
        if (activity == null) {
            return null;
        }

        return ActivityResponse.builder()
                .activityId(activity.getActivityId())
                .activityType(activity.getActivityType())
                .activitySubject(activity.getActivitySubject())
                .activityDescription(activity.getActivityDescription())
                .activityStatus(activity.getActivityStatus())
                .activityDate(activity.getActivityDate())
                .dueDate(activity.getDueDate())
                .completedDate(activity.getCompletedDate())
                .durationMinutes(activity.getDurationMinutes())
                .priority(activity.getPriority())
                .relatedType(activity.getRelatedType())
                .relatedId(activity.getRelatedId())
                .assignedUserId(activity.getAssignedUser() != null ? activity.getAssignedUser().getId() : null)
                .assignedUserName(activity.getAssignedUser() != null ? activity.getAssignedUser().getUserName() : null)
                .tenantId(activity.getTenantId())
                .useAt(activity.getUseAt())
                .createdDate(activity.getCreatedDate())
                .updatedDate(activity.getUpdatedDate())
                .build();
    }

    public static List<ActivityResponse> fromList(List<Activity> activities) {
        if (activities == null) {
            return null;
        }
        return activities.stream()
                .map(ActivityResponse::from)
                .collect(Collectors.toList());
    }
}
