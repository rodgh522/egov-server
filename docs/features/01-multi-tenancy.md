# Feature: Multi-Tenancy

## 1. Core Rules
- **Strict Isolation**: All data must be isolated by `TENANT_ID`.
- **Filtering**: Application-level filtering via Hibernate `@Filter` is primary; RLS is secondary/optional.
- **Context**: `TenantContext` (ThreadLocal) holds the current tenant ID from the authenticated user.

## 2. Schema Design regarding Multi-tenancy
All tables MUST have `TENANT_ID` and standard audit fields.

### Standard Tenant Table
```sql
CREATE TABLE TENANTS (
    TENANT_ID VARCHAR(20) NOT NULL PRIMARY KEY,
    TENANT_NAME VARCHAR(100),
    USE_AT CHAR(1) DEFAULT 'Y'
    -- ... audit fields
);
```

### Tenant Filter Implementation
**Rule**: Use `TenantContext` to set the Hibernate filter parameter.

```java
@Entity
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = String.class))
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
public class BaseTenantEntity {
    @Column(name = "TENANT_ID", nullable = false, updatable = false)
    private String tenantId;
}
```

## 3. Tenant Context Aspect
**Rule**: Intercept service/repository calls or Web requests to set the filter.

```java
// Logic:
// 1. Get Authentication from SecurityContext
// 2. Extract tenantId from CustomUserDetails
// 3. Enable "tenantFilter" on Hibernate Session
```
