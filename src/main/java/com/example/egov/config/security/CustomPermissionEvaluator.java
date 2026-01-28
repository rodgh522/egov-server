package com.example.egov.config.security;

import com.example.egov.domain.auth.CustomUserDetails;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Custom permission evaluator for @PreAuthorize("hasPermission(...)") expressions.
 *
 * Usage in controllers:
 *   @PreAuthorize("hasPermission(null, 'menu', 'READ')")
 *   @PreAuthorize("hasPermission(#id, 'user', 'WRITE')")
 */
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    /**
     * Evaluate permission for a specific target object
     *
     * @param authentication Current authentication
     * @param targetDomainObject Target object (used as resource code if String, otherwise ignored)
     * @param permission Permission string - can be "ACTION" or "RESOURCE:ACTION" format
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return false;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String permissionStr = String.valueOf(permission);

        // If permission string already contains full format (API:resource:action)
        if (permissionStr.startsWith("API:")) {
            return userDetails.hasPermission(permissionStr);
        }

        // If targetDomainObject is provided as resource code
        if (targetDomainObject instanceof String) {
            String resourceCode = (String) targetDomainObject;
            String fullPermission = "API:" + resourceCode + ":" + permissionStr;
            return userDetails.hasPermission(fullPermission);
        }

        return false;
    }

    /**
     * Evaluate permission for target identified by type and ID
     *
     * @param authentication Current authentication
     * @param targetId Target object ID (can be null for general resource permissions)
     * @param targetType Target type (resource code, e.g., "menu", "user")
     * @param permission Permission action (e.g., "READ", "WRITE", "DELETE")
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId,
                                  String targetType, Object permission) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return false;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String fullPermission = "API:" + targetType + ":" + permission;

        return userDetails.hasPermission(fullPermission);
    }
}
