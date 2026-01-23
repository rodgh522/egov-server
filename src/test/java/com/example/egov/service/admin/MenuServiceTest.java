package com.example.egov.service.admin;

import com.example.egov.config.multitenancy.TenantContext;
import com.example.egov.domain.admin.*;
import com.example.egov.web.admin.dto.MenuCreateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for Menu Permission Auto-Generation feature.
 *
 * This test verifies that permissions are automatically generated
 * when a menu is created via MenuService.
 */
@SpringBootTest
@Transactional
public class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @BeforeEach
    public void setup() {
        // Clear tenant context
        TenantContext.clear();

        // Clean up existing data
        permissionRepository.deleteAllInBatch();
        menuRepository.deleteAllInBatch();
        tenantRepository.deleteAllInBatch();

        // Create test tenant
        Tenant tenant = new Tenant();
        tenant.setTenantId("TEST_TENANT");
        tenant.setTenantName("Test Tenant");
        tenant.setUseAt("Y");
        tenantRepository.save(tenant);

        // Set tenant context
        TenantContext.setCurrentTenantId("TEST_TENANT");
    }

    @AfterEach
    public void cleanup() {
        TenantContext.clear();
    }

    @Test
    public void testMenuCreationGeneratesPermissions() {
        // Given: A new menu with both menuPath and apiEndpoint
        MenuCreateRequest request = new MenuCreateRequest();
        request.setMenuCode("test-dashboard");
        request.setMenuName("Test Dashboard");
        request.setMenuPath("/test/dashboard");
        request.setApiEndpoint("/api/v1/test/dashboard");
        request.setIsVisible("Y");
        request.setIsActive("Y");

        // When: Menu is created via service
        Menu savedMenu = menuService.createMenu(request);

        // Then: Permissions should be auto-generated
        List<Permission> permissions = permissionRepository.findAll();

        // Expected permissions:
        // 1. API:test-dashboard:READ
        // 2. MENU:test-dashboard:READ
        // 3. MENU:test-dashboard:WRITE
        // 4. MENU:test-dashboard:DELETE
        assertEquals(4, permissions.size(), "Should generate 4 permissions (1 API + 3 MENU)");

        // Verify API permission
        boolean hasApiRead = permissions.stream()
                .anyMatch(p -> "API".equals(p.getPermissionType())
                        && "test-dashboard".equals(p.getPermissionCode())
                        && "READ".equals(p.getPermissionAction()));
        assertTrue(hasApiRead, "Should have API:test-dashboard:READ permission");

        // Verify MENU permissions
        long menuPermissionCount = permissions.stream()
                .filter(p -> "MENU".equals(p.getPermissionType())
                        && "test-dashboard".equals(p.getPermissionCode()))
                .count();
        assertEquals(3, menuPermissionCount, "Should have 3 MENU permissions (READ, WRITE, DELETE)");
    }

    @Test
    public void testMenuWithOnlyMenuPathGeneratesMenuPermissions() {
        // Given: A menu with only menuPath (no apiEndpoint)
        MenuCreateRequest request = new MenuCreateRequest();
        request.setMenuCode("settings-page");
        request.setMenuName("Settings Page");
        request.setMenuPath("/settings");
        request.setIsVisible("Y");
        request.setIsActive("Y");

        // When: Menu is created via service
        menuService.createMenu(request);

        // Then: Only MENU permissions should be generated
        List<Permission> permissions = permissionRepository.findAll();
        assertEquals(3, permissions.size(), "Should generate 3 MENU permissions only");

        boolean allAreMenuType = permissions.stream()
                .allMatch(p -> "MENU".equals(p.getPermissionType()));
        assertTrue(allAreMenuType, "All permissions should be MENU type");
    }

    @Test
    public void testMenuWithOnlyApiEndpointGeneratesApiPermission() {
        // Given: A menu with only apiEndpoint (no menuPath)
        MenuCreateRequest request = new MenuCreateRequest();
        request.setMenuCode("api-health");
        request.setMenuName("API Health Check");
        request.setApiEndpoint("/api/v1/health");
        request.setIsVisible("Y");
        request.setIsActive("Y");

        // When: Menu is created via service
        menuService.createMenu(request);

        // Then: Only API permission should be generated
        List<Permission> permissions = permissionRepository.findAll();
        assertEquals(1, permissions.size(), "Should generate 1 API permission only");

        Permission apiPermission = permissions.get(0);
        assertEquals("API", apiPermission.getPermissionType());
        assertEquals("api-health", apiPermission.getPermissionCode());
        assertEquals("READ", apiPermission.getPermissionAction());
        assertEquals("/api/v1/health", apiPermission.getResourcePath());
    }

    @Test
    public void testPermissionTenantIdMatchesMenuTenantId() {
        // Given: A menu for specific tenant
        MenuCreateRequest request = new MenuCreateRequest();
        request.setMenuCode("tenant-test");
        request.setMenuName("Tenant Test");
        request.setMenuPath("/tenant-test");

        // When: Menu is created via service
        menuService.createMenu(request);

        // Then: All generated permissions should have same tenantId
        List<Permission> permissions = permissionRepository.findAll();
        boolean allHaveCorrectTenant = permissions.stream()
                .allMatch(p -> "TEST_TENANT".equals(p.getTenantId()));
        assertTrue(allHaveCorrectTenant, "All permissions should belong to TEST_TENANT");
    }
}
