package com.example.egov.web.sales;

import com.example.egov.domain.sales.Customer;
import com.example.egov.domain.sales.Lead;
import com.example.egov.service.sales.LeadService;
import com.example.egov.web.sales.dto.CustomerResponse;
import com.example.egov.web.sales.dto.LeadCreateRequest;
import com.example.egov.web.sales.dto.LeadResponse;
import com.example.egov.web.sales.dto.LeadUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @PostMapping
    public ResponseEntity<LeadResponse> createLead(@Valid @RequestBody LeadCreateRequest request) {
        log.info("Creating lead: {}", request.getLeadName());
        Lead lead = leadService.createLead(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(LeadResponse.from(lead));
    }

    @PutMapping("/{leadId}")
    public ResponseEntity<LeadResponse> updateLead(
            @PathVariable String leadId,
            @Valid @RequestBody LeadUpdateRequest request) {
        log.info("Updating lead: {}", leadId);
        Lead lead = leadService.updateLead(leadId, request);
        return ResponseEntity.ok(LeadResponse.from(lead));
    }

    @PostMapping("/{leadId}/convert")
    public ResponseEntity<CustomerResponse> convertLead(
            @PathVariable String leadId,
            @RequestParam String customerCode,
            @RequestParam(required = false) String customerName) {
        log.info("Converting lead: {} to customer", leadId);
        Customer customer = leadService.convertLeadToCustomer(leadId, customerCode, customerName);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerResponse.from(customer));
    }

    @GetMapping("/{leadId}")
    public ResponseEntity<LeadResponse> getLead(@PathVariable String leadId) {
        log.debug("Fetching lead: {}", leadId);
        return leadService.getLead(leadId)
                .map(lead -> ResponseEntity.ok(LeadResponse.from(lead)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<LeadResponse>> getAllLeads() {
        log.debug("Fetching all leads");
        List<Lead> leads = leadService.getAllLeads();
        return ResponseEntity.ok(LeadResponse.fromList(leads));
    }

    @GetMapping("/active")
    public ResponseEntity<List<LeadResponse>> getActiveLeads() {
        log.debug("Fetching active leads");
        List<Lead> leads = leadService.getActiveLeads();
        return ResponseEntity.ok(LeadResponse.fromList(leads));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LeadResponse>> getLeadsByStatus(@PathVariable String status) {
        log.debug("Fetching leads by status: {}", status);
        List<Lead> leads = leadService.getLeadsByStatus(status);
        return ResponseEntity.ok(LeadResponse.fromList(leads));
    }

    @GetMapping("/source/{source}")
    public ResponseEntity<List<LeadResponse>> getLeadsBySource(@PathVariable String source) {
        log.debug("Fetching leads by source: {}", source);
        List<Lead> leads = leadService.getLeadsBySource(source);
        return ResponseEntity.ok(LeadResponse.fromList(leads));
    }

    @GetMapping("/assigned/{userId}")
    public ResponseEntity<List<LeadResponse>> getLeadsByAssignedUser(@PathVariable String userId) {
        log.debug("Fetching leads assigned to user: {}", userId);
        List<Lead> leads = leadService.getLeadsByAssignedUser(userId);
        return ResponseEntity.ok(LeadResponse.fromList(leads));
    }

    @GetMapping("/search")
    public ResponseEntity<List<LeadResponse>> searchLeads(@RequestParam String keyword) {
        log.debug("Searching leads with keyword: {}", keyword);
        List<Lead> leads = leadService.searchLeads(keyword);
        return ResponseEntity.ok(LeadResponse.fromList(leads));
    }

    @DeleteMapping("/{leadId}")
    public ResponseEntity<Void> deleteLead(@PathVariable String leadId) {
        log.info("Deleting lead: {}", leadId);
        leadService.deleteLead(leadId);
        return ResponseEntity.noContent().build();
    }
}
