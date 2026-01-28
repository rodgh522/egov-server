package com.example.egov.web.sales;

import com.example.egov.domain.sales.Opportunity;
import com.example.egov.service.sales.OpportunityService;
import com.example.egov.web.sales.dto.OpportunityCreateRequest;
import com.example.egov.web.sales.dto.OpportunityResponse;
import com.example.egov.web.sales.dto.OpportunityUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/opportunities")
@RequiredArgsConstructor
public class OpportunityController {

    private final OpportunityService opportunityService;

    @PostMapping
    public ResponseEntity<OpportunityResponse> createOpportunity(@Valid @RequestBody OpportunityCreateRequest request) {
        log.info("Creating opportunity: {}", request.getOpportunityName());
        Opportunity opportunity = opportunityService.createOpportunity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(OpportunityResponse.from(opportunity));
    }

    @PutMapping("/{opportunityId}")
    public ResponseEntity<OpportunityResponse> updateOpportunity(
            @PathVariable String opportunityId,
            @Valid @RequestBody OpportunityUpdateRequest request) {
        log.info("Updating opportunity: {}", opportunityId);
        Opportunity opportunity = opportunityService.updateOpportunity(opportunityId, request);
        return ResponseEntity.ok(OpportunityResponse.from(opportunity));
    }

    @GetMapping("/{opportunityId}")
    public ResponseEntity<OpportunityResponse> getOpportunity(@PathVariable String opportunityId) {
        log.debug("Fetching opportunity: {}", opportunityId);
        return opportunityService.getOpportunity(opportunityId)
                .map(opportunity -> ResponseEntity.ok(OpportunityResponse.from(opportunity)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<OpportunityResponse>> getAllOpportunities() {
        log.debug("Fetching all opportunities");
        List<Opportunity> opportunities = opportunityService.getAllOpportunities();
        return ResponseEntity.ok(OpportunityResponse.fromList(opportunities));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OpportunityResponse>> getOpportunitiesByCustomer(@PathVariable String customerId) {
        log.debug("Fetching opportunities for customer: {}", customerId);
        List<Opportunity> opportunities = opportunityService.getOpportunitiesByCustomer(customerId);
        return ResponseEntity.ok(OpportunityResponse.fromList(opportunities));
    }

    @GetMapping("/stage/{stageId}")
    public ResponseEntity<List<OpportunityResponse>> getOpportunitiesByStage(@PathVariable String stageId) {
        log.debug("Fetching opportunities by stage: {}", stageId);
        List<Opportunity> opportunities = opportunityService.getOpportunitiesByStage(stageId);
        return ResponseEntity.ok(OpportunityResponse.fromList(opportunities));
    }

    @GetMapping("/assigned/{userId}")
    public ResponseEntity<List<OpportunityResponse>> getOpportunitiesByAssignedUser(@PathVariable String userId) {
        log.debug("Fetching opportunities assigned to user: {}", userId);
        List<Opportunity> opportunities = opportunityService.getOpportunitiesByAssignedUser(userId);
        return ResponseEntity.ok(OpportunityResponse.fromList(opportunities));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<OpportunityResponse>> getOpportunitiesByBranch(@PathVariable String branchId) {
        log.debug("Fetching opportunities by branch: {}", branchId);
        List<Opportunity> opportunities = opportunityService.getOpportunitiesByBranch(branchId);
        return ResponseEntity.ok(OpportunityResponse.fromList(opportunities));
    }

    @GetMapping("/open")
    public ResponseEntity<List<OpportunityResponse>> getOpenOpportunities() {
        log.debug("Fetching open opportunities");
        List<Opportunity> opportunities = opportunityService.getOpenOpportunities();
        return ResponseEntity.ok(OpportunityResponse.fromList(opportunities));
    }

    @GetMapping("/won")
    public ResponseEntity<List<OpportunityResponse>> getWonOpportunities() {
        log.debug("Fetching won opportunities");
        List<Opportunity> opportunities = opportunityService.getWonOpportunities();
        return ResponseEntity.ok(OpportunityResponse.fromList(opportunities));
    }

    @GetMapping("/lost")
    public ResponseEntity<List<OpportunityResponse>> getLostOpportunities() {
        log.debug("Fetching lost opportunities");
        List<Opportunity> opportunities = opportunityService.getLostOpportunities();
        return ResponseEntity.ok(OpportunityResponse.fromList(opportunities));
    }

    @GetMapping("/close-date")
    public ResponseEntity<List<OpportunityResponse>> getOpportunitiesByCloseDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("Fetching opportunities by close date range: {} - {}", startDate, endDate);
        List<Opportunity> opportunities = opportunityService.getOpportunitiesByCloseDateRange(startDate, endDate);
        return ResponseEntity.ok(OpportunityResponse.fromList(opportunities));
    }

    @GetMapping("/search")
    public ResponseEntity<List<OpportunityResponse>> searchOpportunities(@RequestParam String keyword) {
        log.debug("Searching opportunities with keyword: {}", keyword);
        List<Opportunity> opportunities = opportunityService.searchOpportunities(keyword);
        return ResponseEntity.ok(OpportunityResponse.fromList(opportunities));
    }

    @DeleteMapping("/{opportunityId}")
    public ResponseEntity<Void> deleteOpportunity(@PathVariable String opportunityId) {
        log.info("Deleting opportunity: {}", opportunityId);
        opportunityService.deleteOpportunity(opportunityId);
        return ResponseEntity.noContent().build();
    }
}
