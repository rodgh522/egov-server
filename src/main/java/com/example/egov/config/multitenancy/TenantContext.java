package com.example.egov.config.multitenancy;

import lombok.extern.slf4j.Slf4j;

/**
 * Thread-local context for storing the current tenant ID.
 *
 * This class provides a centralized mechanism for managing tenant context
 * within a multi-tenant application. It uses ThreadLocal to ensure that
 * each request thread has its own isolated tenant ID.
 *
 * Usage:
 * - Set tenant ID at the beginning of request processing (via TenantInterceptor)
 * - Access tenant ID throughout the request lifecycle
 * - Clear tenant ID after request completion to prevent memory leaks
 *
 * Thread Safety:
 * - ThreadLocal ensures thread-safe access to tenant ID
 * - Each thread maintains its own copy of the tenant ID
 */
@Slf4j
public class TenantContext {

    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    /**
     * Sets the tenant ID for the current thread.
     *
     * @param tenantId the tenant ID to set
     */
    public static void setCurrentTenantId(String tenantId) {
        log.debug("Setting tenant ID: {}", tenantId);
        currentTenant.set(tenantId);
    }

    /**
     * Gets the tenant ID for the current thread.
     *
     * @return the current tenant ID, or null if not set
     */
    public static String getCurrentTenantId() {
        return currentTenant.get();
    }

    /**
     * Clears the tenant ID for the current thread.
     *
     * IMPORTANT: This must be called after request processing to prevent
     * ThreadLocal memory leaks in thread pool environments.
     */
    public static void clear() {
        log.debug("Clearing tenant ID: {}", currentTenant.get());
        currentTenant.remove();
    }

    /**
     * Checks if a tenant ID is currently set for the current thread.
     *
     * @return true if tenant ID is set, false otherwise
     */
    public static boolean isSet() {
        return currentTenant.get() != null;
    }
}
