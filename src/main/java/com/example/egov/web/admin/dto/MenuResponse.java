package com.example.egov.web.admin.dto;

import com.example.egov.domain.admin.Menu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for Menu responses.
 *
 * This DTO is used to return menu data to clients, hiding internal implementation details.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {

    private Long menuNo;
    private String menuCode;
    private String menuName;
    private String menuType;
    private String menuPath;
    private String apiEndpoint;
    private String iconName;
    private Long upperMenuNo;
    private Integer menuOrder;
    private String menuDescription;
    private String isVisible;
    private String isActive;
    private String tenantId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    // For hierarchical menu structure
    private List<MenuResponse> children;

    /**
     * Convert a Menu entity to a MenuResponse DTO.
     *
     * @param menu The Menu entity to convert
     * @return MenuResponse DTO
     */
    public static MenuResponse from(Menu menu) {
        if (menu == null) {
            return null;
        }

        return MenuResponse.builder()
                .menuNo(menu.getMenuNo())
                .menuCode(menu.getMenuCode())
                .menuName(menu.getMenuName())
                .menuType(menu.getMenuType())
                .menuPath(menu.getMenuPath())
                .apiEndpoint(menu.getApiEndpoint())
                .iconName(menu.getIconName())
                .upperMenuNo(menu.getUpperMenuNo())
                .menuOrder(menu.getMenuOrder())
                .menuDescription(menu.getMenuDescription())
                .isVisible(menu.getIsVisible())
                .isActive(menu.getIsActive())
                .tenantId(menu.getTenantId())
                .createdDate(menu.getCreatedDate())
                .updatedDate(menu.getUpdatedDate())
                .build();
    }

    /**
     * Convert a list of Menu entities to MenuResponse DTOs.
     *
     * @param menus List of Menu entities
     * @return List of MenuResponse DTOs
     */
    public static List<MenuResponse> fromList(List<Menu> menus) {
        if (menus == null) {
            return null;
        }
        return menus.stream()
                .map(MenuResponse::from)
                .collect(Collectors.toList());
    }
}
