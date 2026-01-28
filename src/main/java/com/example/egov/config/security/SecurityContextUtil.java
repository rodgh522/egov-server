package com.example.egov.config.security;

import com.example.egov.domain.auth.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * Utility class for easy access to current user's security context.
 *
 * Usage:
 *   String tenantId = SecurityContextUtil.getCurrentTenantId();
 *   boolean hasPermission = SecurityContextUtil.hasPermission("menu", "READ");
 */
public final class SecurityContextUtil {

    private SecurityContextUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Get current authentication or empty if not authenticated
     */
    public static Optional<Authentication> getAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
            return Optional.of(auth);
        }
        return Optional.empty();
    }

    /**
     * Get current user details or empty if not authenticated
     */
    public static Optional<CustomUserDetails> getCurrentUser() {
        return getAuthentication()
                .map(Authentication::getPrincipal)
                .filter(p -> p instanceof CustomUserDetails)
                .map(p -> (CustomUserDetails) p);
    }

    /**
     * Get current user ID
     */
    public static String getCurrentUserId() {
        return getCurrentUser().map(CustomUserDetails::getUserId).orElse(null);
    }

    /**
     * Get current tenant ID
     */
    public static String getCurrentTenantId() {
        return getCurrentUser().map(CustomUserDetails::getTenantId).orElse(null);
    }

    /**
     * Get current branch ID
     */
    public static String getCurrentBranchId() {
        return getCurrentUser().map(CustomUserDetails::getBranchId).orElse(null);
    }

    /**
     * Get current group ID
     */
    public static String getCurrentGroupId() {
        return getCurrentUser().map(CustomUserDetails::getGroupId).orElse(null);
    }

    /**
     * Get current position ID
     */
    public static String getCurrentPositionId() {
        return getCurrentUser().map(CustomUserDetails::getPositionId).orElse(null);
    }

    /**
     * Get current esntl ID (primary key)
     */
    public static String getCurrentEsntlId() {
        return getCurrentUser().map(CustomUserDetails::getEsntlId).orElse(null);
    }

    /**
     * Get current user's role IDs
     */
    public static Set<String> getCurrentRoleIds() {
        return getCurrentUser().map(CustomUserDetails::getRoleIds).orElse(Collections.emptySet());
    }

    /**
     * Get current user's permission codes
     */
    public static Set<String> getCurrentPermissions() {
        return getCurrentUser().map(CustomUserDetails::getPermissions).orElse(Collections.emptySet());
    }

    /**
     * Check if current user has specific permission code
     * @param permissionCode Full permission code (e.g., "API:menu:READ")
     */
    public static boolean hasPermission(String permissionCode) {
        return getCurrentPermissions().contains(permissionCode);
    }

    /**
     * Check if current user has permission for resource and action
     * @param resourceCode Resource code (e.g., "menu")
     * @param action Action type (e.g., "READ", "WRITE", "DELETE")
     */
    public static boolean hasPermission(String resourceCode, String action) {
        String permissionCode = "API:" + resourceCode + ":" + action;
        return hasPermission(permissionCode);
    }

    /**
     * Check if current user has any of the specified roles
     */
    public static boolean hasAnyRole(String... roleIds) {
        Set<String> currentRoles = getCurrentRoleIds();
        for (String roleId : roleIds) {
            if (currentRoles.contains(roleId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if current user has specific role
     */
    public static boolean hasRole(String roleId) {
        return getCurrentRoleIds().contains(roleId);
    }

    /**
     * Check if user is authenticated
     */
    public static boolean isAuthenticated() {
        return getCurrentUser().isPresent();
    }
}
