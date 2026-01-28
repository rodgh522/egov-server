package com.example.egov.domain.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {

    Optional<Activity> findByActivityId(String activityId);

    List<Activity> findByActivityType(String activityType);

    List<Activity> findByActivityStatus(String activityStatus);

    List<Activity> findByPriority(String priority);

    List<Activity> findByUseAt(String useAt);

    @Query("SELECT a FROM Activity a WHERE a.assignedUser.id = :userId")
    List<Activity> findByAssignedUserId(@Param("userId") String userId);

    @Query("SELECT a FROM Activity a WHERE a.relatedType = :relatedType AND a.relatedId = :relatedId")
    List<Activity> findByRelatedEntity(
            @Param("relatedType") String relatedType,
            @Param("relatedId") String relatedId);

    @Query("SELECT a FROM Activity a WHERE a.dueDate BETWEEN :startDate AND :endDate")
    List<Activity> findByDueDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM Activity a WHERE a.activityStatus = 'PLANNED' AND a.dueDate < :now")
    List<Activity> findOverdueActivities(@Param("now") LocalDateTime now);

    @Query("SELECT a FROM Activity a WHERE a.assignedUser.id = :userId AND a.activityStatus = 'PLANNED' ORDER BY a.dueDate ASC")
    List<Activity> findPendingActivitiesByUserId(@Param("userId") String userId);

    @Query("SELECT a FROM Activity a WHERE a.activitySubject LIKE %:keyword%")
    List<Activity> searchByKeyword(@Param("keyword") String keyword);

    List<Activity> findByTenantId(String tenantId);
}
