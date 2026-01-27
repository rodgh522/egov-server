package com.example.egov.domain.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByTenantId(String tenantId);

    Optional<Menu> findByMenuCode(String menuCode);

    boolean existsByMenuCode(String menuCode);

    List<Menu> findByIsVisibleAndIsActiveOrderByMenuOrderAsc(String isVisible, String isActive);

    @Query(value = "SELECT COUNT(*) > 0 FROM menus WHERE upper_menu_no = :upperMenuNo", nativeQuery = true)
    boolean existsByUpperMenuNo(@Param("upperMenuNo") Long upperMenuNo);

    List<Menu> findByUpperMenuNo(Long upperMenuNo);

    @Query("SELECT DISTINCT m FROM Menu m " +
            "WHERE m.tenantId = :tenantId " +
            "AND m.isVisible = 'Y' " +
            "AND m.isActive = 'Y' " +
            "AND EXISTS (" +
            "    SELECT 1 FROM Permission p " +
            "    JOIN RolePermission rp ON p.permissionId = rp.permission.permissionId " + // Assuming join path is
                                                                                           // correct in JPQL
            "    JOIN UserRole ur ON rp.role.roleId = ur.role.roleId " +
            "    WHERE ur.user.userId = :userId " +
            "    AND p.menu.menuNo = m.menuNo " +
            "    AND p.permissionType = 'MENU' " +
            "    AND p.permissionAction = 'READ'" +
            ") " +
            "ORDER BY m.menuOrder ASC")
    List<Menu> findAccessibleMenus(@Param("userId") String userId, @Param("tenantId") String tenantId);
}
// Note: JPQL might need adjustment depending on exact field names in entities.
// RolePermission has `role` and `permission`. Permission has `permissionId`.
// UserRole has `user` and `role`.
// Checked JPQL logic: p.menu (Permission has `menu` object). m.menuNo (Menu
// ID).
