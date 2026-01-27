package com.example.egov.config.multitenancy;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

/**
 * AOP Aspect for automatically enabling Hibernate tenant filter on all
 * repository operations.
 *
 * This aspect intercepts all method calls to JPA repositories and automatically
 * enables
 * the "tenantFilter" Hibernate filter with the current tenant ID from
 * TenantContext.
 *
 * How it works:
 * 1. Intercepts all repository method calls (via @Around advice)
 * 2. Unwraps Hibernate Session from JPA EntityManager
 * 3. Enables the "tenantFilter" with current tenant ID
 * 4. Proceeds with repository method execution
 * 5. Hibernate automatically adds "WHERE TENANT_ID = :tenantId" to queries
 *
 * Filter Definition:
 * Must be defined on entity classes using:
 * 
 * @FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId",
 *                 type = String.class))
 * @Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
 */
@Slf4j
@Aspect
@Component
public class TenantFilterAspect {

    private static final String SYSTEM_TENANT_ID = "SYSTEM";

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Around advice that wraps all repository method calls.
     *
     * Pointcut expression matches:
     * - All methods in classes implementing Repository interface
     * - Located in com.example.egov.domain package and sub-packages
     * - Ending with "Repository" class name
     *
     * SYSTEM tenant has access to all data across tenants (filter disabled).
     * Other tenants only see their own data (filter enabled).
     *
     * @param joinPoint the join point representing the repository method call
     * @return the result of the repository method execution
     * @throws Throwable if the repository method throws an exception
     */
    @Around("execution(* com.example.egov.domain..*Repository+.*(..))")
    public Object enableTenantFilter(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // Get Hibernate Session from JPA EntityManager
            Session session = entityManager.unwrap(Session.class);

            // Get current tenant ID from context
            String tenantId = TenantContext.getCurrentTenantId();

            if (tenantId != null) {
                // SYSTEM tenant can access all data - disable filter
                if (SYSTEM_TENANT_ID.equals(tenantId)) {
                    session.disableFilter("tenantFilter");
                    log.trace("Tenant filter disabled for SYSTEM tenant: {}",
                            joinPoint.getSignature().toShortString());
                } else {
                    // Enable tenant filter for non-SYSTEM tenants
                    Filter filter = session.enableFilter("tenantFilter");
                    filter.setParameter("tenantId", tenantId);

                    log.trace("Tenant filter enabled for repository call: {} with tenantId: {}",
                            joinPoint.getSignature().toShortString(), tenantId);
                }
            } else {
                // Explicitly disable filter if no tenant ID is present
                session.disableFilter("tenantFilter");

                log.warn("No tenant ID found in context for repository call: {}. " +
                        "Filter disabled - this may expose cross-tenant data!",
                        joinPoint.getSignature().toShortString());
            }

            // Proceed with repository method execution
            return joinPoint.proceed();

        } catch (Exception e) {
            log.error("Error enabling tenant filter for repository call: {}",
                    joinPoint.getSignature().toShortString(), e);
            throw e;
        }
    }

    /**
     * Alternative pointcut for custom query methods.
     * Use this if you have custom @Query annotations that need tenant filtering.
     *
     * Note: Currently commented out. Uncomment if needed.
     */
    // @Around("@annotation(org.springframework.data.jpa.repository.Query)")
    // public Object enableTenantFilterForCustomQuery(ProceedingJoinPoint joinPoint)
    // throws Throwable {
    // return enableTenantFilter(joinPoint);
    // }
}
