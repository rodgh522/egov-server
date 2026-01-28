package com.example.egov.service.sales;

import com.example.egov.config.multitenancy.TenantContext;
import com.example.egov.domain.admin.Branch;
import com.example.egov.domain.admin.BranchRepository;
import com.example.egov.domain.admin.User;
import com.example.egov.domain.admin.UserRepository;
import com.example.egov.domain.sales.SalesTarget;
import com.example.egov.domain.sales.SalesTargetRepository;
import com.example.egov.web.sales.dto.SalesTargetCreateRequest;
import com.example.egov.web.sales.dto.SalesTargetUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesTargetService {

    private static final String SYSTEM_TENANT_ID = "SYSTEM";

    private final SalesTargetRepository salesTargetRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    private boolean isSystemTenant() {
        return SYSTEM_TENANT_ID.equals(TenantContext.getCurrentTenantId());
    }

    private void verifyTargetAccess(SalesTarget target) {
        if (!isSystemTenant() && !target.getTenantId().equals(TenantContext.getCurrentTenantId())) {
            throw new AccessDeniedException("Access denied to sales target: " + target.getTargetId());
        }
    }

    @Transactional
    public SalesTarget createTarget(SalesTargetCreateRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();

        String targetId = generateTargetId();
        log.info("Creating sales target: {} for tenant: {}", targetId, tenantId);

        SalesTarget target = new SalesTarget();
        target.setTargetId(targetId);
        target.setTargetYear(request.getTargetYear());
        target.setTargetMonth(request.getTargetMonth());
        target.setTargetAmount(request.getTargetAmount());
        target.setAchievedAmount(request.getAchievedAmount() != null ? request.getAchievedAmount() : BigDecimal.ZERO);
        target.setTargetType(request.getTargetType() != null ? request.getTargetType() : "REVENUE");
        target.setTenantId(tenantId);

        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));
            target.setUser(user);
        }

        if (request.getBranchId() != null) {
            Branch branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + request.getBranchId()));
            target.setBranch(branch);
        }

        SalesTarget savedTarget = salesTargetRepository.save(target);
        log.info("Sales target created successfully: {}", savedTarget.getTargetId());

        return savedTarget;
    }

    @Transactional
    public SalesTarget updateTarget(String targetId, SalesTargetUpdateRequest request) {
        SalesTarget target = salesTargetRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("Sales target not found: " + targetId));

        verifyTargetAccess(target);
        log.info("Updating sales target: {}", targetId);

        if (request.getTargetYear() != null) {
            target.setTargetYear(request.getTargetYear());
        }
        if (request.getTargetMonth() != null) {
            target.setTargetMonth(request.getTargetMonth());
        }
        if (request.getTargetAmount() != null) {
            target.setTargetAmount(request.getTargetAmount());
        }
        if (request.getAchievedAmount() != null) {
            target.setAchievedAmount(request.getAchievedAmount());
        }
        if (request.getTargetType() != null) {
            target.setTargetType(request.getTargetType());
        }
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));
            target.setUser(user);
        }
        if (request.getBranchId() != null) {
            Branch branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + request.getBranchId()));
            target.setBranch(branch);
        }

        SalesTarget updatedTarget = salesTargetRepository.save(target);
        log.info("Sales target updated successfully: {}", updatedTarget.getTargetId());

        return updatedTarget;
    }

    @Transactional
    public SalesTarget updateAchievedAmount(String targetId, BigDecimal achievedAmount) {
        SalesTarget target = salesTargetRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("Sales target not found: " + targetId));

        verifyTargetAccess(target);

        target.setAchievedAmount(achievedAmount);

        SalesTarget updatedTarget = salesTargetRepository.save(target);
        log.info("Sales target achieved amount updated: {} -> {}", targetId, achievedAmount);

        return updatedTarget;
    }

    public Optional<SalesTarget> getTarget(String targetId) {
        Optional<SalesTarget> target = salesTargetRepository.findById(targetId);
        target.ifPresent(this::verifyTargetAccess);
        return target;
    }

    public List<SalesTarget> getAllTargets() {
        return salesTargetRepository.findAll();
    }

    public List<SalesTarget> getTargetsByYear(Integer year) {
        return salesTargetRepository.findByTargetYear(year);
    }

    public List<SalesTarget> getTargetsByYearAndMonth(Integer year, Integer month) {
        return salesTargetRepository.findByTargetYearAndTargetMonth(year, month);
    }

    public List<SalesTarget> getTargetsByType(String type) {
        return salesTargetRepository.findByTargetType(type);
    }

    public List<SalesTarget> getTargetsByUser(String userId) {
        return salesTargetRepository.findByUserId(userId);
    }

    public List<SalesTarget> getTargetsByBranch(String branchId) {
        return salesTargetRepository.findByBranchId(branchId);
    }

    public List<SalesTarget> getTargetsByUserAndYear(String userId, Integer year) {
        return salesTargetRepository.findByUserIdAndYear(userId, year);
    }

    public List<SalesTarget> getTargetsByBranchAndYear(String branchId, Integer year) {
        return salesTargetRepository.findByBranchIdAndYear(branchId, year);
    }

    public List<SalesTarget> getCompanyTargetsByYear(Integer year) {
        return salesTargetRepository.findCompanyTargetsByYear(year);
    }

    @Transactional
    public void deleteTarget(String targetId) {
        SalesTarget target = salesTargetRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("Sales target not found: " + targetId));

        verifyTargetAccess(target);

        log.info("Deleting sales target: {}", targetId);
        salesTargetRepository.delete(target);
        log.info("Sales target deleted successfully: {}", targetId);
    }

    private String generateTargetId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "TGT" + timestamp.substring(timestamp.length() - 10) + suffix;
    }
}
