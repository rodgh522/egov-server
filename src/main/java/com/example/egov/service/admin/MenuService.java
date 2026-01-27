package com.example.egov.service.admin;

import com.example.egov.config.multitenancy.TenantContext;
import com.example.egov.domain.admin.Menu;
import com.example.egov.domain.admin.MenuRepository;
import com.example.egov.web.admin.dto.MenuCreateRequest;
import com.example.egov.web.admin.dto.MenuUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing Menu entities.
 *
 * This service handles business logic for menu operations including CRUD operations.
 * Menu creation/updates automatically trigger permission generation via MenuPermissionGenerator.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final PermissionService permissionService;

    /**
     * Create a new menu.
     *
     * The menu will be automatically associated with the current tenant from TenantContext.
     * Permissions will be auto-generated via PermissionService.
     *
     * @param request Menu creation request
     * @return Created Menu entity
     */
    @Transactional
    public Menu createMenu(MenuCreateRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Cannot create menu without tenant context");
        }

        log.info("Creating menu: {} for tenant: {}", request.getMenuCode(), tenantId);

        Menu menu = new Menu();
        menu.setMenuCode(request.getMenuCode());
        menu.setMenuName(request.getMenuName());
        menu.setMenuType(request.getMenuType());
        menu.setMenuPath(request.getMenuPath());
        menu.setApiEndpoint(request.getApiEndpoint());
        menu.setIconName(request.getIconName());
        menu.setUpperMenuNo(request.getUpperMenuNo());
        menu.setMenuOrder(request.getMenuOrder());
        menu.setMenuDescription(request.getMenuDescription());
        menu.setIsVisible(request.getIsVisible());
        menu.setIsActive(request.getIsActive());
        menu.setTenantId(tenantId);

        Menu savedMenu = menuRepository.save(menu);
        log.info("Menu created successfully: {} (ID: {})", savedMenu.getMenuCode(), savedMenu.getMenuNo());

        // Auto-generate permissions for the new menu
        permissionService.autoGeneratePermissionsForMenu(savedMenu);

        return savedMenu;
    }

    /**
     * Update an existing menu.
     *
     * Only provided fields will be updated (partial update support).
     * Permission updates will be triggered via PermissionService.
     *
     * @param menuNo Menu ID to update
     * @param request Update request with new values
     * @return Updated Menu entity
     */
    @Transactional
    public Menu updateMenu(Long menuNo, MenuUpdateRequest request) {
        Menu menu = menuRepository.findById(menuNo)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found: " + menuNo));

        log.info("Updating menu: {} (ID: {})", menu.getMenuCode(), menuNo);

        // Update only provided fields
        if (request.getMenuName() != null) {
            menu.setMenuName(request.getMenuName());
        }
        if (request.getMenuType() != null) {
            menu.setMenuType(request.getMenuType());
        }
        if (request.getMenuPath() != null) {
            menu.setMenuPath(request.getMenuPath());
        }
        if (request.getApiEndpoint() != null) {
            menu.setApiEndpoint(request.getApiEndpoint());
        }
        if (request.getIconName() != null) {
            menu.setIconName(request.getIconName());
        }
        if (request.getUpperMenuNo() != null) {
            menu.setUpperMenuNo(request.getUpperMenuNo());
        }
        if (request.getMenuOrder() != null) {
            menu.setMenuOrder(request.getMenuOrder());
        }
        if (request.getMenuDescription() != null) {
            menu.setMenuDescription(request.getMenuDescription());
        }
        if (request.getIsVisible() != null) {
            menu.setIsVisible(request.getIsVisible());
        }
        if (request.getIsActive() != null) {
            menu.setIsActive(request.getIsActive());
        }

        Menu updatedMenu = menuRepository.save(menu);
        log.info("Menu updated successfully: {}", updatedMenu.getMenuCode());

        // Regenerate permissions if menu paths/endpoints changed
        permissionService.autoGeneratePermissionsForMenu(updatedMenu);

        return updatedMenu;
    }

    /**
     * Get a menu by ID.
     *
     * @param menuNo Menu ID
     * @return Menu entity if found
     */
    public Optional<Menu> getMenu(Long menuNo) {
        return menuRepository.findById(menuNo);
    }

    /**
     * Get a menu by code.
     *
     * @param menuCode Menu code
     * @return Menu entity if found
     */
    public Optional<Menu> getMenuByCode(String menuCode) {
        return menuRepository.findByMenuCode(menuCode);
    }

    /**
     * Get all menus for the current tenant.
     *
     * Results are automatically filtered by tenant via Hibernate filter.
     *
     * @return List of Menu entities
     */
    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    /**
     * Get all visible and active menus for the current tenant, ordered by menuOrder.
     *
     * @return List of Menu entities
     */
    public List<Menu> getVisibleMenus() {
        return menuRepository.findByIsVisibleAndIsActiveOrderByMenuOrderAsc("Y", "Y");
    }

    /**
     * Delete a menu.
     *
     * Associated permissions will be cascade deleted if FK constraints are configured.
     * Deletion will fail if the menu has child menus referencing it.
     *
     * @param menuNo Menu ID to delete
     * @throws IllegalStateException if menu has child menus
     */
    @Transactional
    public void deleteMenu(Long menuNo) {
        Menu menu = menuRepository.findById(menuNo)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found: " + menuNo));

        List<Menu> children = menuRepository.findByUpperMenuNo(menuNo);
        if (!children.isEmpty()) {
            String childCodes = children.stream()
                    .map(Menu::getMenuCode)
                    .collect(java.util.stream.Collectors.joining(", "));
            throw new IllegalStateException(
                    "Cannot delete menu '" + menu.getMenuCode() + "' with child menus. Delete these first: " + childCodes);
        }

        log.info("Deleting menu: {} (ID: {})", menu.getMenuCode(), menuNo);
        menuRepository.delete(menu);
        log.info("Menu deleted successfully: {}", menu.getMenuCode());
    }

    /**
     * Check if a menu code exists for the current tenant.
     *
     * @param menuCode Menu code to check
     * @return true if exists, false otherwise
     */
    public boolean existsByMenuCode(String menuCode) {
        return menuRepository.existsByMenuCode(menuCode);
    }

    /**
     * Get child menus of a parent menu.
     *
     * @param parentMenuNo Parent menu ID
     * @return List of child Menu entities
     */
    public List<Menu> getChildMenus(Long parentMenuNo) {
        return menuRepository.findByUpperMenuNo(parentMenuNo);
    }
}
