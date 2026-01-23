package com.example.egov.domain.admin;

import com.example.egov.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Entity
@Table(name = "PERMISSIONS")
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter
@Setter
@NoArgsConstructor
public class Permission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PERMISSION_ID")
    private Long permissionId;

    @Column(name = "PERMISSION_CODE", length = 100, nullable = false)
    private String permissionCode;

    @Column(name = "PERMISSION_TYPE", length = 20, nullable = false)
    private String permissionType; // API, MENU

    @Column(name = "PERMISSION_ACTION", length = 20, nullable = false)
    private String permissionAction; // READ, WRITE, DELETE, DOWNLOAD

    @Column(name = "RESOURCE_PATH", length = 255)
    private String resourcePath;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MENU_NO")
    private Menu menu;
}
