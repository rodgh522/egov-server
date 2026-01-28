package com.example.egov.web.sales.dto;

import com.example.egov.domain.sales.PipelineStage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PipelineStageResponse {

    private String stageId;
    private String stageName;
    private String stageCode;
    private Integer stageOrder;
    private Integer probability;
    private String stageColor;
    private String isWon;
    private String isLost;
    private String tenantId;
    private String useAt;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static PipelineStageResponse from(PipelineStage stage) {
        if (stage == null) {
            return null;
        }

        return PipelineStageResponse.builder()
                .stageId(stage.getStageId())
                .stageName(stage.getStageName())
                .stageCode(stage.getStageCode())
                .stageOrder(stage.getStageOrder())
                .probability(stage.getProbability())
                .stageColor(stage.getStageColor())
                .isWon(stage.getIsWon())
                .isLost(stage.getIsLost())
                .tenantId(stage.getTenantId())
                .useAt(stage.getUseAt())
                .createdDate(stage.getCreatedDate())
                .updatedDate(stage.getUpdatedDate())
                .build();
    }

    public static List<PipelineStageResponse> fromList(List<PipelineStage> stages) {
        if (stages == null) {
            return null;
        }
        return stages.stream()
                .map(PipelineStageResponse::from)
                .collect(Collectors.toList());
    }
}
