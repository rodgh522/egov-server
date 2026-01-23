package com.example.egov.web.admin;

import com.example.egov.domain.admin.Menu;
import com.example.egov.service.admin.MenuService;
import com.example.egov.web.admin.dto.MenuCreateRequest;
import com.example.egov.web.admin.dto.MenuResponse;
import com.example.egov.web.admin.dto.MenuUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Menu management.
 *
 * Provides endpoints for CRUD operations on menus.
 * Menu creation/updates automatically trigger permission generation.
 *
 * Security: All endpoints should be protected with appropriate permissions.
 * TODO: Add @PreAuthorize annotations once security is fully configured.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * Create a new menu.
     *
     * Permissions will be auto-generated based on menuPath and apiEndpoint.
     *
     * @param request Menu creation request
     * @return Created menu response
     */
    @PostMapping
    // @PreAuthorize("hasAuthority('API:menu-management:WRITE')")
    public ResponseEntity<MenuResponse> createMenu(@Valid @RequestBody MenuCreateRequest request) {
        log.info("Creating menu: {}", request.getMenuCode());

        Menu menu = menuService.createMenu(request);
        MenuResponse response = MenuResponse.from(menu);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update an existing menu.
     *
     * @param menuNo Menu ID
     * @param request Update request
     * @return Updated menu response
     */
    @PutMapping("/{menuNo}")
    // @PreAuthorize("hasAuthority('API:menu-management:WRITE')")
    public ResponseEntity<MenuResponse> updateMenu(
            @PathVariable Long menuNo,
            @Valid @RequestBody MenuUpdateRequest request) {
        log.info("Updating menu ID: {}", menuNo);

        Menu menu = menuService.updateMenu(menuNo, request);
        MenuResponse response = MenuResponse.from(menu);

        return ResponseEntity.ok(response);
    }

    /**
     * Get a menu by ID.
     *
     * @param menuNo Menu ID
     * @return Menu response
     */
    @GetMapping("/{menuNo}")
    // @PreAuthorize("hasAuthority('API:menu-management:READ')")
    public ResponseEntity<MenuResponse> getMenu(@PathVariable Long menuNo) {
        log.debug("Fetching menu ID: {}", menuNo);

        return menuService.getMenu(menuNo)
                .map(menu -> ResponseEntity.ok(MenuResponse.from(menu)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get a menu by code.
     *
     * @param menuCode Menu code
     * @return Menu response
     */
    @GetMapping("/code/{menuCode}")
    // @PreAuthorize("hasAuthority('API:menu-management:READ')")
    public ResponseEntity<MenuResponse> getMenuByCode(@PathVariable String menuCode) {
        log.debug("Fetching menu by code: {}", menuCode);

        return menuService.getMenuByCode(menuCode)
                .map(menu -> ResponseEntity.ok(MenuResponse.from(menu)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all menus for the current tenant.
     *
     * Results are automatically filtered by tenant.
     *
     * @return List of menu responses
     */
    @GetMapping
    // @PreAuthorize("hasAuthority('API:menu-management:READ')")
    public ResponseEntity<List<MenuResponse>> getAllMenus() {
        log.debug("Fetching all menus");

        List<Menu> menus = menuService.getAllMenus();
        List<MenuResponse> responses = MenuResponse.fromList(menus);

        return ResponseEntity.ok(responses);
    }

    /**
     * Get all visible and active menus for the current tenant.
     *
     * This endpoint is typically used for rendering user menus.
     *
     * @return List of visible menu responses
     */
    @GetMapping("/visible")
    // @PreAuthorize("hasAuthority('API:menu-management:READ')")
    public ResponseEntity<List<MenuResponse>> getVisibleMenus() {
        log.debug("Fetching visible menus");

        List<Menu> menus = menuService.getVisibleMenus();
        List<MenuResponse> responses = MenuResponse.fromList(menus);

        return ResponseEntity.ok(responses);
    }

    /**
     * Delete a menu.
     *
     * Associated permissions will be cascade deleted.
     *
     * @param menuNo Menu ID to delete
     * @return No content response
     */
    @DeleteMapping("/{menuNo}")
    // @PreAuthorize("hasAuthority('API:menu-management:DELETE')")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long menuNo) {
        log.info("Deleting menu ID: {}", menuNo);

        menuService.deleteMenu(menuNo);

        return ResponseEntity.noContent().build();
    }

    /**
     * Check if a menu code exists.
     *
     * @param menuCode Menu code to check
     * @return Boolean response
     */
    @GetMapping("/exists/{menuCode}")
    // @PreAuthorize("hasAuthority('API:menu-management:READ')")
    public ResponseEntity<Boolean> existsByMenuCode(@PathVariable String menuCode) {
        log.debug("Checking if menu code exists: {}", menuCode);

        boolean exists = menuService.existsByMenuCode(menuCode);

        return ResponseEntity.ok(exists);
    }
}
