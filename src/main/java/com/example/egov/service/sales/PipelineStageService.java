package com.example.egov.service.sales;

import com.example.egov.config.multitenancy.TenantContext;
import com.example.egov.domain.sales.PipelineStage;
import com.example.egov.domain.sales.PipelineStageRepository;
import com.example.egov.web.sales.dto.PipelineStageCreateRequest;
import com.example.egov.web.sales.dto.PipelineStageUpdateRequest;
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
public class PipelineStageService {

    private static final String SYSTEM_TENANT_ID = "SYSTEM";

    private final PipelineStageRepository pipelineStageRepository;

    private boolean isSystemTenant() {
        return SYSTEM_TENANT_ID.equals(TenantContext.getCurrentTenantId());
    }

    private void verifyStageAccess(PipelineStage stage) {
        if (!isSystemTenant() && !stage.getTenantId().equals(TenantContext.getCurrentTenantId())) {
            throw new AccessDeniedException("Access denied to pipeline stage: " + stage.getStageId());
        }
    }

    @Transactional
    public PipelineStage createStage(PipelineStageCreateRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();

        if (pipelineStageRepository.existsByStageCode(request.getStageCode())) {
            throw new IllegalArgumentException("Stage code already exists: " + request.getStageCode());
        }

        String stageId = generateStageId();
        log.info("Creating pipeline stage: {} for tenant: {}", stageId, tenantId);

        PipelineStage stage = new PipelineStage();
        stage.setStageId(stageId);
        stage.setStageName(request.getStageName());
        stage.setStageCode(request.getStageCode());
        stage.setStageOrder(request.getStageOrder() != null ? request.getStageOrder() : 0);
        stage.setProbability(request.getProbability() != null ? request.getProbability() : 0);
        stage.setStageColor(request.getStageColor());
        stage.setIsWon(request.getIsWon() != null ? request.getIsWon() : "N");
        stage.setIsLost(request.getIsLost() != null ? request.getIsLost() : "N");
        stage.setUseAt(request.getUseAt() != null ? request.getUseAt() : "Y");
        stage.setTenantId(tenantId);

        PipelineStage savedStage = pipelineStageRepository.save(stage);
        log.info("Pipeline stage created successfully: {}", savedStage.getStageId());

        return savedStage;
    }

    @Transactional
    public PipelineStage updateStage(String stageId, PipelineStageUpdateRequest request) {
        PipelineStage stage = pipelineStageRepository.findById(stageId)
                .orElseThrow(() -> new IllegalArgumentException("Pipeline stage not found: " + stageId));

        verifyStageAccess(stage);
        log.info("Updating pipeline stage: {}", stageId);

        if (request.getStageName() != null) {
            stage.setStageName(request.getStageName());
        }
        if (request.getStageCode() != null && !request.getStageCode().equals(stage.getStageCode())) {
            if (pipelineStageRepository.existsByStageCode(request.getStageCode())) {
                throw new IllegalArgumentException("Stage code already exists: " + request.getStageCode());
            }
            stage.setStageCode(request.getStageCode());
        }
        if (request.getStageOrder() != null) {
            stage.setStageOrder(request.getStageOrder());
        }
        if (request.getProbability() != null) {
            stage.setProbability(request.getProbability());
        }
        if (request.getStageColor() != null) {
            stage.setStageColor(request.getStageColor());
        }
        if (request.getIsWon() != null) {
            stage.setIsWon(request.getIsWon());
        }
        if (request.getIsLost() != null) {
            stage.setIsLost(request.getIsLost());
        }
        if (request.getUseAt() != null) {
            stage.setUseAt(request.getUseAt());
        }

        PipelineStage updatedStage = pipelineStageRepository.save(stage);
        log.info("Pipeline stage updated successfully: {}", updatedStage.getStageId());

        return updatedStage;
    }

    public Optional<PipelineStage> getStage(String stageId) {
        Optional<PipelineStage> stage = pipelineStageRepository.findById(stageId);
        stage.ifPresent(this::verifyStageAccess);
        return stage;
    }

    public List<PipelineStage> getAllStages() {
        return pipelineStageRepository.findAll();
    }

    public List<PipelineStage> getActiveStagesOrdered() {
        return pipelineStageRepository.findAllActiveOrderByStageOrder();
    }

    public Optional<PipelineStage> getWonStage() {
        return pipelineStageRepository.findWonStage();
    }

    public Optional<PipelineStage> getLostStage() {
        return pipelineStageRepository.findLostStage();
    }

    @Transactional
    public void deleteStage(String stageId) {
        PipelineStage stage = pipelineStageRepository.findById(stageId)
                .orElseThrow(() -> new IllegalArgumentException("Pipeline stage not found: " + stageId));

        verifyStageAccess(stage);

        log.info("Deleting pipeline stage: {}", stageId);
        pipelineStageRepository.delete(stage);
        log.info("Pipeline stage deleted successfully: {}", stageId);
    }

    public boolean existsByStageCode(String stageCode) {
        return pipelineStageRepository.existsByStageCode(stageCode);
    }

    private String generateStageId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "STG" + timestamp.substring(timestamp.length() - 10) + suffix;
    }
}
