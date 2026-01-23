# Feature: User & Role Management

## 1. User Entity Rules
- **No Direct Roles**: Users do NOT have a `ROLE_ID` column.
- **Context**: Must belong to Branch, Position, and optionally Report to a Manager.
- **Multi-Role**: Users are assigned roles via `USER_ROLES` table.

## 2. Schema Definitions

### Enhanced Users
```sql
CREATE TABLE USERS (
    ESNTL_ID VARCHAR(20) PRIMARY KEY, -- USER_ID
    BRANCH_ID VARCHAR(20),
    POSITION_ID VARCHAR(20),
    MANAGER_ID VARCHAR(20),
    TENANT_ID VARCHAR(20) NOT NULL
    -- ... basic fields (password, name)
);
```

### User Roles (M:N)
```sql
CREATE TABLE USER_ROLES (
    USER_ROLE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    USER_ID VARCHAR(20) NOT NULL,
    ROLE_ID VARCHAR(20) NOT NULL,
    IS_PRIMARY CHAR(1) DEFAULT 'N',
    TENANT_ID VARCHAR(20) NOT NULL,
    UNIQUE (USER_ID, ROLE_ID, TENANT_ID)
);
```

### Group Roles
Roles can also be assigned to Groups, implying default roles for members.
```sql
CREATE TABLE GROUP_ROLES (
    GROUP_ROLE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    GROUP_ID VARCHAR(20) NOT NULL,
    ROLE_ID VARCHAR(20) NOT NULL
);
```
