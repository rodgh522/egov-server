package com.example.egov.service.admin;

import com.example.egov.config.multitenancy.TenantContext;
import com.example.egov.domain.admin.Branch;
import com.example.egov.domain.admin.BranchRepository;
import com.example.egov.domain.admin.TenantRepository;
import com.example.egov.web.admin.dto.BranchCreateRequest;
import com.example.egov.web.admin.dto.BranchUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BranchService {

    private static final String SYSTEM_TENANT_ID = "SYSTEM";

    private final BranchRepository branchRepository;
    private final TenantRepository tenantRepository;

    private boolean isSystemTenant() {
        return SYSTEM_TENANT_ID.equals(TenantContext.getCurrentTenantId());
    }

    private void verifyBranchAccess(Branch branch) {
        if (!isSystemTenant() && !branch.getTenantId().equals(TenantContext.getCurrentTenantId())) {
            throw new AccessDeniedException("Access denied to branch: " + branch.getBranchId());
        }
    }

    @Transactional
    public Branch createBranch(BranchCreateRequest request) {
        String currentTenantId = TenantContext.getCurrentTenantId();

        if (branchRepository.existsByBranchCode(request.getBranchCode())) {
            throw new IllegalArgumentException("Branch code already exists: " + request.getBranchCode());
        }

        // Determine tenant ID: SYSTEM can specify, others use current tenant
        String tenantId;
        if (isSystemTenant() && request.getTenantId() != null) {
            if (!tenantRepository.existsByTenantId(request.getTenantId())) {
                throw new IllegalArgumentException("Tenant not found: " + request.getTenantId());
            }
            tenantId = request.getTenantId();
        } else {
            tenantId = currentTenantId;
        }

        String branchId = generateBranchId();
        log.info("Creating branch: {} for tenant: {}", branchId, tenantId);

        Branch branch = new Branch();
        branch.setBranchId(branchId);
        branch.setBranchName(request.getBranchName());
        branch.setBranchCode(request.getBranchCode());
        branch.setBranchAddress(request.getBranchAddress());
        branch.setBranchPhone(request.getBranchPhone());
        branch.setUseAt(request.getUseAt());
        branch.setTenantId(tenantId);

        // Set parent branch if specified
        if (request.getParentBranchId() != null) {
            Branch parentBranch = branchRepository.findById(request.getParentBranchId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent branch not found: " + request.getParentBranchId()));
            // Verify parent branch belongs to same tenant
            if (!parentBranch.getTenantId().equals(tenantId)) {
                throw new IllegalArgumentException("Parent branch must belong to the same tenant");
            }
            branch.setParentBranch(parentBranch);
        }

        Branch savedBranch = branchRepository.save(branch);
        log.info("Branch created successfully: {}", savedBranch.getBranchId());

        return savedBranch;
    }

    @Transactional
    public Branch updateBranch(String branchId, BranchUpdateRequest request) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + branchId));

        verifyBranchAccess(branch);

        log.info("Updating branch: {}", branchId);

        if (request.getBranchName() != null) {
            branch.setBranchName(request.getBranchName());
        }
        if (request.getBranchCode() != null) {
            // Check for duplicate code
            Optional<Branch> existingBranch = branchRepository.findByBranchCode(request.getBranchCode());
            if (existingBranch.isPresent() && !existingBranch.get().getBranchId().equals(branchId)) {
                throw new IllegalArgumentException("Branch code already exists: " + request.getBranchCode());
            }
            branch.setBranchCode(request.getBranchCode());
        }
        if (request.getBranchAddress() != null) {
            branch.setBranchAddress(request.getBranchAddress());
        }
        if (request.getBranchPhone() != null) {
            branch.setBranchPhone(request.getBranchPhone());
        }
        if (request.getUseAt() != null) {
            branch.setUseAt(request.getUseAt());
        }
        if (request.getParentBranchId() != null) {
            if (request.getParentBranchId().equals(branchId)) {
                throw new IllegalArgumentException("Branch cannot be its own parent");
            }
            Branch parentBranch = branchRepository.findById(request.getParentBranchId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent branch not found: " + request.getParentBranchId()));
            if (!parentBranch.getTenantId().equals(branch.getTenantId())) {
                throw new IllegalArgumentException("Parent branch must belong to the same tenant");
            }
            branch.setParentBranch(parentBranch);
        }

        Branch updatedBranch = branchRepository.save(branch);
        log.info("Branch updated successfully: {}", updatedBranch.getBranchId());

        return updatedBranch;
    }

    public Optional<Branch> getBranch(String branchId) {
        Optional<Branch> branch = branchRepository.findById(branchId);
        branch.ifPresent(this::verifyBranchAccess);
        return branch;
    }

    public List<Branch> getAllBranches() {
        // TenantFilterAspect handles filtering:
        // - SYSTEM tenant sees all branches
        // - Other tenants see only their own branches
        return branchRepository.findAll();
    }

    public List<Branch> getActiveBranches() {
        return branchRepository.findByUseAt("Y");
    }

    public List<Branch> getBranchesByTenant(String tenantId) {
        if (!isSystemTenant()) {
            throw new AccessDeniedException("Only SYSTEM tenant can query by tenant ID");
        }
        return branchRepository.findByTenantId(tenantId);
    }

    @Transactional
    public void deleteBranch(String branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + branchId));

        verifyBranchAccess(branch);

        // Check for child branches
        List<Branch> children = branchRepository.findByParentBranch(branch);
        if (!children.isEmpty()) {
            throw new IllegalStateException("Cannot delete branch with child branches. Delete children first.");
        }

        log.info("Deleting branch: {}", branchId);
        branchRepository.delete(branch);
        log.info("Branch deleted successfully: {}", branchId);
    }

    public boolean existsByBranchId(String branchId) {
        return branchRepository.existsByBranchId(branchId);
    }

    public boolean existsByBranchCode(String branchCode) {
        return branchRepository.existsByBranchCode(branchCode);
    }

    private String generateBranchId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "BRN" + timestamp.substring(timestamp.length() - 10) + suffix;
    }
}
