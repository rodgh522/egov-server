package com.example.egov.web.admin;

import com.example.egov.domain.admin.Tenant;
import com.example.egov.service.admin.TenantService;
import com.example.egov.web.admin.dto.TenantCreateRequest;
import com.example.egov.web.admin.dto.TenantResponse;
import com.example.egov.web.admin.dto.TenantUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Tenant management.
 *
 * Provides endpoints for CRUD operations on tenants.
 * Only SYSTEM tenant can access these endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<TenantResponse> createTenant(@Valid @RequestBody TenantCreateRequest request) {
        log.info("Creating tenant: {}", request.getTenantId());

        Tenant tenant = tenantService.createTenant(request);
        TenantResponse response = TenantResponse.from(tenant);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{tenantId}")
    public ResponseEntity<TenantResponse> updateTenant(
            @PathVariable String tenantId,
            @Valid @RequestBody TenantUpdateRequest request) {
        log.info("Updating tenant: {}", tenantId);

        Tenant tenant = tenantService.updateTenant(tenantId, request);
        TenantResponse response = TenantResponse.from(tenant);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<TenantResponse> getOwnTenant() {
        log.debug("Fetching own tenant");

        return tenantService.getOwnTenant()
                .map(tenant -> ResponseEntity.ok(TenantResponse.from(tenant)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<TenantResponse> getTenant(@PathVariable String tenantId) {
        log.debug("Fetching tenant: {}", tenantId);

        return tenantService.getTenant(tenantId)
                .map(tenant -> ResponseEntity.ok(TenantResponse.from(tenant)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TenantResponse>> getAllTenants() {
        log.debug("Fetching all tenants");

        List<Tenant> tenants = tenantService.getAllTenants();
        List<TenantResponse> responses = TenantResponse.fromList(tenants);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    public ResponseEntity<List<TenantResponse>> getActiveTenants() {
        log.debug("Fetching active tenants");

        List<Tenant> tenants = tenantService.getActiveTenants();
        List<TenantResponse> responses = TenantResponse.fromList(tenants);

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{tenantId}")
    public ResponseEntity<Void> deleteTenant(@PathVariable String tenantId) {
        log.info("Deleting tenant: {}", tenantId);

        tenantService.deleteTenant(tenantId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{tenantId}")
    public ResponseEntity<Boolean> existsByTenantId(@PathVariable String tenantId) {
        log.debug("Checking if tenant exists: {}", tenantId);

        boolean exists = tenantService.existsByTenantId(tenantId);

        return ResponseEntity.ok(exists);
    }
}
