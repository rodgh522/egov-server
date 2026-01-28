package com.example.egov.config.security;

import org.springframework.stereotype.Component;

/**
 * Bean for custom security expressions in @PreAuthorize annotations.
 *
 * Usage:
 *   @PreAuthorize("@securityEvaluator.hasPermission('menu', 'READ')")
 *   @PreAuthorize("@securityEvaluator.hasBranch(#branchId)")
 *   @PreAuthorize("@securityEvaluator.hasAnyPermission('API:user:READ', 'API:user:WRITE')")
 */
@Component("securityEvaluator")
public class SecurityExpressionBean {

    /**
     * Check if user has specific permission for resource and action
     * @param resourceCode Resource code (e.g., "menu", "user")
     * @param action Action type (e.g., "READ", "WRITE", "DELETE")
     */
    public boolean hasPermission(String resourceCode, String action) {
        return SecurityContextUtil.hasPermission(resourceCode, action);
    }

    /**
     * Check if user has full permission code
     * @param permissionCode Full permission code (e.g., "API:menu:READ")
     */
    public boolean hasPermissionCode(String permissionCode) {
        return SecurityContextUtil.hasPermission(permissionCode);
    }

    /**
     * Check if user has any of the specified permissions
     * @param permissionCodes Permission codes to check
     */
    public boolean hasAnyPermission(String... permissionCodes) {
        for (String code : permissionCodes) {
            if (SecurityContextUtil.hasPermission(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if user has all of the specified permissions
     * @param permissionCodes Permission codes to check
     */
    public boolean hasAllPermissions(String... permissionCodes) {
        for (String code : permissionCodes) {
            if (!SecurityContextUtil.hasPermission(code)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if user belongs to specific branch
     * @param branchId Branch ID to check
     */
    public boolean hasBranch(String branchId) {
        String currentBranch = SecurityContextUtil.getCurrentBranchId();
        return branchId != null && branchId.equals(currentBranch);
    }

    /**
     * Check if user belongs to specific group
     * @param groupId Group ID to check
     */
    public boolean hasGroup(String groupId) {
        String currentGroup = SecurityContextUtil.getCurrentGroupId();
        return groupId != null && groupId.equals(currentGroup);
    }

    /**
     * Check if user has specific position
     * @param positionId Position ID to check
     */
    public boolean hasPosition(String positionId) {
        String currentPosition = SecurityContextUtil.getCurrentPositionId();
        return positionId != null && positionId.equals(currentPosition);
    }

    /**
     * Check if user has specific role
     * @param roleId Role ID to check
     */
    public boolean hasRole(String roleId) {
        return SecurityContextUtil.hasRole(roleId);
    }

    /**
     * Check if user has any of the specified roles
     * @param roleIds Role IDs to check
     */
    public boolean hasAnyRole(String... roleIds) {
        return SecurityContextUtil.hasAnyRole(roleIds);
    }

    /**
     * Check if user belongs to specific tenant
     * @param tenantId Tenant ID to check
     */
    public boolean hasTenant(String tenantId) {
        String currentTenant = SecurityContextUtil.getCurrentTenantId();
        return tenantId != null && tenantId.equals(currentTenant);
    }

    /**
     * Check if user is from same branch as target entity's branch
     * @param targetBranchId Target entity's branch ID
     */
    public boolean isSameBranch(String targetBranchId) {
        return hasBranch(targetBranchId);
    }

    /**
     * Check if user can access resource (permission + optional branch check)
     * @param resourceCode Resource code
     * @param action Action type
     * @param targetBranchId Target branch ID (null to skip branch check)
     */
    public boolean canAccess(String resourceCode, String action, String targetBranchId) {
        boolean hasPermission = hasPermission(resourceCode, action);
        if (targetBranchId == null) {
            return hasPermission;
        }
        return hasPermission && hasBranch(targetBranchId);
    }
}
