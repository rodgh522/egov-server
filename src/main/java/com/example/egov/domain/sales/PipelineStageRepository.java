package com.example.egov.domain.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PipelineStageRepository extends JpaRepository<PipelineStage, String> {

    Optional<PipelineStage> findByStageId(String stageId);

    Optional<PipelineStage> findByStageCode(String stageCode);

    boolean existsByStageCode(String stageCode);

    List<PipelineStage> findByUseAt(String useAt);

    @Query("SELECT ps FROM PipelineStage ps WHERE ps.useAt = 'Y' ORDER BY ps.stageOrder ASC")
    List<PipelineStage> findAllActiveOrderByStageOrder();

    @Query("SELECT ps FROM PipelineStage ps WHERE ps.isWon = 'Y'")
    Optional<PipelineStage> findWonStage();

    @Query("SELECT ps FROM PipelineStage ps WHERE ps.isLost = 'Y'")
    Optional<PipelineStage> findLostStage();

    List<PipelineStage> findByTenantId(String tenantId);
}
