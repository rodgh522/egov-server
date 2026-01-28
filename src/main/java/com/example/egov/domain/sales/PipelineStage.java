package com.example.egov.domain.sales;

import com.example.egov.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "PIPELINE_STAGES")
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter
@Setter
@NoArgsConstructor
public class PipelineStage extends BaseEntity {

    @Id
    @Column(name = "STAGE_ID", length = 20)
    private String stageId;

    @Column(name = "STAGE_NAME", length = 100, nullable = false)
    private String stageName;

    @Column(name = "STAGE_CODE", length = 50, nullable = false)
    private String stageCode;

    @Column(name = "STAGE_ORDER")
    private Integer stageOrder = 0;

    @Column(name = "PROBABILITY")
    private Integer probability = 0;

    @Column(name = "STAGE_COLOR", length = 7)
    private String stageColor;

    @Column(name = "IS_WON", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String isWon = "N";

    @Column(name = "IS_LOST", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String isLost = "N";

    @Column(name = "USE_AT", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String useAt = "Y";
}
