package com.example.egov.web.admin.dto;

import com.example.egov.domain.admin.Group;
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
public class GroupResponse {

    private String groupId;
    private String groupName;
    private String groupCode;
    private String groupDescription;
    private String branchId;
    private String branchName;
    private String tenantId;
    private String useAt;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static GroupResponse from(Group group) {
        if (group == null) {
            return null;
        }

        return GroupResponse.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .groupCode(group.getGroupCode())
                .groupDescription(group.getGroupDescription())
                .branchId(group.getBranch() != null ? group.getBranch().getBranchId() : null)
                .branchName(group.getBranch() != null ? group.getBranch().getBranchName() : null)
                .tenantId(group.getTenantId())
                .useAt(group.getUseAt())
                .createdDate(group.getCreatedDate())
                .updatedDate(group.getUpdatedDate())
                .build();
    }

    public static List<GroupResponse> fromList(List<Group> groups) {
        if (groups == null) {
            return null;
        }
        return groups.stream()
                .map(GroupResponse::from)
                .collect(Collectors.toList());
    }
}
