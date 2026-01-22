package com.example.egov.config.multitenancy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor to extract and set tenant ID from authenticated user.
 *
 * This interceptor runs before each request to:
 * 1. Extract the tenant ID from the authenticated user's principal
 * 2. Store it in TenantContext for use throughout the request lifecycle
 * 3. Clear the context after request completion to prevent memory leaks
 *
 * Flow:
 * - preHandle: Extract tenant ID from Authentication → Set in TenantContext
 * - afterCompletion: Clear TenantContext to prevent ThreadLocal leaks
 */
@Slf4j
@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Value("${app.multi-tenancy.default-tenant:SYSTEM}")
    private String defaultTenant;

    /**
     * Called before request handler execution.
     * Extracts tenant ID from authenticated user and sets it in TenantContext.
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  chosen handler to execute
     * @return true to continue request processing
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                            @NonNull HttpServletResponse response,
                            @NonNull Object handler) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String tenantId = extractTenantId(authentication);

        // Set tenant ID in context
        TenantContext.setCurrentTenantId(tenantId);

        log.debug("Tenant ID set for request: {} (URI: {})", tenantId, request.getRequestURI());

        return true;
    }

    /**
     * Called after request processing is complete.
     * Clears tenant context to prevent memory leaks.
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  handler used for request
     * @param ex       exception thrown during handler execution, if any
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {
        // Clear tenant context to prevent ThreadLocal memory leaks
        TenantContext.clear();
        log.debug("Tenant context cleared for request: {}", request.getRequestURI());
    }

    /**
     * Extracts tenant ID from authentication principal.
     *
     * Supports multiple principal types:
     * - CustomUserDetails (when implemented): extracts tenantId directly
     * - UserDetails with tenant property: uses reflection to get tenantId
     * - Fallback: uses default system tenant
     *
     * @param authentication the authentication object
     * @return extracted tenant ID or default tenant
     */
    private String extractTenantId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("No authentication found, using default tenant: {}", defaultTenant);
            return defaultTenant;
        }

        Object principal = authentication.getPrincipal();

        // Handle string principal (e.g., "anonymousUser")
        if (principal instanceof String) {
            log.debug("String principal detected: {}, using default tenant", principal);
            return defaultTenant;
        }

        // Try to extract tenant ID from principal
        // When CustomUserDetails is implemented, this will work automatically
        try {
            // Use reflection to get tenantId if available
            var getTenantIdMethod = principal.getClass().getMethod("getTenantId");
            String tenantId = (String) getTenantIdMethod.invoke(principal);

            if (tenantId != null && !tenantId.isEmpty()) {
                log.debug("Extracted tenant ID from principal: {}", tenantId);
                return tenantId;
            }
        } catch (Exception e) {
            log.debug("Could not extract tenant ID from principal: {}", e.getMessage());
        }

        // Fallback to default tenant
        log.debug("Using default tenant: {}", defaultTenant);
        return defaultTenant;
    }
}
