package com.example.egov.web.sales;

import com.example.egov.domain.sales.SalesTarget;
import com.example.egov.service.sales.SalesTargetService;
import com.example.egov.web.sales.dto.SalesTargetCreateRequest;
import com.example.egov.web.sales.dto.SalesTargetResponse;
import com.example.egov.web.sales.dto.SalesTargetUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/sales-targets")
@RequiredArgsConstructor
public class SalesTargetController {

    private final SalesTargetService salesTargetService;

    @PostMapping
    public ResponseEntity<SalesTargetResponse> createTarget(@Valid @RequestBody SalesTargetCreateRequest request) {
        log.info("Creating sales target for year: {}", request.getTargetYear());
        SalesTarget target = salesTargetService.createTarget(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SalesTargetResponse.from(target));
    }

    @PutMapping("/{targetId}")
    public ResponseEntity<SalesTargetResponse> updateTarget(
            @PathVariable String targetId,
            @Valid @RequestBody SalesTargetUpdateRequest request) {
        log.info("Updating sales target: {}", targetId);
        SalesTarget target = salesTargetService.updateTarget(targetId, request);
        return ResponseEntity.ok(SalesTargetResponse.from(target));
    }

    @PatchMapping("/{targetId}/achieved")
    public ResponseEntity<SalesTargetResponse> updateAchievedAmount(
            @PathVariable String targetId,
            @RequestParam BigDecimal achievedAmount) {
        log.info("Updating achieved amount for target: {}", targetId);
        SalesTarget target = salesTargetService.updateAchievedAmount(targetId, achievedAmount);
        return ResponseEntity.ok(SalesTargetResponse.from(target));
    }

    @GetMapping("/{targetId}")
    public ResponseEntity<SalesTargetResponse> getTarget(@PathVariable String targetId) {
        log.debug("Fetching sales target: {}", targetId);
        return salesTargetService.getTarget(targetId)
                .map(target -> ResponseEntity.ok(SalesTargetResponse.from(target)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<SalesTargetResponse>> getAllTargets() {
        log.debug("Fetching all sales targets");
        List<SalesTarget> targets = salesTargetService.getAllTargets();
        return ResponseEntity.ok(SalesTargetResponse.fromList(targets));
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<List<SalesTargetResponse>> getTargetsByYear(@PathVariable Integer year) {
        log.debug("Fetching sales targets for year: {}", year);
        List<SalesTarget> targets = salesTargetService.getTargetsByYear(year);
        return ResponseEntity.ok(SalesTargetResponse.fromList(targets));
    }

    @GetMapping("/year/{year}/month/{month}")
    public ResponseEntity<List<SalesTargetResponse>> getTargetsByYearAndMonth(
            @PathVariable Integer year,
            @PathVariable Integer month) {
        log.debug("Fetching sales targets for year: {}, month: {}", year, month);
        List<SalesTarget> targets = salesTargetService.getTargetsByYearAndMonth(year, month);
        return ResponseEntity.ok(SalesTargetResponse.fromList(targets));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<SalesTargetResponse>> getTargetsByType(@PathVariable String type) {
        log.debug("Fetching sales targets by type: {}", type);
        List<SalesTarget> targets = salesTargetService.getTargetsByType(type);
        return ResponseEntity.ok(SalesTargetResponse.fromList(targets));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SalesTargetResponse>> getTargetsByUser(@PathVariable String userId) {
        log.debug("Fetching sales targets for user: {}", userId);
        List<SalesTarget> targets = salesTargetService.getTargetsByUser(userId);
        return ResponseEntity.ok(SalesTargetResponse.fromList(targets));
    }

    @GetMapping("/user/{userId}/year/{year}")
    public ResponseEntity<List<SalesTargetResponse>> getTargetsByUserAndYear(
            @PathVariable String userId,
            @PathVariable Integer year) {
        log.debug("Fetching sales targets for user: {} and year: {}", userId, year);
        List<SalesTarget> targets = salesTargetService.getTargetsByUserAndYear(userId, year);
        return ResponseEntity.ok(SalesTargetResponse.fromList(targets));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<SalesTargetResponse>> getTargetsByBranch(@PathVariable String branchId) {
        log.debug("Fetching sales targets for branch: {}", branchId);
        List<SalesTarget> targets = salesTargetService.getTargetsByBranch(branchId);
        return ResponseEntity.ok(SalesTargetResponse.fromList(targets));
    }

    @GetMapping("/branch/{branchId}/year/{year}")
    public ResponseEntity<List<SalesTargetResponse>> getTargetsByBranchAndYear(
            @PathVariable String branchId,
            @PathVariable Integer year) {
        log.debug("Fetching sales targets for branch: {} and year: {}", branchId, year);
        List<SalesTarget> targets = salesTargetService.getTargetsByBranchAndYear(branchId, year);
        return ResponseEntity.ok(SalesTargetResponse.fromList(targets));
    }

    @GetMapping("/company/year/{year}")
    public ResponseEntity<List<SalesTargetResponse>> getCompanyTargetsByYear(@PathVariable Integer year) {
        log.debug("Fetching company sales targets for year: {}", year);
        List<SalesTarget> targets = salesTargetService.getCompanyTargetsByYear(year);
        return ResponseEntity.ok(SalesTargetResponse.fromList(targets));
    }

    @DeleteMapping("/{targetId}")
    public ResponseEntity<Void> deleteTarget(@PathVariable String targetId) {
        log.info("Deleting sales target: {}", targetId);
        salesTargetService.deleteTarget(targetId);
        return ResponseEntity.noContent().build();
    }
}
