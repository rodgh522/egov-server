package com.example.egov.web.admin.dto;

import com.example.egov.domain.admin.User;
import com.example.egov.domain.admin.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * User response DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String esntlId;
    private String userId;
    private String userName;
    private String email;
    private String phone;
    private String groupId;
    private String groupName;
    private String branchId;
    private String branchName;
    private String positionId;
    private String positionName;
    private String managerId;
    private String managerName;
    private String statusCode;
    private String useAt;
    private String tenantId;
    private List<RoleInfo> roles;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleInfo {
        private String roleId;
        private String roleName;
        private String isPrimary;
    }

    /**
     * Convert User entity to UserResponse DTO
     */
    public static UserResponse from(User user) {
        if (user == null) {
            return null;
        }

        List<RoleInfo> roles = user.getUserRoles() != null
                ? user.getUserRoles().stream()
                    .map(UserResponse::toRoleInfo)
                    .toList()
                : Collections.emptyList();

        return UserResponse.builder()
                .esntlId(user.getId())
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .groupId(user.getGroup() != null ? user.getGroup().getGroupId() : null)
                .groupName(user.getGroup() != null ? user.getGroup().getGroupName() : null)
                .branchId(user.getBranch() != null ? user.getBranch().getBranchId() : null)
                .branchName(user.getBranch() != null ? user.getBranch().getBranchName() : null)
                .positionId(user.getPosition() != null ? user.getPosition().getPositionId() : null)
                .positionName(user.getPosition() != null ? user.getPosition().getPositionName() : null)
                .managerId(user.getManager() != null ? user.getManager().getId() : null)
                .managerName(user.getManager() != null ? user.getManager().getUserName() : null)
                .statusCode(user.getStatusCode())
                .useAt(user.getUseAt())
                .tenantId(user.getTenantId())
                .roles(roles)
                .createdDate(user.getCreatedDate())
                .updatedDate(user.getUpdatedDate())
                .build();
    }

    private static RoleInfo toRoleInfo(UserRole userRole) {
        return RoleInfo.builder()
                .roleId(userRole.getRole().getRoleId())
                .roleName(userRole.getRole().getRoleName())
                .isPrimary(userRole.getIsPrimary())
                .build();
    }

    /**
     * Convert list of User entities to list of UserResponse DTOs
     */
    public static List<UserResponse> fromList(List<User> users) {
        if (users == null) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(UserResponse::from)
                .toList();
    }
}
