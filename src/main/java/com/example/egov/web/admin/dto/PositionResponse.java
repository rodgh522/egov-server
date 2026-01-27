package com.example.egov.web.admin.dto;

import com.example.egov.domain.admin.Position;
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
public class PositionResponse {

    private String positionId;
    private String positionName;
    private String positionCode;
    private Integer positionLevel;
    private String positionDescription;
    private String tenantId;
    private String useAt;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static PositionResponse from(Position position) {
        if (position == null) {
            return null;
        }

        return PositionResponse.builder()
                .positionId(position.getPositionId())
                .positionName(position.getPositionName())
                .positionCode(position.getPositionCode())
                .positionLevel(position.getPositionLevel())
                .positionDescription(position.getPositionDescription())
                .tenantId(position.getTenantId())
                .useAt(position.getUseAt())
                .createdDate(position.getCreatedDate())
                .updatedDate(position.getUpdatedDate())
                .build();
    }

    public static List<PositionResponse> fromList(List<Position> positions) {
        if (positions == null) {
            return null;
        }
        return positions.stream()
                .map(PositionResponse::from)
                .collect(Collectors.toList());
    }
}
