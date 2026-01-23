# Feature: Permission & Menu System

## 1. Menu-Driven Permission Auto-Generation
**Rule**: System automatically generates permissions when a Menu is created/updated.

### Mechanism
- **Trigger**: JPA Entity Listener (`@PostPersist`, `@PostUpdate`) on `Menu` entity.
- **Naming Convention**:
    - **API**: `API:{menuCode}:{action}` (Action: READ, WRITE, etc.)
    - **MENU**: `MENU:{menuCode}:{action}` (Action: READ)

### Rules
1. **Menu → API**: If `apiEndpoint` exists, create `API` type permission.
2. **Menu → UI**: ALWAYS create `MENU` type permission for UI access control.
3. **Synchronization**: Updates to Menu Code/Paths must propagate to Permissions.

## 2. Schema Definitions

### Menus
```sql
CREATE TABLE MENUS (
    MENU_NO BIGINT AUTO_INCREMENT PRIMARY KEY,
    MENU_CODE VARCHAR(50) UNIQUE,
    MENU_PATH VARCHAR(255),
    API_ENDPOINT VARCHAR(255),
    IS_VISIBLE CHAR(1) DEFAULT 'Y',
    TENANT_ID VARCHAR(20) NOT NULL
);
```

### Permissions (Auto-Generated)
```sql
CREATE TABLE PERMISSIONS (
    PERMISSION_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    PERMISSION_CODE VARCHAR(100) NOT NULL,
    PERMISSION_TYPE VARCHAR(20) NOT NULL, -- 'API', 'MENU'
    PERMISSION_ACTION VARCHAR(20) NOT NULL,
    RESOURCE_PATH VARCHAR(255),
    MENU_NO BIGINT, -- Source menu
    UNIQUE (PERMISSION_CODE, PERMISSION_TYPE, PERMISSION_ACTION, TENANT_ID)
);
```

### Role-Permissions (M:N)
```sql
CREATE TABLE ROLE_PERMISSIONS (
    ROLE_ID VARCHAR(20) NOT NULL,
    PERMISSION_ID BIGINT NOT NULL,
    TENANT_ID VARCHAR(20) NOT NULL,
    UNIQUE (ROLE_ID, PERMISSION_ID, TENANT_ID)
);
```

## 3. Audit Logging
Record all permission checks (Access/Denial).

```sql
CREATE TABLE PERMISSION_AUDIT_LOG (
    LOG_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    USER_ID VARCHAR(20),
    PERMISSION_ID BIGINT,
    ACTION VARCHAR(50),
    STATUS VARCHAR(20), -- SUCCESS, DENIED
    ACCESS_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
