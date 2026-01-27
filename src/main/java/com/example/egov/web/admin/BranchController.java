package com.example.egov.web.admin;

import com.example.egov.domain.admin.Branch;
import com.example.egov.service.admin.BranchService;
import com.example.egov.web.admin.dto.BranchCreateRequest;
import com.example.egov.web.admin.dto.BranchResponse;
import com.example.egov.web.admin.dto.BranchUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Branch management.
 *
 * Access control:
 * - SYSTEM tenant can access all branches across tenants
 * - Other tenants can only access their own branches
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    public ResponseEntity<BranchResponse> createBranch(@Valid @RequestBody BranchCreateRequest request) {
        log.info("Creating branch: {}", request.getBranchName());

        Branch branch = branchService.createBranch(request);
        BranchResponse response = BranchResponse.from(branch);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{branchId}")
    public ResponseEntity<BranchResponse> updateBranch(
            @PathVariable String branchId,
            @Valid @RequestBody BranchUpdateRequest request) {
        log.info("Updating branch: {}", branchId);

        Branch branch = branchService.updateBranch(branchId, request);
        BranchResponse response = BranchResponse.from(branch);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{branchId}")
    public ResponseEntity<BranchResponse> getBranch(@PathVariable String branchId) {
        log.debug("Fetching branch: {}", branchId);

        return branchService.getBranch(branchId)
                .map(branch -> ResponseEntity.ok(BranchResponse.from(branch)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<BranchResponse>> getAllBranches() {
        log.debug("Fetching all branches");

        List<Branch> branches = branchService.getAllBranches();
        List<BranchResponse> responses = BranchResponse.fromList(branches);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    public ResponseEntity<List<BranchResponse>> getActiveBranches() {
        log.debug("Fetching active branches");

        List<Branch> branches = branchService.getActiveBranches();
        List<BranchResponse> responses = BranchResponse.fromList(branches);

        return ResponseEntity.ok(responses);
    }

    /**
     * Get branches by tenant ID (SYSTEM tenant only).
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<BranchResponse>> getBranchesByTenant(@PathVariable String tenantId) {
        log.debug("Fetching branches for tenant: {}", tenantId);

        List<Branch> branches = branchService.getBranchesByTenant(tenantId);
        List<BranchResponse> responses = BranchResponse.fromList(branches);

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{branchId}")
    public ResponseEntity<Void> deleteBranch(@PathVariable String branchId) {
        log.info("Deleting branch: {}", branchId);

        branchService.deleteBranch(branchId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{branchId}")
    public ResponseEntity<Boolean> existsByBranchId(@PathVariable String branchId) {
        log.debug("Checking if branch exists: {}", branchId);

        boolean exists = branchService.existsByBranchId(branchId);

        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/code/{branchCode}")
    public ResponseEntity<Boolean> existsByBranchCode(@PathVariable String branchCode) {
        log.debug("Checking if branch code exists: {}", branchCode);

        boolean exists = branchService.existsByBranchCode(branchCode);

        return ResponseEntity.ok(exists);
    }
}
