package com.example.egov.web.admin.dto;

import com.example.egov.domain.admin.Tenant;
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
public class TenantResponse {

    private String tenantId;
    private String tenantName;
    private String tenantDescription;
    private String useAt;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static TenantResponse from(Tenant tenant) {
        if (tenant == null) {
            return null;
        }

        return TenantResponse.builder()
                .tenantId(tenant.getTenantId())
                .tenantName(tenant.getTenantName())
                .tenantDescription(tenant.getTenantDescription())
                .useAt(tenant.getUseAt())
                .createdDate(tenant.getCreatedDate())
                .updatedDate(tenant.getUpdatedDate())
                .build();
    }

    public static List<TenantResponse> fromList(List<Tenant> tenants) {
        if (tenants == null) {
            return null;
        }
        return tenants.stream()
                .map(TenantResponse::from)
                .collect(Collectors.toList());
    }
}
