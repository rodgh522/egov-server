package com.example.egov.service.sales;

import com.example.egov.config.multitenancy.TenantContext;
import com.example.egov.domain.admin.User;
import com.example.egov.domain.admin.UserRepository;
import com.example.egov.domain.sales.Customer;
import com.example.egov.domain.sales.CustomerRepository;
import com.example.egov.domain.sales.Lead;
import com.example.egov.domain.sales.LeadRepository;
import com.example.egov.web.sales.dto.LeadCreateRequest;
import com.example.egov.web.sales.dto.LeadUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeadService {

    private static final String SYSTEM_TENANT_ID = "SYSTEM";

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    private boolean isSystemTenant() {
        return SYSTEM_TENANT_ID.equals(TenantContext.getCurrentTenantId());
    }

    private void verifyLeadAccess(Lead lead) {
        if (!isSystemTenant() && !lead.getTenantId().equals(TenantContext.getCurrentTenantId())) {
            throw new AccessDeniedException("Access denied to lead: " + lead.getLeadId());
        }
    }

    @Transactional
    public Lead createLead(LeadCreateRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();

        String leadId = generateLeadId();
        log.info("Creating lead: {} for tenant: {}", leadId, tenantId);

        Lead lead = new Lead();
        lead.setLeadId(leadId);
        lead.setLeadName(request.getLeadName());
        lead.setCompanyName(request.getCompanyName());
        lead.setContactName(request.getContactName());
        lead.setEmail(request.getEmail());
        lead.setPhone(request.getPhone());
        lead.setLeadSource(request.getLeadSource());
        lead.setLeadStatus(request.getLeadStatus() != null ? request.getLeadStatus() : "NEW");
        lead.setLeadScore(request.getLeadScore() != null ? request.getLeadScore() : 0);
        lead.setIndustry(request.getIndustry());
        lead.setEstimatedRevenue(request.getEstimatedRevenue());
        lead.setDescription(request.getDescription());
        lead.setUseAt(request.getUseAt() != null ? request.getUseAt() : "Y");
        lead.setTenantId(tenantId);

        if (request.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Assigned user not found: " + request.getAssignedUserId()));
            lead.setAssignedUser(assignedUser);
        }

        Lead savedLead = leadRepository.save(lead);
        log.info("Lead created successfully: {}", savedLead.getLeadId());

        return savedLead;
    }

    @Transactional
    public Lead updateLead(String leadId, LeadUpdateRequest request) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));

        verifyLeadAccess(lead);
        log.info("Updating lead: {}", leadId);

        if (request.getLeadName() != null) {
            lead.setLeadName(request.getLeadName());
        }
        if (request.getCompanyName() != null) {
            lead.setCompanyName(request.getCompanyName());
        }
        if (request.getContactName() != null) {
            lead.setContactName(request.getContactName());
        }
        if (request.getEmail() != null) {
            lead.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            lead.setPhone(request.getPhone());
        }
        if (request.getLeadSource() != null) {
            lead.setLeadSource(request.getLeadSource());
        }
        if (request.getLeadStatus() != null) {
            lead.setLeadStatus(request.getLeadStatus());
        }
        if (request.getLeadScore() != null) {
            lead.setLeadScore(request.getLeadScore());
        }
        if (request.getIndustry() != null) {
            lead.setIndustry(request.getIndustry());
        }
        if (request.getEstimatedRevenue() != null) {
            lead.setEstimatedRevenue(request.getEstimatedRevenue());
        }
        if (request.getDescription() != null) {
            lead.setDescription(request.getDescription());
        }
        if (request.getUseAt() != null) {
            lead.setUseAt(request.getUseAt());
        }
        if (request.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Assigned user not found: " + request.getAssignedUserId()));
            lead.setAssignedUser(assignedUser);
        }

        Lead updatedLead = leadRepository.save(lead);
        log.info("Lead updated successfully: {}", updatedLead.getLeadId());

        return updatedLead;
    }

    @Transactional
    public Customer convertLeadToCustomer(String leadId, String customerCode, String customerName) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));

        verifyLeadAccess(lead);

        if (lead.getConvertedCustomer() != null) {
            throw new IllegalStateException("Lead is already converted");
        }

        if (customerRepository.existsByCustomerCode(customerCode)) {
            throw new IllegalArgumentException("Customer code already exists: " + customerCode);
        }

        log.info("Converting lead: {} to customer", leadId);

        // Create new customer from lead data
        String customerId = "CUS" + String.valueOf(System.currentTimeMillis()).substring(3) +
                UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setCustomerName(customerName != null ? customerName : lead.getCompanyName());
        customer.setCustomerCode(customerCode);
        customer.setCustomerType("COMPANY");
        customer.setIndustry(lead.getIndustry());
        customer.setPhone(lead.getPhone());
        customer.setEmail(lead.getEmail());
        customer.setAssignedUser(lead.getAssignedUser());
        customer.setUseAt("Y");
        customer.setTenantId(lead.getTenantId());

        Customer savedCustomer = customerRepository.save(customer);

        // Update lead as converted
        lead.setLeadStatus("CONVERTED");
        lead.setConvertedCustomer(savedCustomer);
        lead.setConvertedDate(LocalDateTime.now());
        leadRepository.save(lead);

        log.info("Lead converted successfully to customer: {}", savedCustomer.getCustomerId());

        return savedCustomer;
    }

    public Optional<Lead> getLead(String leadId) {
        Optional<Lead> lead = leadRepository.findById(leadId);
        lead.ifPresent(this::verifyLeadAccess);
        return lead;
    }

    public List<Lead> getAllLeads() {
        return leadRepository.findAll();
    }

    public List<Lead> getActiveLeads() {
        return leadRepository.findActiveLeads();
    }

    public List<Lead> getLeadsByStatus(String status) {
        return leadRepository.findByLeadStatus(status);
    }

    public List<Lead> getLeadsBySource(String source) {
        return leadRepository.findByLeadSource(source);
    }

    public List<Lead> getLeadsByAssignedUser(String userId) {
        return leadRepository.findByAssignedUserId(userId);
    }

    public List<Lead> searchLeads(String keyword) {
        return leadRepository.searchByKeyword(keyword);
    }

    @Transactional
    public void deleteLead(String leadId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));

        verifyLeadAccess(lead);

        log.info("Deleting lead: {}", leadId);
        leadRepository.delete(lead);
        log.info("Lead deleted successfully: {}", leadId);
    }

    private String generateLeadId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "LED" + timestamp.substring(timestamp.length() - 10) + suffix;
    }
}
