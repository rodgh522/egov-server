package com.example.egov.domain.admin.listener;

import com.example.egov.domain.admin.Menu;
import com.example.egov.domain.admin.Permission;
import com.example.egov.domain.admin.PermissionRepository;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity Listener for automatic permission generation from Menu entities.
 *
 * This listener automatically creates Permission records when a Menu is created or updated,
 * following the menu-driven permission generation strategy.
 *
 * Auto-Generation Rules:
 * 1. If menu has API_ENDPOINT → Create API:READ permission
 * 2. If menu has MENU_PATH → Create MENU:READ, MENU:WRITE, MENU:DELETE permissions
 *
 * Permission Naming Convention:
 * - API permissions: API:{menuCode}:READ
 * - MENU permissions: MENU:{menuCode}:{action}
 *
 * This implements the core feature described in project-vision.md:
 * "Menu-Driven Permission Auto-Generation"
 */
@Slf4j
@Component
public class MenuPermissionGenerator {

    private static PermissionRepository permissionRepository;

    /**
     * Inject PermissionRepository via setter (required for JPA Entity Listeners).
     * Entity listeners are not Spring-managed by default, so we use static injection.
     */
    @Autowired
    public void setPermissionRepository(PermissionRepository repository) {
        MenuPermissionGenerator.permissionRepository = repository;
    }

    /**
     * Automatically generate permissions when a new menu is created.
     *
     * @param menu The newly persisted Menu entity
     */
    @PostPersist
    public void generatePermissionsOnCreate(Menu menu) {
        log.info("Auto-generating permissions for newly created menu: {} ({})",
                 menu.getMenuCode(), menu.getMenuName());
        generatePermissions(menu);
    }

    /**
     * Update permissions when menu properties change.
     *
     * @param menu The updated Menu entity
     */
    @PostUpdate
    public void updatePermissionsOnUpdate(Menu menu) {
        log.info("Updating permissions for modified menu: {} ({})",
                 menu.getMenuCode(), menu.getMenuName());
        generatePermissions(menu);
    }

    /**
     * Core permission generation logic.
     *
     * Generates permissions based on menu properties:
     * - API permissions if apiEndpoint exists
     * - MENU permissions if menuPath exists
     *
     * @param menu The Menu entity to generate permissions for
     */
    private void generatePermissions(Menu menu) {
        if (permissionRepository == null) {
            log.warn("PermissionRepository not initialized. Skipping permission generation.");
            return;
        }

        if (menu.getMenuCode() == null || menu.getMenuCode().isEmpty()) {
            log.warn("Menu has no menuCode. Skipping permission generation.");
            return;
        }

        List<Permission> permissions = new ArrayList<>();

        // Generate API permission if apiEndpoint exists
        if (menu.getApiEndpoint() != null && !menu.getApiEndpoint().isEmpty()) {
            Permission apiPermission = createPermission(
                menu.getMenuCode(),
                "API",
                "READ",
                menu.getApiEndpoint(),
                menu,
                "Auto-generated API permission from menu: " + menu.getMenuCode()
            );
            permissions.add(apiPermission);
        }

        // Generate MENU permissions if menuPath exists
        if (menu.getMenuPath() != null && !menu.getMenuPath().isEmpty()) {
            // READ permission (always created for menu access)
            permissions.add(createPermission(
                menu.getMenuCode(),
                "MENU",
                "READ",
                menu.getMenuPath(),
                menu,
                "Auto-generated MENU READ permission from menu: " + menu.getMenuCode()
            ));

            // WRITE permission (for create/update actions)
            permissions.add(createPermission(
                menu.getMenuCode(),
                "MENU",
                "WRITE",
                menu.getMenuPath(),
                menu,
                "Auto-generated MENU WRITE permission from menu: " + menu.getMenuCode()
            ));

            // DELETE permission (for delete actions)
            permissions.add(createPermission(
                menu.getMenuCode(),
                "MENU",
                "DELETE",
                menu.getMenuPath(),
                menu,
                "Auto-generated MENU DELETE permission from menu: " + menu.getMenuCode()
            ));
        }

        // Save all permissions (with duplicate handling)
        for (Permission permission : permissions) {
            try {
                // Check if permission already exists
                boolean exists = permissionRepository.existsByPermissionCodeAndPermissionTypeAndPermissionActionAndTenantId(
                    permission.getPermissionCode(),
                    permission.getPermissionType(),
                    permission.getPermissionAction(),
                    permission.getTenantId()
                );

                if (!exists) {
                    permissionRepository.save(permission);
                    log.debug("Created permission: {}:{}:{}",
                             permission.getPermissionType(),
                             permission.getPermissionCode(),
                             permission.getPermissionAction());
                } else {
                    log.debug("Permission already exists, skipping: {}:{}:{}",
                             permission.getPermissionType(),
                             permission.getPermissionCode(),
                             permission.getPermissionAction());
                }
            } catch (Exception e) {
                log.error("Failed to create permission for menu {}: {}",
                         menu.getMenuCode(), e.getMessage());
            }
        }

        log.info("Permission generation completed for menu: {}. Generated {} permissions.",
                 menu.getMenuCode(), permissions.size());
    }

    /**
     * Helper method to create a Permission entity.
     *
     * @param code Permission code (same as menu code)
     * @param type Permission type (API or MENU)
     * @param action Permission action (READ, WRITE, DELETE)
     * @param resourcePath Resource path (API endpoint or menu path)
     * @param menu Source menu that generated this permission
     * @param description Human-readable description
     * @return Permission entity (not yet persisted)
     */
    private Permission createPermission(String code, String type, String action,
                                       String resourcePath, Menu menu, String description) {
        Permission permission = new Permission();
        permission.setPermissionCode(code);
        permission.setPermissionType(type);
        permission.setPermissionAction(action);
        permission.setResourcePath(resourcePath);
        permission.setMenu(menu);
        permission.setTenantId(menu.getTenantId());
        permission.setDescription(description);
        return permission;
    }
}
