package com.example.egov.domain.admin;

import com.example.egov.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Menu entity - represents a menu item in the system.
 *
 * Menus are hierarchical and can auto-generate permissions.
 * Tenant filtering is applied via Hibernate filter.
 */
@Entity
@Table(name = "MENUS")

@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter
@Setter
@NoArgsConstructor
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENU_NO")
    private Long menuNo;

    @Column(name = "MENU_CODE", length = 50, unique = true, nullable = false)
    private String menuCode;

    @Column(name = "MENU_NAME", length = 100, nullable = false)
    private String menuName;

    @Column(name = "MENU_TYPE", length = 20)
    private String menuType; // MENU, FOLDER, LINK

    @Column(name = "MENU_PATH", length = 255)
    private String menuPath;

    @Column(name = "API_ENDPOINT", length = 255)
    private String apiEndpoint;

    @Column(name = "ICON_NAME", length = 50)
    private String iconName;

    @Column(name = "UPPER_MENU_NO")
    private Long upperMenuNo;

    @Column(name = "MENU_ORDER")
    private Integer menuOrder;

    @Column(name = "MENU_DESCRIPTION", length = 255)
    private String menuDescription;

    @Column(name = "IS_VISIBLE", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String isVisible;

    @Column(name = "IS_ACTIVE", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String isActive;

    @Column(name = "CREATED_BY", length = 20)
    private String createdBy;

    @PrePersist
    public void prePersist() {
        if (this.menuType == null) {
            this.menuType = "MENU";
        }
        if (this.isVisible == null) {
            this.isVisible = "Y";
        }
        if (this.isActive == null) {
            this.isActive = "Y";
        }
        if (this.menuOrder == null) {
            this.menuOrder = 0;
        }
    }
}
