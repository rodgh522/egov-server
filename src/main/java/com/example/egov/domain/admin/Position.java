package com.example.egov.domain.admin;

import com.example.egov.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "POSITIONS")
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter
@Setter
@NoArgsConstructor
public class Position extends BaseEntity {

    @Id
    @Column(name = "POSITION_ID", length = 20)
    private String positionId;

    @Column(name = "POSITION_NAME", length = 100, nullable = false)
    private String positionName;

    @Column(name = "POSITION_CODE", length = 20, nullable = false)
    private String positionCode;

    @Column(name = "POSITION_LEVEL")
    private Integer positionLevel;

    @Column(name = "POSITION_DESCRIPTION", length = 255)
    private String positionDescription;

    @Column(name = "USE_AT", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String useAt = "Y";
}
