package com.example.egov.web.sales.dto;

import com.example.egov.domain.sales.SalesTarget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesTargetResponse {

    private String targetId;
    private Integer targetYear;
    private Integer targetMonth;
    private BigDecimal targetAmount;
    private BigDecimal achievedAmount;
    private BigDecimal achievementRate;
    private String targetType;
    private String userId;
    private String userName;
    private String branchId;
    private String branchName;
    private String tenantId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static SalesTargetResponse from(SalesTarget target) {
        if (target == null) {
            return null;
        }

        BigDecimal achievementRate = BigDecimal.ZERO;
        if (target.getTargetAmount() != null && target.getTargetAmount().compareTo(BigDecimal.ZERO) > 0
                && target.getAchievedAmount() != null) {
            achievementRate = target.getAchievedAmount()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(target.getTargetAmount(), 2, RoundingMode.HALF_UP);
        }

        return SalesTargetResponse.builder()
                .targetId(target.getTargetId())
                .targetYear(target.getTargetYear())
                .targetMonth(target.getTargetMonth())
                .targetAmount(target.getTargetAmount())
                .achievedAmount(target.getAchievedAmount())
                .achievementRate(achievementRate)
                .targetType(target.getTargetType())
                .userId(target.getUser() != null ? target.getUser().getId() : null)
                .userName(target.getUser() != null ? target.getUser().getUserName() : null)
                .branchId(target.getBranch() != null ? target.getBranch().getBranchId() : null)
                .branchName(target.getBranch() != null ? target.getBranch().getBranchName() : null)
                .tenantId(target.getTenantId())
                .createdDate(target.getCreatedDate())
                .updatedDate(target.getUpdatedDate())
                .build();
    }

    public static List<SalesTargetResponse> fromList(List<SalesTarget> targets) {
        if (targets == null) {
            return null;
        }
        return targets.stream()
                .map(SalesTargetResponse::from)
                .collect(Collectors.toList());
    }
}
