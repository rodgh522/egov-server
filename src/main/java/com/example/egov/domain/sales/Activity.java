package com.example.egov.domain.sales;

import com.example.egov.domain.admin.User;
import com.example.egov.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "ACTIVITIES")
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter
@Setter
@NoArgsConstructor
public class Activity extends BaseEntity {

    @Id
    @Column(name = "ACTIVITY_ID", length = 20)
    private String activityId;

    @Column(name = "ACTIVITY_TYPE", length = 20, nullable = false)
    private String activityType;

    @Column(name = "ACTIVITY_SUBJECT", length = 200, nullable = false)
    private String activitySubject;

    @Column(name = "ACTIVITY_DESCRIPTION", columnDefinition = "TEXT")
    private String activityDescription;

    @Column(name = "ACTIVITY_STATUS", length = 20)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String activityStatus = "PLANNED";

    @Column(name = "ACTIVITY_DATE")
    private LocalDateTime activityDate;

    @Column(name = "DUE_DATE")
    private LocalDateTime dueDate;

    @Column(name = "COMPLETED_DATE")
    private LocalDateTime completedDate;

    @Column(name = "DURATION_MINUTES")
    private Integer durationMinutes;

    @Column(name = "PRIORITY", length = 10)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String priority = "NORMAL";

    @Column(name = "RELATED_TYPE", length = 20)
    private String relatedType;

    @Column(name = "RELATED_ID", length = 20)
    private String relatedId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNED_USER_ID")
    private User assignedUser;

    @Column(name = "USE_AT", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String useAt = "Y";

    @Column(name = "CREATED_BY", length = 20)
    private String createdBy;
}
