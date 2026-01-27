package com.example.egov.service.admin;

import com.example.egov.config.multitenancy.TenantContext;
import com.example.egov.domain.admin.Position;
import com.example.egov.domain.admin.PositionRepository;
import com.example.egov.domain.admin.TenantRepository;
import com.example.egov.web.admin.dto.PositionCreateRequest;
import com.example.egov.web.admin.dto.PositionUpdateRequest;
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
public class PositionService {

    private static final String SYSTEM_TENANT_ID = "SYSTEM";

    private final PositionRepository positionRepository;
    private final TenantRepository tenantRepository;

    private boolean isSystemTenant() {
        return SYSTEM_TENANT_ID.equals(TenantContext.getCurrentTenantId());
    }

    private void verifyPositionAccess(Position position) {
        if (!isSystemTenant() && !position.getTenantId().equals(TenantContext.getCurrentTenantId())) {
            throw new AccessDeniedException("Access denied to position: " + position.getPositionId());
        }
    }

    @Transactional
    public Position createPosition(PositionCreateRequest request) {
        String currentTenantId = TenantContext.getCurrentTenantId();

        if (positionRepository.existsByPositionId(request.getPositionId())) {
            throw new IllegalArgumentException("Position ID already exists: " + request.getPositionId());
        }

        if (positionRepository.existsByPositionCode(request.getPositionCode())) {
            throw new IllegalArgumentException("Position code already exists: " + request.getPositionCode());
        }

        String tenantId;
        if (isSystemTenant() && request.getTenantId() != null) {
            if (!tenantRepository.existsByTenantId(request.getTenantId())) {
                throw new IllegalArgumentException("Tenant not found: " + request.getTenantId());
            }
            tenantId = request.getTenantId();
        } else {
            tenantId = currentTenantId;
        }

        log.info("Creating position: {} for tenant: {}", request.getPositionId(), tenantId);

        Position position = new Position();
        position.setPositionId(request.getPositionId());
        position.setPositionName(request.getPositionName());
        position.setPositionCode(request.getPositionCode());
        position.setPositionLevel(request.getPositionLevel());
        position.setPositionDescription(request.getPositionDescription());
        position.setUseAt(request.getUseAt());
        position.setTenantId(tenantId);

        Position savedPosition = positionRepository.save(position);
        log.info("Position created successfully: {}", savedPosition.getPositionId());

        return savedPosition;
    }

    @Transactional
    public Position updatePosition(String positionId, PositionUpdateRequest request) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new IllegalArgumentException("Position not found: " + positionId));

        verifyPositionAccess(position);

        log.info("Updating position: {}", positionId);

        if (request.getPositionName() != null) {
            position.setPositionName(request.getPositionName());
        }
        if (request.getPositionCode() != null) {
            Optional<Position> existingPosition = positionRepository.findByPositionCode(request.getPositionCode());
            if (existingPosition.isPresent() && !existingPosition.get().getPositionId().equals(positionId)) {
                throw new IllegalArgumentException("Position code already exists: " + request.getPositionCode());
            }
            position.setPositionCode(request.getPositionCode());
        }
        if (request.getPositionLevel() != null) {
            position.setPositionLevel(request.getPositionLevel());
        }
        if (request.getPositionDescription() != null) {
            position.setPositionDescription(request.getPositionDescription());
        }
        if (request.getUseAt() != null) {
            position.setUseAt(request.getUseAt());
        }

        Position updatedPosition = positionRepository.save(position);
        log.info("Position updated successfully: {}", updatedPosition.getPositionId());

        return updatedPosition;
    }

    public Optional<Position> getPosition(String positionId) {
        Optional<Position> position = positionRepository.findById(positionId);
        position.ifPresent(this::verifyPositionAccess);
        return position;
    }

    public List<Position> getAllPositions() {
        return positionRepository.findAllByOrderByPositionLevelAsc();
    }

    public List<Position> getActivePositions() {
        return positionRepository.findByUseAt("Y");
    }

    public List<Position> getPositionsByTenant(String tenantId) {
        if (!isSystemTenant()) {
            throw new AccessDeniedException("Only SYSTEM tenant can query by tenant ID");
        }
        return positionRepository.findByTenantId(tenantId);
    }

    @Transactional
    public void deletePosition(String positionId) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new IllegalArgumentException("Position not found: " + positionId));

        verifyPositionAccess(position);

        log.info("Deleting position: {}", positionId);
        positionRepository.delete(position);
        log.info("Position deleted successfully: {}", positionId);
    }

    public boolean existsByPositionId(String positionId) {
        return positionRepository.existsByPositionId(positionId);
    }

    public boolean existsByPositionCode(String positionCode) {
        return positionRepository.existsByPositionCode(positionCode);
    }
}
