# Feature: Organization Architecture

## 1. Hierarchy Rules
- **Structure**: Tenant → Branch → Group → Position → User.
- **Branches**: Physical locations. Supports hierarchy (Parent Branch).
- **Groups**: Departments/Teams. Belongs to a Branch.
- **Positions**: Job roles with levels (e.g., Manager, Staff).

## 2. Schema Definitions

### Branches
```sql
CREATE TABLE BRANCHES (
    BRANCH_ID VARCHAR(20) PRIMARY KEY,
    PARENT_BRANCH_ID VARCHAR(20), -- Recursive hierarchy
    TENANT_ID VARCHAR(20) NOT NULL
    -- ... basic fields
);
```

### Groups
```sql
CREATE TABLE GROUPS (
    GROUP_ID VARCHAR(20) PRIMARY KEY,
    BRANCH_ID VARCHAR(20) NOT NULL,
    TENANT_ID VARCHAR(20) NOT NULL,
    UNIQUE (GROUP_CODE, TENANT_ID)
);
```

### Positions
```sql
CREATE TABLE POSITIONS (
    POSITION_ID VARCHAR(20) PRIMARY KEY,
    POSITION_LEVEL INT DEFAULT 0, -- 0=Highest
    TENANT_ID VARCHAR(20) NOT NULL
);
```
