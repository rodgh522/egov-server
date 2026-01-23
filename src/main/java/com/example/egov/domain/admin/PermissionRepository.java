package com.example.egov.domain.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    boolean existsByPermissionCodeAndPermissionTypeAndPermissionActionAndTenantId(
            String permissionCode, String permissionType, String permissionAction, String tenantId);

    @Modifying
    @Query("UPDATE Permission p SET p.resourcePath = :resourcePath WHERE p.menu.menuNo = :menuNo")
    void updateResourcePathsByMenuNo(@Param("menuNo") Long menuNo, @Param("resourcePath") String resourcePath);
}
