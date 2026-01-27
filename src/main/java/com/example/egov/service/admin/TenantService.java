package com.example.egov.service.admin;

import com.example.egov.config.multitenancy.TenantContext;
import com.example.egov.domain.admin.Tenant;
import com.example.egov.domain.admin.TenantRepository;
import com.example.egov.web.admin.dto.TenantCreateRequest;
import com.example.egov.web.admin.dto.TenantUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TenantService {

    private static final String SYSTEM_TENANT_ID = "SYSTEM";

    private final TenantRepository tenantRepository;

    /**
     * Verify that the current user is from the SYSTEM tenant.
     * Only SYSTEM tenant can manage tenants.
     */
    private void verifySystemTenantAccess() {
        String currentTenantId = TenantContext.getCurrentTenantId();
        if (!SYSTEM_TENANT_ID.equals(currentTenantId)) {
            log.warn("Access denied: Tenant {} attempted to access tenant management", currentTenantId);
            throw new AccessDeniedException("Only SYSTEM tenant can manage tenants");
        }
    }

    @Transactional
    public Tenant createTenant(TenantCreateRequest request) {
        verifySystemTenantAccess();

        if (tenantRepository.existsByTenantId(request.getTenantId())) {
            throw new IllegalArgumentException("Tenant ID already exists: " + request.getTenantId());
        }

        log.info("Creating tenant: {}", request.getTenantId());

        Tenant tenant = new Tenant();
        tenant.setTenantId(request.getTenantId());
        tenant.setTenantName(request.getTenantName());
        tenant.setTenantDescription(request.getTenantDescription());
        tenant.setUseAt(request.getUseAt());

        Tenant savedTenant = tenantRepository.save(tenant);
        log.info("Tenant created successfully: {}", savedTenant.getTenantId());

        return savedTenant;
    }

    @Transactional
    public Tenant updateTenant(String tenantId, TenantUpdateRequest request) {
        verifySystemTenantAccess();

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));

        log.info("Updating tenant: {}", tenantId);

        if (request.getTenantName() != null) {
            tenant.setTenantName(request.getTenantName());
        }
        if (request.getTenantDescription() != null) {
            tenant.setTenantDescription(request.getTenantDescription());
        }
        if (request.getUseAt() != null) {
            tenant.setUseAt(request.getUseAt());
        }

        Tenant updatedTenant = tenantRepository.save(tenant);
        log.info("Tenant updated successfully: {}", updatedTenant.getTenantId());

        return updatedTenant;
    }

    public Optional<Tenant> getTenant(String tenantId) {
        verifySystemTenantAccess();
        return tenantRepository.findById(tenantId);
    }

    public List<Tenant> getAllTenants() {
        verifySystemTenantAccess();
        return tenantRepository.findAll();
    }

    public List<Tenant> getActiveTenants() {
        verifySystemTenantAccess();
        return tenantRepository.findByUseAt("Y");
    }

    @Transactional
    public void deleteTenant(String tenantId) {
        verifySystemTenantAccess();

        if (SYSTEM_TENANT_ID.equals(tenantId)) {
            throw new IllegalArgumentException("Cannot delete SYSTEM tenant");
        }

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));

        log.info("Deleting tenant: {}", tenantId);
        tenantRepository.delete(tenant);
        log.info("Tenant deleted successfully: {}", tenantId);
    }

    public boolean existsByTenantId(String tenantId) {
        verifySystemTenantAccess();
        return tenantRepository.existsByTenantId(tenantId);
    }
}
