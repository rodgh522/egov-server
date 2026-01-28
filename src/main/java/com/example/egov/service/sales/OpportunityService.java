package com.example.egov.service.sales;

import com.example.egov.config.multitenancy.TenantContext;
import com.example.egov.domain.admin.Branch;
import com.example.egov.domain.admin.BranchRepository;
import com.example.egov.domain.admin.User;
import com.example.egov.domain.admin.UserRepository;
import com.example.egov.domain.sales.*;
import com.example.egov.web.sales.dto.OpportunityCreateRequest;
import com.example.egov.web.sales.dto.OpportunityUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OpportunityService {

    private static final String SYSTEM_TENANT_ID = "SYSTEM";

    private final OpportunityRepository opportunityRepository;
    private final CustomerRepository customerRepository;
    private final ContactRepository contactRepository;
    private final PipelineStageRepository pipelineStageRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    private boolean isSystemTenant() {
        return SYSTEM_TENANT_ID.equals(TenantContext.getCurrentTenantId());
    }

    private void verifyOpportunityAccess(Opportunity opportunity) {
        if (!isSystemTenant() && !opportunity.getTenantId().equals(TenantContext.getCurrentTenantId())) {
            throw new AccessDeniedException("Access denied to opportunity: " + opportunity.getOpportunityId());
        }
    }

    @Transactional
    public Opportunity createOpportunity(OpportunityCreateRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + request.getCustomerId()));

        PipelineStage stage = pipelineStageRepository.findById(request.getStageId())
                .orElseThrow(() -> new IllegalArgumentException("Pipeline stage not found: " + request.getStageId()));

        String opportunityId = generateOpportunityId();
        log.info("Creating opportunity: {} for tenant: {}", opportunityId, tenantId);

        Opportunity opportunity = new Opportunity();
        opportunity.setOpportunityId(opportunityId);
        opportunity.setOpportunityName(request.getOpportunityName());
        opportunity.setCustomer(customer);
        opportunity.setStage(stage);
        opportunity.setAmount(request.getAmount());
        opportunity.setProbability(stage.getProbability());
        opportunity.setExpectedCloseDate(request.getExpectedCloseDate());
        opportunity.setLeadSource(request.getLeadSource());
        opportunity.setDescription(request.getDescription());
        opportunity.setNextStep(request.getNextStep());
        opportunity.setCompetitorInfo(request.getCompetitorInfo());
        opportunity.setUseAt(request.getUseAt() != null ? request.getUseAt() : "Y");
        opportunity.setTenantId(tenantId);

        if (request.getContactId() != null) {
            Contact contact = contactRepository.findById(request.getContactId())
                    .orElseThrow(() -> new IllegalArgumentException("Contact not found: " + request.getContactId()));
            opportunity.setContact(contact);
        }

        if (request.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Assigned user not found: " + request.getAssignedUserId()));
            opportunity.setAssignedUser(assignedUser);
        }

        if (request.getBranchId() != null) {
            Branch branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + request.getBranchId()));
            opportunity.setBranch(branch);
        }

        Opportunity savedOpportunity = opportunityRepository.save(opportunity);
        log.info("Opportunity created successfully: {}", savedOpportunity.getOpportunityId());

        return savedOpportunity;
    }

    @Transactional
    public Opportunity updateOpportunity(String opportunityId, OpportunityUpdateRequest request) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found: " + opportunityId));

        verifyOpportunityAccess(opportunity);
        log.info("Updating opportunity: {}", opportunityId);

        if (request.getOpportunityName() != null) {
            opportunity.setOpportunityName(request.getOpportunityName());
        }
        if (request.getStageId() != null) {
            PipelineStage stage = pipelineStageRepository.findById(request.getStageId())
                    .orElseThrow(() -> new IllegalArgumentException("Pipeline stage not found: " + request.getStageId()));
            opportunity.setStage(stage);
            opportunity.setProbability(stage.getProbability());

            // Set actual close date if moving to won/lost stage
            if ("Y".equals(stage.getIsWon()) || "Y".equals(stage.getIsLost())) {
                if (opportunity.getActualCloseDate() == null) {
                    opportunity.setActualCloseDate(LocalDate.now());
                }
            }
        }
        if (request.getAmount() != null) {
            opportunity.setAmount(request.getAmount());
        }
        if (request.getExpectedCloseDate() != null) {
            opportunity.setExpectedCloseDate(request.getExpectedCloseDate());
        }
        if (request.getLeadSource() != null) {
            opportunity.setLeadSource(request.getLeadSource());
        }
        if (request.getDescription() != null) {
            opportunity.setDescription(request.getDescription());
        }
        if (request.getNextStep() != null) {
            opportunity.setNextStep(request.getNextStep());
        }
        if (request.getCompetitorInfo() != null) {
            opportunity.setCompetitorInfo(request.getCompetitorInfo());
        }
        if (request.getWonReason() != null) {
            opportunity.setWonReason(request.getWonReason());
        }
        if (request.getLostReason() != null) {
            opportunity.setLostReason(request.getLostReason());
        }
        if (request.getUseAt() != null) {
            opportunity.setUseAt(request.getUseAt());
        }
        if (request.getContactId() != null) {
            Contact contact = contactRepository.findById(request.getContactId())
                    .orElseThrow(() -> new IllegalArgumentException("Contact not found: " + request.getContactId()));
            opportunity.setContact(contact);
        }
        if (request.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Assigned user not found: " + request.getAssignedUserId()));
            opportunity.setAssignedUser(assignedUser);
        }
        if (request.getBranchId() != null) {
            Branch branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + request.getBranchId()));
            opportunity.setBranch(branch);
        }

        Opportunity updatedOpportunity = opportunityRepository.save(opportunity);
        log.info("Opportunity updated successfully: {}", updatedOpportunity.getOpportunityId());

        return updatedOpportunity;
    }

    public Optional<Opportunity> getOpportunity(String opportunityId) {
        Optional<Opportunity> opportunity = opportunityRepository.findById(opportunityId);
        opportunity.ifPresent(this::verifyOpportunityAccess);
        return opportunity;
    }

    public List<Opportunity> getAllOpportunities() {
        return opportunityRepository.findAll();
    }

    public List<Opportunity> getOpportunitiesByCustomer(String customerId) {
        return opportunityRepository.findByCustomerId(customerId);
    }

    public List<Opportunity> getOpportunitiesByStage(String stageId) {
        return opportunityRepository.findByStageId(stageId);
    }

    public List<Opportunity> getOpportunitiesByAssignedUser(String userId) {
        return opportunityRepository.findByAssignedUserId(userId);
    }

    public List<Opportunity> getOpportunitiesByBranch(String branchId) {
        return opportunityRepository.findByBranchId(branchId);
    }

    public List<Opportunity> getOpenOpportunities() {
        return opportunityRepository.findOpenOpportunities();
    }

    public List<Opportunity> getWonOpportunities() {
        return opportunityRepository.findWonOpportunities();
    }

    public List<Opportunity> getLostOpportunities() {
        return opportunityRepository.findLostOpportunities();
    }

    public List<Opportunity> getOpportunitiesByCloseDateRange(LocalDate startDate, LocalDate endDate) {
        return opportunityRepository.findByExpectedCloseDateBetween(startDate, endDate);
    }

    public List<Opportunity> searchOpportunities(String keyword) {
        return opportunityRepository.searchByKeyword(keyword);
    }

    @Transactional
    public void deleteOpportunity(String opportunityId) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found: " + opportunityId));

        verifyOpportunityAccess(opportunity);

        log.info("Deleting opportunity: {}", opportunityId);
        opportunityRepository.delete(opportunity);
        log.info("Opportunity deleted successfully: {}", opportunityId);
    }

    private String generateOpportunityId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "OPP" + timestamp.substring(timestamp.length() - 10) + suffix;
    }
}
