# CRM Project - Back-end Specification

## 1. Schema Design (PostgreSQL ERD)

### 1.1 Core Entity Overview

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

### 1.2 Detailed Table Schemas

#### 1.2.1 Tenant & Organization Tables

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

#### 1.2.2 User & Role Management (Enhanced)

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

#### 1.2.3 Menu & Permission System (Core)

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

#### 1.2.4 Audit & Logging

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

### 1.3 ERD Diagram (Mermaid)

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

## 2. Permission Logic - Auto-Generation Mechanism

### 2.1 Overview

When a **System Admin (Developer)** creates a new menu entry, the system automatically generates corresponding permission records. This ensures consistency and reduces manual configuration.

### 2.2 Permission Generation Rules

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

### 2.3 Implementation Strategy

#### 2.3.1 JPA Entity Listener Approach

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

### 2.4 Permission Deletion Strategy

When a menu is deleted:
- **Option A (Soft Delete)**: Mark permissions as inactive but retain for audit
- **Option B (Cascade Delete)**: Remove permissions (current FK CASCADE)
- **Recommended**: Soft delete with `IS_ACTIVE` flag

### 2.5 Permission Update Strategy

When menu properties change:
- **Menu code change**: Update `PERMISSION_CODE` in related permissions
- **API endpoint change**: Update `RESOURCE_PATH` for API permissions
- **Menu path change**: Update `RESOURCE_PATH` for MENU permissions

---

## 3. Security Architecture - Multi-Tenant RBAC

### 3.1 Multi-Tenant Filtering Strategy

#### 3.1.1 Application-Level Tenant Filter (Recommended via Hibernate)

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
```

#### 3.1.2 Tenant Context Resolution

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
}
```

### 3.2 Spring Security RBAC Integration

#### 3.2.1 Custom UserDetailsService

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
```

#### 3.2.2 Dynamic Authorization with Custom Expression

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

### 3.3 Security Configuration (eGovFrame 5.0)

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
}
```

---

## 4. API Contracts

### 4.1 Authentication

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

### 4.2 Fetch User Menus

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

### 4.3 Create Menu (Admin)

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
