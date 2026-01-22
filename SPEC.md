# CRM Project - Technical Specification

## 1. Executive Summary

This document provides a comprehensive technical specification for **crm-project**, a multi-tenant CRM dashboard built on eGovFrame 5.0 Beta. The system features automated permission generation, advanced RBAC, and a hierarchical organizational structure with strict tenant isolation.

### Key Features
- **Multi-tenancy**: Strict data isolation at PostgreSQL level
- **Automated Permission System**: Menu-driven permission auto-generation
- **Advanced RBAC**: Multiple role assignments with granular permissions
- **Organizational Hierarchy**: Tenant → Branch → Group → Position → User
- **Modern Stack**: Spring Boot 3.x + React 19.2 + shadcn/ui

---

## 2. Schema Design (PostgreSQL ERD)

### 2.1 Core Entity Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                   Multi-Tenant Architecture                      │
└─────────────────────────────────────────────────────────────────┘

TENANTS (Root)
    │
    ├─→ BRANCHES (Physical locations)
    │       │
    │       └─→ GROUPS (Departments/Teams)
    │               │
    │               └─→ POSITIONS (Job roles)
    │                       │
    │                       └─→ USERS
    │
    ├─→ ROLES (Tenant-specific)
    │       │
    │       └─→ ROLE_PERMISSIONS (M:N)
    │               │
    │               └─→ PERMISSIONS (Auto-generated from Menus)
    │
    └─→ MENUS (Menu structure)
            │
            └─→ PERMISSIONS (1:N, auto-created)
```

### 2.2 Detailed Table Schemas

#### 2.2.1 Tenant & Organization Tables

```sql
-- =====================================================
-- TENANT MANAGEMENT
-- =====================================================

-- Main tenant table (ALREADY EXISTS)
CREATE TABLE TENANTS (
    TENANT_ID VARCHAR(20) NOT NULL,
    TENANT_NAME VARCHAR(100),
    TENANT_DESCRIPTION VARCHAR(255),
    USE_AT CHAR(1) DEFAULT 'Y',
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP,
    PRIMARY KEY (TENANT_ID)
);

-- Branch: Physical locations under a tenant
CREATE TABLE BRANCHES (
    BRANCH_ID VARCHAR(20) NOT NULL,
    BRANCH_NAME VARCHAR(100) NOT NULL,
    BRANCH_CODE VARCHAR(20) UNIQUE NOT NULL,
    BRANCH_ADDRESS VARCHAR(255),
    BRANCH_PHONE VARCHAR(20),
    PARENT_BRANCH_ID VARCHAR(20), -- Hierarchical branches
    TENANT_ID VARCHAR(20) NOT NULL,
    USE_AT CHAR(1) DEFAULT 'Y',
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP,
    PRIMARY KEY (BRANCH_ID),
    FOREIGN KEY (TENANT_ID) REFERENCES TENANTS(TENANT_ID) ON DELETE CASCADE,
    FOREIGN KEY (PARENT_BRANCH_ID) REFERENCES BRANCHES(BRANCH_ID)
);
CREATE INDEX IDX_BRANCHES_TENANT ON BRANCHES(TENANT_ID);

-- Group: Departments or teams within a branch
CREATE TABLE GROUPS (
    GROUP_ID VARCHAR(20) NOT NULL,
    GROUP_NAME VARCHAR(100) NOT NULL,
    GROUP_CODE VARCHAR(20) NOT NULL,
    GROUP_DESCRIPTION VARCHAR(255),
    BRANCH_ID VARCHAR(20) NOT NULL,
    TENANT_ID VARCHAR(20) NOT NULL,
    USE_AT CHAR(1) DEFAULT 'Y',
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP,
    PRIMARY KEY (GROUP_ID),
    FOREIGN KEY (BRANCH_ID) REFERENCES BRANCHES(BRANCH_ID) ON DELETE CASCADE,
    FOREIGN KEY (TENANT_ID) REFERENCES TENANTS(TENANT_ID) ON DELETE CASCADE,
    UNIQUE (GROUP_CODE, TENANT_ID)
);
CREATE INDEX IDX_GROUPS_TENANT ON GROUPS(TENANT_ID);
CREATE INDEX IDX_GROUPS_BRANCH ON GROUPS(BRANCH_ID);

-- Position: Job titles/positions within the organization
CREATE TABLE POSITIONS (
    POSITION_ID VARCHAR(20) NOT NULL,
    POSITION_NAME VARCHAR(100) NOT NULL,
    POSITION_CODE VARCHAR(20) NOT NULL,
    POSITION_LEVEL INT DEFAULT 0, -- Hierarchy level (0=highest)
    POSITION_DESCRIPTION VARCHAR(255),
    TENANT_ID VARCHAR(20) NOT NULL,
    USE_AT CHAR(1) DEFAULT 'Y',
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP,
    PRIMARY KEY (POSITION_ID),
    FOREIGN KEY (TENANT_ID) REFERENCES TENANTS(TENANT_ID) ON DELETE CASCADE,
    UNIQUE (POSITION_CODE, TENANT_ID)
);
CREATE INDEX IDX_POSITIONS_TENANT ON POSITIONS(TENANT_ID);
```

#### 2.2.2 User & Role Management (Enhanced)

```sql
-- =====================================================
-- USER & ROLE MANAGEMENT (ENHANCED)
-- =====================================================

-- Enhanced USERS table (modify existing)
-- Note: Removed single ROLE_ID FK, will use USER_ROLES junction table
ALTER TABLE USERS DROP FOREIGN KEY IF EXISTS USERS_ibfk_1;
ALTER TABLE USERS DROP COLUMN IF EXISTS ROLE_ID;

ALTER TABLE USERS ADD COLUMN BRANCH_ID VARCHAR(20);
ALTER TABLE USERS ADD COLUMN POSITION_ID VARCHAR(20);
ALTER TABLE USERS ADD COLUMN MANAGER_ID VARCHAR(20); -- Reports to
ALTER TABLE USERS ADD COLUMN PHONE VARCHAR(20);
ALTER TABLE USERS ADD COLUMN UPDATED_DATE TIMESTAMP;

ALTER TABLE USERS ADD FOREIGN KEY (BRANCH_ID) REFERENCES BRANCHES(BRANCH_ID);
ALTER TABLE USERS ADD FOREIGN KEY (POSITION_ID) REFERENCES POSITIONS(POSITION_ID);
ALTER TABLE USERS ADD FOREIGN KEY (MANAGER_ID) REFERENCES USERS(ESNTL_ID);

CREATE INDEX IDX_USERS_TENANT ON USERS(TENANT_ID);
CREATE INDEX IDX_USERS_GROUP ON USERS(GROUP_ID);
CREATE INDEX IDX_USERS_BRANCH ON USERS(BRANCH_ID);

-- User-Role junction table (M:N - users can have multiple roles)
CREATE TABLE USER_ROLES (
    USER_ROLE_ID BIGINT AUTO_INCREMENT,
    USER_ID VARCHAR(20) NOT NULL, -- References USERS.ESNTL_ID
    ROLE_ID VARCHAR(20) NOT NULL,
    ASSIGNED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ASSIGNED_BY VARCHAR(20), -- Who assigned this role
    IS_PRIMARY CHAR(1) DEFAULT 'N', -- Primary role for the user
    TENANT_ID VARCHAR(20) NOT NULL,
    PRIMARY KEY (USER_ROLE_ID),
    FOREIGN KEY (USER_ID) REFERENCES USERS(ESNTL_ID) ON DELETE CASCADE,
    FOREIGN KEY (ROLE_ID) REFERENCES ROLES(ROLE_ID) ON DELETE CASCADE,
    FOREIGN KEY (TENANT_ID) REFERENCES TENANTS(TENANT_ID) ON DELETE CASCADE,
    UNIQUE (USER_ID, ROLE_ID, TENANT_ID)
);
CREATE INDEX IDX_USER_ROLES_USER ON USER_ROLES(USER_ID);
CREATE INDEX IDX_USER_ROLES_ROLE ON USER_ROLES(ROLE_ID);
CREATE INDEX IDX_USER_ROLES_TENANT ON USER_ROLES(TENANT_ID);

-- Group-Role junction table (roles assigned at group level)
CREATE TABLE GROUP_ROLES (
    GROUP_ROLE_ID BIGINT AUTO_INCREMENT,
    GROUP_ID VARCHAR(20) NOT NULL,
    ROLE_ID VARCHAR(20) NOT NULL,
    ASSIGNED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    TENANT_ID VARCHAR(20) NOT NULL,
    PRIMARY KEY (GROUP_ROLE_ID),
    FOREIGN KEY (GROUP_ID) REFERENCES GROUPS(GROUP_ID) ON DELETE CASCADE,
    FOREIGN KEY (ROLE_ID) REFERENCES ROLES(ROLE_ID) ON DELETE CASCADE,
    FOREIGN KEY (TENANT_ID) REFERENCES TENANTS(TENANT_ID) ON DELETE CASCADE,
    UNIQUE (GROUP_ID, ROLE_ID, TENANT_ID)
);
CREATE INDEX IDX_GROUP_ROLES_TENANT ON GROUP_ROLES(TENANT_ID);
```

#### 2.2.3 Menu & Permission System (Core)

```sql
-- =====================================================
-- MENU & PERMISSION SYSTEM
-- =====================================================

-- Enhanced MENUS table (modify existing)
ALTER TABLE MENUS ADD COLUMN MENU_CODE VARCHAR(50) UNIQUE; -- e.g., 'business-list'
ALTER TABLE MENUS ADD COLUMN MENU_TYPE VARCHAR(20) DEFAULT 'MENU'; -- MENU, FOLDER, LINK
ALTER TABLE MENUS ADD COLUMN MENU_PATH VARCHAR(255); -- Frontend route path
ALTER TABLE MENUS ADD COLUMN API_ENDPOINT VARCHAR(255); -- Backend API path
ALTER TABLE MENUS ADD COLUMN ICON_NAME VARCHAR(50); -- Icon for UI
ALTER TABLE MENUS ADD COLUMN IS_VISIBLE CHAR(1) DEFAULT 'Y';
ALTER TABLE MENUS ADD COLUMN IS_ACTIVE CHAR(1) DEFAULT 'Y';
ALTER TABLE MENUS ADD COLUMN CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE MENUS ADD COLUMN CREATED_BY VARCHAR(20);

CREATE INDEX IDX_MENUS_TENANT ON MENUS(TENANT_ID);
CREATE INDEX IDX_MENUS_CODE ON MENUS(MENU_CODE);
CREATE INDEX IDX_MENUS_PARENT ON MENUS(UPPER_MENU_NO);

-- Permissions table (auto-generated from menus)
CREATE TABLE PERMISSIONS (
    PERMISSION_ID BIGINT AUTO_INCREMENT,
    PERMISSION_CODE VARCHAR(100) NOT NULL, -- e.g., 'business-list'
    PERMISSION_TYPE VARCHAR(20) NOT NULL, -- 'API' or 'MENU'
    PERMISSION_ACTION VARCHAR(20) NOT NULL, -- 'READ', 'WRITE', 'DELETE', 'DOWNLOAD'
    RESOURCE_PATH VARCHAR(255), -- API path or menu path
    DESCRIPTION VARCHAR(255),
    MENU_NO BIGINT, -- FK to MENUS (source menu that generated this)
    TENANT_ID VARCHAR(20),
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CREATED_BY VARCHAR(20),
    PRIMARY KEY (PERMISSION_ID),
    FOREIGN KEY (MENU_NO) REFERENCES MENUS(MENU_NO) ON DELETE CASCADE,
    UNIQUE (PERMISSION_CODE, PERMISSION_TYPE, PERMISSION_ACTION, TENANT_ID)
);
CREATE INDEX IDX_PERMISSIONS_CODE ON PERMISSIONS(PERMISSION_CODE);
CREATE INDEX IDX_PERMISSIONS_TYPE ON PERMISSIONS(PERMISSION_TYPE);
CREATE INDEX IDX_PERMISSIONS_MENU ON PERMISSIONS(MENU_NO);
CREATE INDEX IDX_PERMISSIONS_TENANT ON PERMISSIONS(TENANT_ID);

-- Role-Permission junction table (M:N)
CREATE TABLE ROLE_PERMISSIONS (
    ROLE_PERMISSION_ID BIGINT AUTO_INCREMENT,
    ROLE_ID VARCHAR(20) NOT NULL,
    PERMISSION_ID BIGINT NOT NULL,
    GRANTED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    GRANTED_BY VARCHAR(20),
    TENANT_ID VARCHAR(20) NOT NULL,
    PRIMARY KEY (ROLE_PERMISSION_ID),
    FOREIGN KEY (ROLE_ID) REFERENCES ROLES(ROLE_ID) ON DELETE CASCADE,
    FOREIGN KEY (PERMISSION_ID) REFERENCES PERMISSIONS(PERMISSION_ID) ON DELETE CASCADE,
    FOREIGN KEY (TENANT_ID) REFERENCES TENANTS(TENANT_ID) ON DELETE CASCADE,
    UNIQUE (ROLE_ID, PERMISSION_ID, TENANT_ID)
);
CREATE INDEX IDX_ROLE_PERMS_ROLE ON ROLE_PERMISSIONS(ROLE_ID);
CREATE INDEX IDX_ROLE_PERMS_PERM ON ROLE_PERMISSIONS(PERMISSION_ID);
CREATE INDEX IDX_ROLE_PERMS_TENANT ON ROLE_PERMISSIONS(TENANT_ID);
```

#### 2.2.4 Audit & Logging

```sql
-- =====================================================
-- AUDIT & LOGGING
-- =====================================================

-- Permission usage audit log
CREATE TABLE PERMISSION_AUDIT_LOG (
    LOG_ID BIGINT AUTO_INCREMENT,
    USER_ID VARCHAR(20) NOT NULL,
    PERMISSION_ID BIGINT NOT NULL,
    ACTION VARCHAR(50), -- API call or menu access
    RESOURCE_PATH VARCHAR(255),
    ACCESS_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    IP_ADDRESS VARCHAR(45),
    USER_AGENT VARCHAR(255),
    STATUS VARCHAR(20), -- SUCCESS, DENIED
    TENANT_ID VARCHAR(20) NOT NULL,
    PRIMARY KEY (LOG_ID)
);
CREATE INDEX IDX_AUDIT_USER ON PERMISSION_AUDIT_LOG(USER_ID);
CREATE INDEX IDX_AUDIT_TENANT ON PERMISSION_AUDIT_LOG(TENANT_ID);
CREATE INDEX IDX_AUDIT_TIME ON PERMISSION_AUDIT_LOG(ACCESS_TIME);
```

### 2.3 ERD Diagram (Mermaid)

```mermaid
erDiagram
    TENANTS ||--o{ BRANCHES : contains
    TENANTS ||--o{ GROUPS : contains
    TENANTS ||--o{ POSITIONS : contains
    TENANTS ||--o{ ROLES : contains
    TENANTS ||--o{ MENUS : contains
    TENANTS ||--o{ USERS : contains

    BRANCHES ||--o{ BRANCHES : "parent-child"
    BRANCHES ||--o{ GROUPS : contains
    BRANCHES ||--o{ USERS : employs

    GROUPS ||--o{ USERS : contains
    GROUPS ||--o{ GROUP_ROLES : has

    POSITIONS ||--o{ USERS : assigned

    USERS ||--o{ USER_ROLES : has
    USERS ||--o{ USERS : "manages (manager_id)"

    ROLES ||--o{ USER_ROLES : assigned
    ROLES ||--o{ GROUP_ROLES : assigned
    ROLES ||--o{ ROLE_PERMISSIONS : grants

    MENUS ||--o{ MENUS : "parent-child"
    MENUS ||--o{ PERMISSIONS : generates

    PERMISSIONS ||--o{ ROLE_PERMISSIONS : granted
    PERMISSIONS ||--o{ PERMISSION_AUDIT_LOG : logs

    USERS ||--o{ PERMISSION_AUDIT_LOG : creates
```

---

## 3. Permission Logic - Auto-Generation Mechanism

### 3.1 Overview

When a **System Admin (Developer)** creates a new menu entry, the system automatically generates corresponding permission records. This ensures consistency and reduces manual configuration.

### 3.2 Permission Generation Rules

#### Rule 1: Menu → API Permission
For each menu with an `API_ENDPOINT`:
```
Input:  Menu { code: "business-list", apiEndpoint: "/api/v1/business" }
Output: Permission {
          code: "business-list",
          type: "API",
          action: "READ",
          resourcePath: "/api/v1/business"
        }
```

#### Rule 2: Menu → MENU Permissions
For each menu with `MENU_PATH`:
```
Input:  Menu { code: "business-list", menuPath: "/business/list" }
Output: [
          Permission { code: "business-list", type: "MENU", action: "READ" },
          Permission { code: "business-list", type: "MENU", action: "WRITE" },
          Permission { code: "business-list", type: "MENU", action: "DOWNLOAD" }
        ]
```

### 3.3 Implementation Strategy

#### 3.3.1 JPA Entity Listener Approach

```java
@Entity
@Table(name = "MENUS")
@EntityListeners(MenuPermissionGenerator.class)
public class Menu {
    // ... existing fields

    @Column(name = "MENU_CODE", unique = true, length = 50)
    private String menuCode;

    @Column(name = "API_ENDPOINT", length = 255)
    private String apiEndpoint;

    @Column(name = "MENU_PATH", length = 255)
    private String menuPath;
}

// Listener for auto-generation
@Component
public class MenuPermissionGenerator {

    @Autowired
    private PermissionRepository permissionRepository;

    @PostPersist
    @PostUpdate
    public void generatePermissions(Menu menu) {
        List<Permission> permissions = new ArrayList<>();

        // Generate API permission if apiEndpoint exists
        if (menu.getApiEndpoint() != null && !menu.getApiEndpoint().isEmpty()) {
            permissions.add(createPermission(
                menu.getMenuCode(),
                "API",
                "READ",
                menu.getApiEndpoint(),
                menu.getMenuNo(),
                menu.getTenantId()
            ));
        }

        // Generate MENU permissions if menuPath exists
        if (menu.getMenuPath() != null && !menu.getMenuPath().isEmpty()) {
            for (String action : List.of("READ", "WRITE", "DOWNLOAD")) {
                permissions.add(createPermission(
                    menu.getMenuCode(),
                    "MENU",
                    action,
                    menu.getMenuPath(),
                    menu.getMenuNo(),
                    menu.getTenantId()
                ));
            }
        }

        // Save all (with duplicate handling)
        permissions.forEach(p -> {
            if (!permissionRepository.existsByCodeAndTypeAndAction(
                p.getPermissionCode(),
                p.getPermissionType(),
                p.getPermissionAction(),
                p.getTenantId()
            )) {
                permissionRepository.save(p);
            }
        });
    }

    private Permission createPermission(String code, String type, String action,
                                       String resourcePath, Long menuNo, String tenantId) {
        Permission p = new Permission();
        p.setPermissionCode(code);
        p.setPermissionType(type);
        p.setPermissionAction(action);
        p.setResourcePath(resourcePath);
        p.setMenuNo(menuNo);
        p.setTenantId(tenantId);
        p.setDescription("Auto-generated from menu: " + code);
        return p;
    }
}
```

#### 3.3.2 Service Layer Approach (Alternative)

```java
@Service
@Transactional
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private PermissionService permissionService;

    public Menu createMenu(MenuDTO dto) {
        Menu menu = menuRepository.save(dto.toEntity());

        // Trigger permission generation
        permissionService.autoGenerateFromMenu(menu);

        return menu;
    }
}

@Service
public class PermissionService {

    public void autoGenerateFromMenu(Menu menu) {
        // Same logic as above
    }
}
```

### 3.4 Permission Deletion Strategy

When a menu is deleted:
- **Option A (Soft Delete)**: Mark permissions as inactive but retain for audit
- **Option B (Cascade Delete)**: Remove permissions (current FK CASCADE)
- **Recommended**: Soft delete with `IS_ACTIVE` flag

```java
@PreRemove
public void onMenuDelete(Menu menu) {
    permissionRepository.deactivateByMenuNo(menu.getMenuNo());
    // or permissionRepository.deleteByMenuNo(menu.getMenuNo());
}
```

### 3.5 Permission Update Strategy

When menu properties change:
- **Menu code change**: Update `PERMISSION_CODE` in related permissions
- **API endpoint change**: Update `RESOURCE_PATH` for API permissions
- **Menu path change**: Update `RESOURCE_PATH` for MENU permissions

```java
@PostUpdate
public void onMenuUpdate(Menu menu) {
    permissionRepository.updateResourcePathsByMenuNo(
        menu.getMenuNo(),
        menu.getApiEndpoint(),
        menu.getMenuPath()
    );
}
```

---

## 4. Security Architecture - Multi-Tenant RBAC

### 4.1 Multi-Tenant Filtering Strategy

#### 4.1.1 Database-Level Row-Level Security (RLS)

**Option A: PostgreSQL RLS Policies**
```sql
-- Enable RLS on all tenant-aware tables
ALTER TABLE USERS ENABLE ROW LEVEL SECURITY;
ALTER TABLE ROLES ENABLE ROW LEVEL SECURITY;
ALTER TABLE MENUS ENABLE ROW LEVEL SECURITY;

-- Create policy (requires setting current tenant in session)
CREATE POLICY tenant_isolation_policy ON USERS
    USING (TENANT_ID = current_setting('app.current_tenant_id')::VARCHAR);
```

**Option B: Application-Level Tenant Filter (Recommended for eGovFrame)**

Using Hibernate filters:

```java
@Entity
@Table(name = "USERS")
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = String.class))
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
public class User {
    // ... fields
}

// Enable filter globally
@Component
public class TenantFilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Around("execution(* com.example.egov.domain..*Repository+.*(..))")
    public Object enableTenantFilter(ProceedingJoinPoint joinPoint) throws Throwable {
        Session session = entityManager.unwrap(Session.class);

        String tenantId = TenantContext.getCurrentTenantId();
        if (tenantId != null) {
            Filter filter = session.enableFilter("tenantFilter");
            filter.setParameter("tenantId", tenantId);
        }

        return joinPoint.proceed();
    }
}

// Thread-local context for tenant ID
public class TenantContext {
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static void setCurrentTenantId(String tenantId) {
        currentTenant.set(tenantId);
    }

    public static String getCurrentTenantId() {
        return currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
    }
}
```

#### 4.1.2 Tenant Context Resolution

Extract tenant ID from authenticated user:

```java
@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            TenantContext.setCurrentTenantId(userDetails.getTenantId());
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                               HttpServletResponse response,
                               Object handler, Exception ex) {
        TenantContext.clear();
    }
}

// Register interceptor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private TenantInterceptor tenantInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantInterceptor);
    }
}
```

### 4.2 Spring Security RBAC Integration

#### 4.2.1 Custom UserDetailsService

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Load all roles for this user
        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());

        // Load all permissions from all roles
        Set<Permission> permissions = new HashSet<>();
        for (UserRole userRole : userRoles) {
            List<RolePermission> rolePerms = rolePermissionRepository
                .findByRoleId(userRole.getRoleId());
            rolePerms.forEach(rp -> permissions.add(rp.getPermission()));
        }

        // Convert to GrantedAuthority
        List<GrantedAuthority> authorities = permissions.stream()
            .map(p -> new SimpleGrantedAuthority(
                p.getPermissionType() + ":" + p.getPermissionCode() + ":" + p.getPermissionAction()
            ))
            .collect(Collectors.toList());

        return new CustomUserDetails(
            user.getUserId(),
            user.getPassword(),
            user.getTenantId(),
            authorities
        );
    }
}

public class CustomUserDetails implements UserDetails {
    private String username;
    private String password;
    private String tenantId;
    private Collection<? extends GrantedAuthority> authorities;

    // Constructor, getters

    public String getTenantId() {
        return tenantId;
    }
}
```

#### 4.2.2 Method-Level Security

```java
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig {
    // Enables @PreAuthorize, @PostAuthorize annotations
}

// Usage in controllers/services
@RestController
@RequestMapping("/api/v1/business")
public class BusinessController {

    @GetMapping
    @PreAuthorize("hasAuthority('API:business-list:READ')")
    public ResponseEntity<List<BusinessDTO>> getBusinessList() {
        // Only accessible if user has the specific permission
    }

    @PostMapping
    @PreAuthorize("hasAuthority('API:business-create:WRITE')")
    public ResponseEntity<BusinessDTO> createBusiness(@RequestBody BusinessDTO dto) {
        // ...
    }
}
```

#### 4.2.3 Dynamic Authorization with Custom Expression

```java
@Component("permissionEvaluator")
public class CustomPermissionEvaluator {

    public boolean hasMenuAccess(String menuCode, String action) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("MENU:" + menuCode + ":" + action));
    }

    public boolean hasApiAccess(String apiCode, String action) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("API:" + apiCode + ":" + action));
    }
}

// Usage
@PreAuthorize("@permissionEvaluator.hasApiAccess('business-list', 'READ')")
public List<BusinessDTO> getList() { }
```

### 4.3 Security Configuration (eGovFrame 5.0)

```java
@Configuration
@EnableWebSecurity
public class EgovSecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**") // API endpoints
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**", "/login", "/swagger-ui/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard")
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 4.4 Tenant Isolation Validation

Create a test suite to ensure tenant isolation:

```java
@SpringBootTest
public class TenantIsolationTest {

    @Test
    public void userFromTenantA_CannotAccessDataFromTenantB() {
        // Authenticate as Tenant A user
        // Try to query Tenant B data
        // Assert: Should return empty or throw exception
    }

    @Test
    public void tenantAdminCanOnlyManageOwnTenantRoles() {
        // Assert role assignment is scoped to tenant
    }
}
```

---

## 5. Frontend Integration - React 19.2 + shadcn/ui

### 5.1 Authentication & User Context

#### 5.1.1 Authentication API Integration

```typescript
// src/lib/auth.ts
export interface User {
  userId: string;
  username: string;
  tenantId: string;
  permissions: string[]; // e.g., ["MENU:business-list:READ", "API:business-list:READ"]
  roles: string[];
}

export async function login(username: string, password: string): Promise<User> {
  const response = await fetch('/api/v1/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  });

  if (!response.ok) throw new Error('Login failed');

  const data = await response.json();

  // Store token
  localStorage.setItem('authToken', data.token);

  return data.user;
}

export async function fetchCurrentUser(): Promise<User> {
  const response = await fetch('/api/v1/auth/me', {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('authToken')}` },
  });

  if (!response.ok) throw new Error('Failed to fetch user');

  return response.json();
}
```

#### 5.1.2 React Context for User & Permissions

```typescript
// src/context/AuthContext.tsx
import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { User, fetchCurrentUser } from '@/lib/auth';

interface AuthContextType {
  user: User | null;
  loading: boolean;
  hasPermission: (permission: string) => boolean;
  hasMenuAccess: (menuCode: string, action?: string) => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchCurrentUser()
      .then(setUser)
      .catch(() => setUser(null))
      .finally(() => setLoading(false));
  }, []);

  const hasPermission = (permission: string): boolean => {
    return user?.permissions.includes(permission) ?? false;
  };

  const hasMenuAccess = (menuCode: string, action: string = 'READ'): boolean => {
    return hasPermission(`MENU:${menuCode}:${action}`);
  };

  return (
    <AuthContext.Provider value={{ user, loading, hasPermission, hasMenuAccess }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
};
```

### 5.2 Dynamic Sidebar with Menu Permissions

#### 5.2.1 Fetch Menu Structure from Backend

```typescript
// src/lib/menu.ts
export interface MenuItem {
  menuNo: number;
  menuCode: string;
  menuName: string;
  menuPath?: string;
  iconName?: string;
  upperMenuNo?: number;
  menuOrder: number;
  children?: MenuItem[];
}

export async function fetchUserMenus(): Promise<MenuItem[]> {
  const response = await fetch('/api/v1/menus/user-menus', {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('authToken')}` },
  });

  if (!response.ok) throw new Error('Failed to fetch menus');

  const menus: MenuItem[] = await response.json();

  // Build tree structure
  return buildMenuTree(menus);
}

function buildMenuTree(menus: MenuItem[]): MenuItem[] {
  const menuMap = new Map<number, MenuItem>();
  const roots: MenuItem[] = [];

  // First pass: create map
  menus.forEach(menu => {
    menuMap.set(menu.menuNo, { ...menu, children: [] });
  });

  // Second pass: build tree
  menus.forEach(menu => {
    const node = menuMap.get(menu.menuNo)!;
    if (menu.upperMenuNo) {
      const parent = menuMap.get(menu.upperMenuNo);
      parent?.children?.push(node);
    } else {
      roots.push(node);
    }
  });

  // Sort by menuOrder
  const sortMenus = (items: MenuItem[]) => {
    items.sort((a, b) => a.menuOrder - b.menuOrder);
    items.forEach(item => item.children && sortMenus(item.children));
  };
  sortMenus(roots);

  return roots;
}
```

#### 5.2.2 Backend API for User Menus

```java
@RestController
@RequestMapping("/api/v1/menus")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/user-menus")
    public ResponseEntity<List<MenuDTO>> getUserMenus(Authentication auth) {
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        // Get all menus user has READ permission for
        List<Menu> accessibleMenus = menuService.getAccessibleMenusForUser(
            userDetails.getUsername(),
            userDetails.getTenantId()
        );

        return ResponseEntity.ok(
            accessibleMenus.stream()
                .map(MenuDTO::from)
                .collect(Collectors.toList())
        );
    }
}

@Service
public class MenuService {

    public List<Menu> getAccessibleMenusForUser(String userId, String tenantId) {
        // 1. Get user's roles
        // 2. Get permissions from those roles
        // 3. Filter MENU permissions with READ action
        // 4. Get corresponding menus
        // 5. Return only IS_VISIBLE = 'Y' and IS_ACTIVE = 'Y'

        return menuRepository.findAccessibleMenus(userId, tenantId);
    }
}

// Custom query
@Query("""
    SELECT DISTINCT m FROM Menu m
    WHERE m.tenantId = :tenantId
    AND m.isVisible = 'Y'
    AND m.isActive = 'Y'
    AND EXISTS (
        SELECT 1 FROM Permission p
        JOIN RolePermission rp ON p.permissionId = rp.permissionId
        JOIN UserRole ur ON rp.roleId = ur.roleId
        WHERE ur.userId = :userId
        AND p.menuNo = m.menuNo
        AND p.permissionType = 'MENU'
        AND p.permissionAction = 'READ'
    )
    ORDER BY m.menuOrder ASC
""")
List<Menu> findAccessibleMenus(@Param("userId") String userId,
                               @Param("tenantId") String tenantId);
```

#### 5.2.3 Sidebar Component with shadcn/ui

```typescript
// src/components/Sidebar.tsx
import { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { ChevronDown, ChevronRight } from 'lucide-react';
import { fetchUserMenus, MenuItem } from '@/lib/menu';
import { useAuth } from '@/context/AuthContext';
import { cn } from '@/lib/utils';
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarMenuSub,
  SidebarProvider,
} from '@/components/ui/sidebar';

export function AppSidebar() {
  const [menus, setMenus] = useState<MenuItem[]>([]);
  const { hasMenuAccess } = useAuth();
  const location = useLocation();

  useEffect(() => {
    fetchUserMenus().then(setMenus);
  }, []);

  return (
    <Sidebar>
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>Navigation</SidebarGroupLabel>
          <SidebarMenu>
            {menus.map(menu => (
              <SidebarMenuItem key={menu.menuNo}>
                <MenuItemRecursive menu={menu} currentPath={location.pathname} />
              </SidebarMenuItem>
            ))}
          </SidebarMenu>
        </SidebarGroup>
      </SidebarContent>
    </Sidebar>
  );
}

function MenuItemRecursive({ menu, currentPath }: { menu: MenuItem; currentPath: string }) {
  const [isOpen, setIsOpen] = useState(false);
  const hasChildren = menu.children && menu.children.length > 0;
  const isActive = currentPath === menu.menuPath;

  if (hasChildren) {
    return (
      <>
        <SidebarMenuButton
          onClick={() => setIsOpen(!isOpen)}
          className={cn(isActive && 'bg-accent')}
        >
          {menu.iconName && <span className="mr-2">{menu.iconName}</span>}
          <span>{menu.menuName}</span>
          {isOpen ? <ChevronDown className="ml-auto" /> : <ChevronRight className="ml-auto" />}
        </SidebarMenuButton>

        {isOpen && (
          <SidebarMenuSub>
            {menu.children.map(child => (
              <SidebarMenuItem key={child.menuNo}>
                <MenuItemRecursive menu={child} currentPath={currentPath} />
              </SidebarMenuItem>
            ))}
          </SidebarMenuSub>
        )}
      </>
    );
  }

  return (
    <SidebarMenuButton asChild isActive={isActive}>
      <Link to={menu.menuPath || '#'}>
        {menu.iconName && <span className="mr-2">{menu.iconName}</span>}
        <span>{menu.menuName}</span>
      </Link>
    </SidebarMenuButton>
  );
}
```

#### 5.2.4 Protected Route Component

```typescript
// src/components/ProtectedRoute.tsx
import { Navigate } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';

interface ProtectedRouteProps {
  children: React.ReactNode;
  menuCode?: string;
  requiredPermission?: string;
}

export function ProtectedRoute({
  children,
  menuCode,
  requiredPermission
}: ProtectedRouteProps) {
  const { user, loading, hasMenuAccess, hasPermission } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // Check menu access
  if (menuCode && !hasMenuAccess(menuCode)) {
    return <Navigate to="/403" replace />;
  }

  // Check specific permission
  if (requiredPermission && !hasPermission(requiredPermission)) {
    return <Navigate to="/403" replace />;
  }

  return <>{children}</>;
}

// Usage in routes
import { BrowserRouter, Routes, Route } from 'react-router-dom';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />

          <Route path="/business/list" element={
            <ProtectedRoute menuCode="business-list">
              <BusinessListPage />
            </ProtectedRoute>
          } />

          <Route path="/admin/roles" element={
            <ProtectedRoute requiredPermission="MENU:admin-roles:READ">
              <RolesManagementPage />
            </ProtectedRoute>
          } />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
```

### 5.3 Action-Level UI Control

Hide/disable buttons based on permissions:

```typescript
// src/pages/BusinessListPage.tsx
import { useAuth } from '@/context/AuthContext';
import { Button } from '@/components/ui/button';

export function BusinessListPage() {
  const { hasPermission } = useAuth();

  const canWrite = hasPermission('MENU:business-list:WRITE');
  const canDownload = hasPermission('MENU:business-list:DOWNLOAD');

  return (
    <div>
      <h1>Business List</h1>

      {canWrite && (
        <Button onClick={handleCreate}>Create New</Button>
      )}

      {canDownload && (
        <Button onClick={handleDownload}>Download CSV</Button>
      )}

      {/* List table */}
    </div>
  );
}
```

---

## 6. Implementation Roadmap

### Phase 1: Database Schema Setup (Week 1-2)
- [ ] Execute SQL migrations for new tables (BRANCHES, GROUPS, POSITIONS, etc.)
- [ ] Update existing tables (USERS, MENUS alterations)
- [ ] Create indexes and foreign keys
- [ ] Seed initial data (system admin, default roles)

### Phase 2: Backend - Domain & Repository (Week 2-3)
- [ ] Create JPA entities for new tables
- [ ] Update existing entities (User, Menu, Role)
- [ ] Implement repositories with custom queries
- [ ] Add Hibernate tenant filter configuration

### Phase 3: Permission Auto-Generation (Week 3-4)
- [ ] Implement MenuPermissionGenerator listener
- [ ] Create PermissionService with auto-generation logic
- [ ] Add permission update/delete handling
- [ ] Write unit tests for permission generation

### Phase 4: Security & RBAC (Week 4-6)
- [ ] Implement TenantInterceptor and TenantContext
- [ ] Update CustomUserDetailsService with multi-role support
- [ ] Configure method-level security
- [ ] Add permission-based authorization
- [ ] Test tenant isolation thoroughly

### Phase 5: Backend APIs (Week 6-8)
- [ ] MenuController - user-accessible menus endpoint
- [ ] RoleController - role management (tenant admin)
- [ ] PermissionController - permission assignment
- [ ] UserController - user management with role assignment
- [ ] Audit logging endpoints

### Phase 6: Frontend - Auth & Context (Week 8-9)
- [ ] Implement AuthContext and authentication flow
- [ ] Create login/logout functionality
- [ ] Setup protected routes
- [ ] Add permission checking hooks

### Phase 7: Frontend - Dynamic Sidebar (Week 9-10)
- [ ] Implement menu fetching logic
- [ ] Build recursive Sidebar component with shadcn/ui
- [ ] Add menu tree rendering
- [ ] Implement active state and navigation

### Phase 8: Frontend - Permission-Based UI (Week 10-11)
- [ ] Add action-level permission checks
- [ ] Implement conditional rendering for buttons/actions
- [ ] Create reusable permission wrapper components
- [ ] Test all permission scenarios

### Phase 9: Testing & Optimization (Week 11-12)
- [ ] Write integration tests for tenant isolation
- [ ] Performance testing for permission checks
- [ ] Security audit
- [ ] Load testing with multiple tenants

### Phase 10: Documentation & Deployment (Week 12-13)
- [ ] API documentation (Swagger)
- [ ] Admin user guide
- [ ] Developer documentation
- [ ] Production deployment

---

## 7. Key Technical Decisions

### 7.1 Why Application-Level Tenant Filtering?
- **Pros**: Framework-agnostic, easier testing, explicit control
- **Cons**: Requires discipline to apply consistently
- **Alternative**: PostgreSQL RLS (harder to debug, performance overhead)

### 7.2 Why JPA Entity Listeners for Permission Generation?
- **Pros**: Automatic, declarative, tied to entity lifecycle
- **Cons**: Less explicit, harder to debug
- **Alternative**: Service layer (more explicit, easier to test)

### 7.3 Why Multiple Role Assignment?
- **Requirement**: Users need different permissions in different contexts
- **Example**: User is "Sales Rep" + "Report Viewer" + "Branch Manager"
- **Implementation**: USER_ROLES junction table

### 7.4 Why Menu-Driven Permissions?
- **Consistency**: Single source of truth (menu structure)
- **Efficiency**: No manual permission creation
- **Risk**: Deletion of menu impacts permissions

---

## 8. Security Checklist

- [ ] All queries include tenant_id in WHERE clause
- [ ] TenantInterceptor applies to all requests
- [ ] UserDetailsService loads all user roles + permissions
- [ ] Method security annotations on all endpoints
- [ ] CSRF protection for state-changing operations
- [ ] Input validation on all DTOs
- [ ] Password encryption (BCrypt)
- [ ] Session timeout configured
- [ ] Audit logging for permission changes
- [ ] Regular security scanning

---

## 9. API Contracts

### 9.1 Authentication

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "john.doe",
  "password": "securePassword123"
}

Response 200:
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "user": {
    "userId": "john.doe",
    "username": "John Doe",
    "tenantId": "TENANT001",
    "permissions": ["MENU:business-list:READ", "API:business-list:READ"],
    "roles": ["SALES_REP", "REPORT_VIEWER"]
  }
}
```

### 9.2 Fetch User Menus

```http
GET /api/v1/menus/user-menus
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...

Response 200:
[
  {
    "menuNo": 1,
    "menuCode": "dashboard",
    "menuName": "Dashboard",
    "menuPath": "/dashboard",
    "iconName": "LayoutDashboard",
    "menuOrder": 1,
    "children": []
  },
  {
    "menuNo": 2,
    "menuCode": "business",
    "menuName": "Business",
    "menuPath": null,
    "iconName": "Briefcase",
    "menuOrder": 2,
    "children": [
      {
        "menuNo": 3,
        "menuCode": "business-list",
        "menuName": "Business List",
        "menuPath": "/business/list",
        "menuOrder": 1
      }
    ]
  }
]
```

### 9.3 Create Menu (Admin)

```http
POST /api/v1/menus
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
Content-Type: application/json

{
  "menuCode": "customer-create",
  "menuName": "Create Customer",
  "menuPath": "/customers/create",
  "apiEndpoint": "/api/v1/customers",
  "iconName": "UserPlus",
  "upperMenuNo": 5,
  "menuOrder": 2
}

Response 201:
{
  "menuNo": 15,
  "menuCode": "customer-create",
  "generatedPermissions": [
    {
      "permissionId": 45,
      "permissionCode": "customer-create",
      "permissionType": "API",
      "permissionAction": "READ"
    },
    {
      "permissionId": 46,
      "permissionCode": "customer-create",
      "permissionType": "MENU",
      "permissionAction": "READ"
    },
    {
      "permissionId": 47,
      "permissionCode": "customer-create",
      "permissionType": "MENU",
      "permissionAction": "WRITE"
    }
  ]
}
```

---

## 10. Appendix

### 10.1 Sample Data for Testing

```sql
-- Tenant
INSERT INTO TENANTS (TENANT_ID, TENANT_NAME, USE_AT)
VALUES ('T001', 'Acme Corporation', 'Y');

-- Branch
INSERT INTO BRANCHES (BRANCH_ID, BRANCH_NAME, BRANCH_CODE, TENANT_ID, USE_AT)
VALUES ('B001', 'Headquarters', 'HQ', 'T001', 'Y');

-- Group
INSERT INTO GROUPS (GROUP_ID, GROUP_NAME, GROUP_CODE, BRANCH_ID, TENANT_ID, USE_AT)
VALUES ('G001', 'Sales Department', 'SALES', 'B001', 'T001', 'Y');

-- Position
INSERT INTO POSITIONS (POSITION_ID, POSITION_NAME, POSITION_CODE, POSITION_LEVEL, TENANT_ID, USE_AT)
VALUES ('P001', 'Sales Manager', 'SALES_MGR', 2, 'T001', 'Y');

-- Role
INSERT INTO ROLES (ROLE_ID, ROLE_NAME, ROLE_DESCRIPTION, TENANT_ID)
VALUES ('R001', 'SALES_MANAGER', 'Sales Manager Role', 'T001');

-- Menu
INSERT INTO MENUS (MENU_NO, MENU_CODE, MENU_NAME, MENU_PATH, API_ENDPOINT, MENU_ORDER, IS_VISIBLE, IS_ACTIVE, TENANT_ID)
VALUES (1, 'business-list', 'Business List', '/business/list', '/api/v1/business', 1, 'Y', 'Y', 'T001');

-- Permissions (auto-generated by listener, but shown here for clarity)
INSERT INTO PERMISSIONS (PERMISSION_CODE, PERMISSION_TYPE, PERMISSION_ACTION, RESOURCE_PATH, MENU_NO, TENANT_ID)
VALUES
('business-list', 'API', 'READ', '/api/v1/business', 1, 'T001'),
('business-list', 'MENU', 'READ', '/business/list', 1, 'T001'),
('business-list', 'MENU', 'WRITE', '/business/list', 1, 'T001');

-- Assign permission to role
INSERT INTO ROLE_PERMISSIONS (ROLE_ID, PERMISSION_ID, TENANT_ID)
VALUES ('R001', 1, 'T001'), ('R001', 2, 'T001');

-- User
INSERT INTO USERS (ESNTL_ID, USER_ID, PASSWORD, USER_NAME, GROUP_ID, BRANCH_ID, POSITION_ID, TENANT_ID)
VALUES ('U001', 'john.doe', '$2a$10$...', 'John Doe', 'G001', 'B001', 'P001', 'T001');

-- Assign role to user
INSERT INTO USER_ROLES (USER_ID, ROLE_ID, IS_PRIMARY, TENANT_ID)
VALUES ('U001', 'R001', 'Y', 'T001');
```

### 10.2 Performance Considerations

- **Indexing**: All FK columns and frequently queried columns indexed
- **Query Optimization**: Use QueryDSL for complex queries
- **Caching**: Consider caching user permissions (Redis/Caffeine)
- **N+1 Prevention**: Use JOIN FETCH in JPA queries
- **Connection Pooling**: Configure HikariCP appropriately

### 10.3 Future Enhancements

- **Dynamic Permission Actions**: Allow custom actions beyond READ/WRITE/DOWNLOAD
- **Time-Based Permissions**: Assign roles with expiration dates
- **Data-Level Permissions**: Row-level filtering (e.g., user can only see their branch's data)
- **Permission Templates**: Preset permission bundles for common roles
- **Audit Dashboard**: Real-time permission usage analytics

---

## Document Version

- **Version**: 1.0
- **Date**: 2026-01-22
- **Author**: CRM Project Team
- **Status**: Ready for Implementation

---

**Next Steps**: Review this specification with the team, finalize any open questions, and proceed with Phase 1 implementation.
