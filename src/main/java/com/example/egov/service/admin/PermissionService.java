package com.example.egov.service.admin;

import com.example.egov.domain.admin.Menu;
import com.example.egov.domain.admin.Permission;
import com.example.egov.domain.admin.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for automatic permission generation from Menu entities.
 *
 * This service implements the menu-driven permission generation strategy.
 * Permissions are automatically created based on menu properties.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    /**
     * Auto-generate permissions for a menu.
     *
     * Generates permissions based on menu properties:
     * - API permissions if apiEndpoint exists
     * - MENU permissions if menuPath exists
     *
     * @param menu The Menu entity to generate permissions for
     */
    @Transactional
    public void autoGeneratePermissionsForMenu(Menu menu) {
        if (menu == null || menu.getMenuNo() == null) {
            log.warn("Cannot generate permissions for null or unsaved menu");
            return;
        }

        if (menu.getMenuCode() == null || menu.getMenuCode().isEmpty()) {
            log.warn("Menu has no menuCode. Skipping permission generation for menu ID: {}", menu.getMenuNo());
            return;
        }

        log.info("Auto-generating permissions for menu: {} (ID: {})", menu.getMenuCode(), menu.getMenuNo());

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
        int created = 0;
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
                    created++;
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

        log.info("Permission generation completed for menu: {}. Created {} new permissions.",
                 menu.getMenuCode(), created);
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
