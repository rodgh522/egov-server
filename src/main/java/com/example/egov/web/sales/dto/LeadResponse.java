package com.example.egov.web.sales.dto;

import com.example.egov.domain.sales.Lead;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadResponse {

    private String leadId;
    private String leadName;
    private String companyName;
    private String contactName;
    private String email;
    private String phone;
    private String leadSource;
    private String leadStatus;
    private Integer leadScore;
    private String industry;
    private BigDecimal estimatedRevenue;
    private String description;
    private String assignedUserId;
    private String assignedUserName;
    private String convertedCustomerId;
    private LocalDateTime convertedDate;
    private String tenantId;
    private String useAt;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static LeadResponse from(Lead lead) {
        if (lead == null) {
            return null;
        }

        return LeadResponse.builder()
                .leadId(lead.getLeadId())
                .leadName(lead.getLeadName())
                .companyName(lead.getCompanyName())
                .contactName(lead.getContactName())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .leadSource(lead.getLeadSource())
                .leadStatus(lead.getLeadStatus())
                .leadScore(lead.getLeadScore())
                .industry(lead.getIndustry())
                .estimatedRevenue(lead.getEstimatedRevenue())
                .description(lead.getDescription())
                .assignedUserId(lead.getAssignedUser() != null ? lead.getAssignedUser().getId() : null)
                .assignedUserName(lead.getAssignedUser() != null ? lead.getAssignedUser().getUserName() : null)
                .convertedCustomerId(lead.getConvertedCustomer() != null ? lead.getConvertedCustomer().getCustomerId() : null)
                .convertedDate(lead.getConvertedDate())
                .tenantId(lead.getTenantId())
                .useAt(lead.getUseAt())
                .createdDate(lead.getCreatedDate())
                .updatedDate(lead.getUpdatedDate())
                .build();
    }

    public static List<LeadResponse> fromList(List<Lead> leads) {
        if (leads == null) {
            return null;
        }
        return leads.stream()
                .map(LeadResponse::from)
                .collect(Collectors.toList());
    }
}
