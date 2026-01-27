package com.example.egov.web.admin.dto;

import com.example.egov.domain.admin.Branch;
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
public class BranchResponse {

    private String branchId;
    private String branchName;
    private String branchCode;
    private String branchAddress;
    private String branchPhone;
    private String parentBranchId;
    private String tenantId;
    private String useAt;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static BranchResponse from(Branch branch) {
        if (branch == null) {
            return null;
        }

        return BranchResponse.builder()
                .branchId(branch.getBranchId())
                .branchName(branch.getBranchName())
                .branchCode(branch.getBranchCode())
                .branchAddress(branch.getBranchAddress())
                .branchPhone(branch.getBranchPhone())
                .parentBranchId(branch.getParentBranch() != null ? branch.getParentBranch().getBranchId() : null)
                .tenantId(branch.getTenantId())
                .useAt(branch.getUseAt())
                .createdDate(branch.getCreatedDate())
                .updatedDate(branch.getUpdatedDate())
                .build();
    }

    public static List<BranchResponse> fromList(List<Branch> branches) {
        if (branches == null) {
            return null;
        }
        return branches.stream()
                .map(BranchResponse::from)
                .collect(Collectors.toList());
    }
}
