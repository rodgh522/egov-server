package com.example.egov.service.sales;

import com.example.egov.config.multitenancy.TenantContext;
import com.example.egov.domain.admin.User;
import com.example.egov.domain.admin.UserRepository;
import com.example.egov.domain.sales.Activity;
import com.example.egov.domain.sales.ActivityRepository;
import com.example.egov.web.sales.dto.ActivityCreateRequest;
import com.example.egov.web.sales.dto.ActivityUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityService {

    private static final String SYSTEM_TENANT_ID = "SYSTEM";

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    private boolean isSystemTenant() {
        return SYSTEM_TENANT_ID.equals(TenantContext.getCurrentTenantId());
    }

    private void verifyActivityAccess(Activity activity) {
        if (!isSystemTenant() && !activity.getTenantId().equals(TenantContext.getCurrentTenantId())) {
            throw new AccessDeniedException("Access denied to activity: " + activity.getActivityId());
        }
    }

    @Transactional
    public Activity createActivity(ActivityCreateRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();

        String activityId = generateActivityId();
        log.info("Creating activity: {} for tenant: {}", activityId, tenantId);

        Activity activity = new Activity();
        activity.setActivityId(activityId);
        activity.setActivityType(request.getActivityType());
        activity.setActivitySubject(request.getActivitySubject());
        activity.setActivityDescription(request.getActivityDescription());
        activity.setActivityStatus(request.getActivityStatus() != null ? request.getActivityStatus() : "PLANNED");
        activity.setActivityDate(request.getActivityDate());
        activity.setDueDate(request.getDueDate());
        activity.setDurationMinutes(request.getDurationMinutes());
        activity.setPriority(request.getPriority() != null ? request.getPriority() : "NORMAL");
        activity.setRelatedType(request.getRelatedType());
        activity.setRelatedId(request.getRelatedId());
        activity.setUseAt(request.getUseAt() != null ? request.getUseAt() : "Y");
        activity.setTenantId(tenantId);

        if (request.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Assigned user not found: " + request.getAssignedUserId()));
            activity.setAssignedUser(assignedUser);
        }

        Activity savedActivity = activityRepository.save(activity);
        log.info("Activity created successfully: {}", savedActivity.getActivityId());

        return savedActivity;
    }

    @Transactional
    public Activity updateActivity(String activityId, ActivityUpdateRequest request) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + activityId));

        verifyActivityAccess(activity);
        log.info("Updating activity: {}", activityId);

        if (request.getActivityType() != null) {
            activity.setActivityType(request.getActivityType());
        }
        if (request.getActivitySubject() != null) {
            activity.setActivitySubject(request.getActivitySubject());
        }
        if (request.getActivityDescription() != null) {
            activity.setActivityDescription(request.getActivityDescription());
        }
        if (request.getActivityStatus() != null) {
            activity.setActivityStatus(request.getActivityStatus());
            if ("COMPLETED".equals(request.getActivityStatus()) && activity.getCompletedDate() == null) {
                activity.setCompletedDate(LocalDateTime.now());
            }
        }
        if (request.getActivityDate() != null) {
            activity.setActivityDate(request.getActivityDate());
        }
        if (request.getDueDate() != null) {
            activity.setDueDate(request.getDueDate());
        }
        if (request.getDurationMinutes() != null) {
            activity.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getPriority() != null) {
            activity.setPriority(request.getPriority());
        }
        if (request.getRelatedType() != null) {
            activity.setRelatedType(request.getRelatedType());
        }
        if (request.getRelatedId() != null) {
            activity.setRelatedId(request.getRelatedId());
        }
        if (request.getUseAt() != null) {
            activity.setUseAt(request.getUseAt());
        }
        if (request.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Assigned user not found: " + request.getAssignedUserId()));
            activity.setAssignedUser(assignedUser);
        }

        Activity updatedActivity = activityRepository.save(activity);
        log.info("Activity updated successfully: {}", updatedActivity.getActivityId());

        return updatedActivity;
    }

    @Transactional
    public Activity completeActivity(String activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + activityId));

        verifyActivityAccess(activity);

        activity.setActivityStatus("COMPLETED");
        activity.setCompletedDate(LocalDateTime.now());

        Activity completedActivity = activityRepository.save(activity);
        log.info("Activity completed: {}", activityId);

        return completedActivity;
    }

    public Optional<Activity> getActivity(String activityId) {
        Optional<Activity> activity = activityRepository.findById(activityId);
        activity.ifPresent(this::verifyActivityAccess);
        return activity;
    }

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public List<Activity> getActivitiesByType(String type) {
        return activityRepository.findByActivityType(type);
    }

    public List<Activity> getActivitiesByStatus(String status) {
        return activityRepository.findByActivityStatus(status);
    }

    public List<Activity> getActivitiesByPriority(String priority) {
        return activityRepository.findByPriority(priority);
    }

    public List<Activity> getActivitiesByAssignedUser(String userId) {
        return activityRepository.findByAssignedUserId(userId);
    }

    public List<Activity> getActivitiesForEntity(String relatedType, String relatedId) {
        return activityRepository.findByRelatedEntity(relatedType, relatedId);
    }

    public List<Activity> getActivitiesByDueDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return activityRepository.findByDueDateBetween(startDate, endDate);
    }

    public List<Activity> getOverdueActivities() {
        return activityRepository.findOverdueActivities(LocalDateTime.now());
    }

    public List<Activity> getPendingActivitiesForUser(String userId) {
        return activityRepository.findPendingActivitiesByUserId(userId);
    }

    public List<Activity> searchActivities(String keyword) {
        return activityRepository.searchByKeyword(keyword);
    }

    @Transactional
    public void deleteActivity(String activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + activityId));

        verifyActivityAccess(activity);

        log.info("Deleting activity: {}", activityId);
        activityRepository.delete(activity);
        log.info("Activity deleted successfully: {}", activityId);
    }

    private String generateActivityId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "ACT" + timestamp.substring(timestamp.length() - 10) + suffix;
    }
}
