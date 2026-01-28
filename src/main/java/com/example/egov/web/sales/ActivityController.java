package com.example.egov.web.sales;

import com.example.egov.domain.sales.Activity;
import com.example.egov.service.sales.ActivityService;
import com.example.egov.web.sales.dto.ActivityCreateRequest;
import com.example.egov.web.sales.dto.ActivityResponse;
import com.example.egov.web.sales.dto.ActivityUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityResponse> createActivity(@Valid @RequestBody ActivityCreateRequest request) {
        log.info("Creating activity: {}", request.getActivitySubject());
        Activity activity = activityService.createActivity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ActivityResponse.from(activity));
    }

    @PutMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> updateActivity(
            @PathVariable String activityId,
            @Valid @RequestBody ActivityUpdateRequest request) {
        log.info("Updating activity: {}", activityId);
        Activity activity = activityService.updateActivity(activityId, request);
        return ResponseEntity.ok(ActivityResponse.from(activity));
    }

    @PostMapping("/{activityId}/complete")
    public ResponseEntity<ActivityResponse> completeActivity(@PathVariable String activityId) {
        log.info("Completing activity: {}", activityId);
        Activity activity = activityService.completeActivity(activityId);
        return ResponseEntity.ok(ActivityResponse.from(activity));
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> getActivity(@PathVariable String activityId) {
        log.debug("Fetching activity: {}", activityId);
        return activityService.getActivity(activityId)
                .map(activity -> ResponseEntity.ok(ActivityResponse.from(activity)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getAllActivities() {
        log.debug("Fetching all activities");
        List<Activity> activities = activityService.getAllActivities();
        return ResponseEntity.ok(ActivityResponse.fromList(activities));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ActivityResponse>> getActivitiesByType(@PathVariable String type) {
        log.debug("Fetching activities by type: {}", type);
        List<Activity> activities = activityService.getActivitiesByType(type);
        return ResponseEntity.ok(ActivityResponse.fromList(activities));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ActivityResponse>> getActivitiesByStatus(@PathVariable String status) {
        log.debug("Fetching activities by status: {}", status);
        List<Activity> activities = activityService.getActivitiesByStatus(status);
        return ResponseEntity.ok(ActivityResponse.fromList(activities));
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<ActivityResponse>> getActivitiesByPriority(@PathVariable String priority) {
        log.debug("Fetching activities by priority: {}", priority);
        List<Activity> activities = activityService.getActivitiesByPriority(priority);
        return ResponseEntity.ok(ActivityResponse.fromList(activities));
    }

    @GetMapping("/assigned/{userId}")
    public ResponseEntity<List<ActivityResponse>> getActivitiesByAssignedUser(@PathVariable String userId) {
        log.debug("Fetching activities assigned to user: {}", userId);
        List<Activity> activities = activityService.getActivitiesByAssignedUser(userId);
        return ResponseEntity.ok(ActivityResponse.fromList(activities));
    }

    @GetMapping("/assigned/{userId}/pending")
    public ResponseEntity<List<ActivityResponse>> getPendingActivitiesForUser(@PathVariable String userId) {
        log.debug("Fetching pending activities for user: {}", userId);
        List<Activity> activities = activityService.getPendingActivitiesForUser(userId);
        return ResponseEntity.ok(ActivityResponse.fromList(activities));
    }

    @GetMapping("/related/{relatedType}/{relatedId}")
    public ResponseEntity<List<ActivityResponse>> getActivitiesForEntity(
            @PathVariable String relatedType,
            @PathVariable String relatedId) {
        log.debug("Fetching activities for entity: {} - {}", relatedType, relatedId);
        List<Activity> activities = activityService.getActivitiesForEntity(relatedType, relatedId);
        return ResponseEntity.ok(ActivityResponse.fromList(activities));
    }

    @GetMapping("/due-date")
    public ResponseEntity<List<ActivityResponse>> getActivitiesByDueDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.debug("Fetching activities by due date range: {} - {}", startDate, endDate);
        List<Activity> activities = activityService.getActivitiesByDueDateRange(startDate, endDate);
        return ResponseEntity.ok(ActivityResponse.fromList(activities));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<ActivityResponse>> getOverdueActivities() {
        log.debug("Fetching overdue activities");
        List<Activity> activities = activityService.getOverdueActivities();
        return ResponseEntity.ok(ActivityResponse.fromList(activities));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ActivityResponse>> searchActivities(@RequestParam String keyword) {
        log.debug("Searching activities with keyword: {}", keyword);
        List<Activity> activities = activityService.searchActivities(keyword);
        return ResponseEntity.ok(ActivityResponse.fromList(activities));
    }

    @DeleteMapping("/{activityId}")
    public ResponseEntity<Void> deleteActivity(@PathVariable String activityId) {
        log.info("Deleting activity: {}", activityId);
        activityService.deleteActivity(activityId);
        return ResponseEntity.noContent().build();
    }
}
