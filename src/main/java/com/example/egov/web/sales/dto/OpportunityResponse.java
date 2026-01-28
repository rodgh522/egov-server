package com.example.egov.web.sales.dto;

import com.example.egov.domain.sales.Opportunity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityResponse {

    private String opportunityId;
    private String opportunityName;
    private String customerId;
    private String customerName;
    private String contactId;
    private String contactName;
    private String stageId;
    private String stageName;
    private BigDecimal amount;
    private Integer probability;
    private LocalDate expectedCloseDate;
    private LocalDate actualCloseDate;
    private String leadSource;
    private String description;
    private String nextStep;
    private String competitorInfo;
    private String assignedUserId;
    private String assignedUserName;
    private String branchId;
    private String branchName;
    private String wonReason;
    private String lostReason;
    private String tenantId;
    private String useAt;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static OpportunityResponse from(Opportunity opportunity) {
        if (opportunity == null) {
            return null;
        }

        return OpportunityResponse.builder()
                .opportunityId(opportunity.getOpportunityId())
                .opportunityName(opportunity.getOpportunityName())
                .customerId(opportunity.getCustomer() != null ? opportunity.getCustomer().getCustomerId() : null)
                .customerName(opportunity.getCustomer() != null ? opportunity.getCustomer().getCustomerName() : null)
                .contactId(opportunity.getContact() != null ? opportunity.getContact().getContactId() : null)
                .contactName(opportunity.getContact() != null ? opportunity.getContact().getContactName() : null)
                .stageId(opportunity.getStage() != null ? opportunity.getStage().getStageId() : null)
                .stageName(opportunity.getStage() != null ? opportunity.getStage().getStageName() : null)
                .amount(opportunity.getAmount())
                .probability(opportunity.getProbability())
                .expectedCloseDate(opportunity.getExpectedCloseDate())
                .actualCloseDate(opportunity.getActualCloseDate())
                .leadSource(opportunity.getLeadSource())
                .description(opportunity.getDescription())
                .nextStep(opportunity.getNextStep())
                .competitorInfo(opportunity.getCompetitorInfo())
                .assignedUserId(opportunity.getAssignedUser() != null ? opportunity.getAssignedUser().getId() : null)
                .assignedUserName(opportunity.getAssignedUser() != null ? opportunity.getAssignedUser().getUserName() : null)
                .branchId(opportunity.getBranch() != null ? opportunity.getBranch().getBranchId() : null)
                .branchName(opportunity.getBranch() != null ? opportunity.getBranch().getBranchName() : null)
                .wonReason(opportunity.getWonReason())
                .lostReason(opportunity.getLostReason())
                .tenantId(opportunity.getTenantId())
                .useAt(opportunity.getUseAt())
                .createdDate(opportunity.getCreatedDate())
                .updatedDate(opportunity.getUpdatedDate())
                .build();
    }

    public static List<OpportunityResponse> fromList(List<Opportunity> opportunities) {
        if (opportunities == null) {
            return null;
        }
        return opportunities.stream()
                .map(OpportunityResponse::from)
                .collect(Collectors.toList());
    }
}
