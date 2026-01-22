-- =====================================================
-- CRM Project - Initial Seed Data
-- Version: V2
-- Description: Seeds system tenant, admin user, and initial menu/role structure
-- Author: CRM Project Team
-- Date: 2026-01-22
-- =====================================================

-- =====================================================
-- 1. SYSTEM TENANT
-- =====================================================

INSERT INTO TENANTS (TENANT_ID, TENANT_NAME, TENANT_DESCRIPTION, USE_AT, CREATED_DATE)
VALUES ('SYSTEM', 'System Tenant', 'Default system tenant for administrative purposes', 'Y', CURRENT_TIMESTAMP);

-- =====================================================
-- 2. ORGANIZATIONAL STRUCTURE (SYSTEM)
-- =====================================================

-- System Branch
INSERT INTO BRANCHES (BRANCH_ID, BRANCH_NAME, BRANCH_CODE, BRANCH_ADDRESS, TENANT_ID, USE_AT, CREATED_DATE)
VALUES ('SYS_BR001', 'System Headquarters', 'SYS_HQ', 'System Administrative Office', 'SYSTEM', 'Y', CURRENT_TIMESTAMP);

-- System Group
INSERT INTO GROUPS (GROUP_ID, GROUP_NAME, GROUP_CODE, GROUP_DESCRIPTION, BRANCH_ID, TENANT_ID, USE_AT, CREATED_DATE)
VALUES ('SYS_GRP001', 'System Administrators', 'SYS_ADMIN', 'System administrator group', 'SYS_BR001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP);

-- System Position
INSERT INTO POSITIONS (POSITION_ID, POSITION_NAME, POSITION_CODE, POSITION_LEVEL, POSITION_DESCRIPTION, TENANT_ID, USE_AT, CREATED_DATE)
VALUES ('SYS_POS001', 'System Administrator', 'SYS_ADMIN', 0, 'Highest level system administrator', 'SYSTEM', 'Y', CURRENT_TIMESTAMP);

-- =====================================================
-- 3. ROLES (SYSTEM)
-- =====================================================

-- Super Admin Role (full system access)
INSERT INTO ROLES (ROLE_ID, ROLE_NAME, ROLE_DESCRIPTION, ROLE_TYPE, TENANT_ID, CREATED_DATE)
VALUES ('ROLE_SUPER_ADMIN', 'Super Administrator', 'Full system access with all permissions', 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP);

-- Tenant Admin Role (tenant-level administration)
INSERT INTO ROLES (ROLE_ID, ROLE_NAME, ROLE_DESCRIPTION, ROLE_TYPE, TENANT_ID, CREATED_DATE)
VALUES ('ROLE_TENANT_ADMIN', 'Tenant Administrator', 'Tenant-level administrative access', 'TENANT', 'SYSTEM', CURRENT_TIMESTAMP);

-- User Role (basic user access)
INSERT INTO ROLES (ROLE_ID, ROLE_NAME, ROLE_DESCRIPTION, ROLE_TYPE, TENANT_ID, CREATED_DATE)
VALUES ('ROLE_USER', 'User', 'Basic user access', 'USER', 'SYSTEM', CURRENT_TIMESTAMP);

-- =====================================================
-- 4. MENUS (SYSTEM)
-- =====================================================

-- Dashboard (Root Menu)
INSERT INTO MENUS (MENU_CODE, MENU_NAME, MENU_TYPE, MENU_PATH, API_ENDPOINT, ICON_NAME, MENU_ORDER, IS_VISIBLE, IS_ACTIVE, TENANT_ID, CREATED_DATE, CREATED_BY)
VALUES ('dashboard', 'Dashboard', 'MENU', '/dashboard', '/api/v1/dashboard', 'LayoutDashboard', 1, 'Y', 'Y', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM');

-- Administration (Folder)
INSERT INTO MENUS (MENU_CODE, MENU_NAME, MENU_TYPE, MENU_PATH, ICON_NAME, MENU_ORDER, IS_VISIBLE, IS_ACTIVE, TENANT_ID, CREATED_DATE, CREATED_BY)
VALUES ('admin', 'Administration', 'FOLDER', NULL, 'Settings', 2, 'Y', 'Y', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM');

-- User Management (Sub-menu under Administration)
INSERT INTO MENUS (MENU_CODE, MENU_NAME, MENU_TYPE, MENU_PATH, API_ENDPOINT, ICON_NAME, UPPER_MENU_NO, MENU_ORDER, IS_VISIBLE, IS_ACTIVE, TENANT_ID, CREATED_DATE, CREATED_BY)
VALUES ('admin-users', 'User Management', 'MENU', '/admin/users', '/api/v1/users', 'Users', (SELECT MENU_NO FROM MENUS WHERE MENU_CODE = 'admin'), 1, 'Y', 'Y', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM');

-- Role Management (Sub-menu under Administration)
INSERT INTO MENUS (MENU_CODE, MENU_NAME, MENU_TYPE, MENU_PATH, API_ENDPOINT, ICON_NAME, UPPER_MENU_NO, MENU_ORDER, IS_VISIBLE, IS_ACTIVE, TENANT_ID, CREATED_DATE, CREATED_BY)
VALUES ('admin-roles', 'Role Management', 'MENU', '/admin/roles', '/api/v1/roles', 'Shield', (SELECT MENU_NO FROM MENUS WHERE MENU_CODE = 'admin'), 2, 'Y', 'Y', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM');

-- Menu Management (Sub-menu under Administration)
INSERT INTO MENUS (MENU_CODE, MENU_NAME, MENU_TYPE, MENU_PATH, API_ENDPOINT, ICON_NAME, UPPER_MENU_NO, MENU_ORDER, IS_VISIBLE, IS_ACTIVE, TENANT_ID, CREATED_DATE, CREATED_BY)
VALUES ('admin-menus', 'Menu Management', 'MENU', '/admin/menus', '/api/v1/menus', 'Menu', (SELECT MENU_NO FROM MENUS WHERE MENU_CODE = 'admin'), 3, 'Y', 'Y', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM');

-- =====================================================
-- 5. PERMISSIONS (AUTO-GENERATED FROM MENUS)
-- =====================================================

-- Dashboard Permissions
INSERT INTO PERMISSIONS (PERMISSION_CODE, PERMISSION_TYPE, PERMISSION_ACTION, RESOURCE_PATH, MENU_NO, TENANT_ID, CREATED_DATE, CREATED_BY)
VALUES
('dashboard', 'API', 'READ', '/api/v1/dashboard', (SELECT MENU_NO FROM MENUS WHERE MENU_CODE = 'dashboard'), 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('dashboard', 'MENU', 'READ', '/dashboard', (SELECT MENU_NO FROM MENUS WHERE MENU_CODE = 'dashboard'), 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM');

-- User Management Permissions
INSERT INTO PERMISSIONS (PERMISSION_CODE, PERMISSION_TYPE, PERMISSION_ACTION, RESOURCE_PATH, MENU_NO, TENANT_ID, CREATED_DATE, CREATED_BY)
VALUES
('admin-users', 'API', 'READ', '/api/v1/users', (SELECT MENU_NO FROM MENUS WHERE MENU_CODE = 'admin-users'), 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('admin-users', 'MENU', 'READ', '/admin/users', (SELECT MENU_NO FROM MENUS WHERE MENU_CODE = 'admin-users'), 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('admin-users', 'MENU', 'WRITE', '/admin/users', (SELECT MENU_NO FROM MENUS WHERE MENU_CODE = 'admin-users'), 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('admin-users', 'MENU', 'DELETE', '/admin/users', (SELECT MENU_NO FROM MENUS WHERE MENU_CODE = 'admin-users'), 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM');

-- Role Management Permissions
INSERT INTO PERMISSIONS (PERMISSION_CODE, PERMISSION_TYPE, PERMISSION_ACTION, RESOURCE_PATH, MENU_NO, TENANT_ID, CREATED_DATE, CREATED_BY)
VALUES
('admin-roles', 'API', 'READ', '/api/v1/roles', (SELECT MENU_NO FROM MENUS WHERE MENU_CODE = 'admin-roles'), 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('admin-roles', 'MENU', 'READ', '/admin/roles', (SELECT MENU_NO FROM MENUS WHERE MENU_CODE = 'admin-roles'), 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('admin-roles', 'MENU', 'WRITE', '/admin/roles', (SELECT MENU_NO FROM MENUS WHERE MENU_CODE = 'admin-roles'), 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('admin-roles', 'MENU', 'DELETE', '/admin/roles', (SELECT MENU_NO FROM MENUS WHERE MENU_CODE = 'admin-roles'), 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM');

-- Menu Management Permissions
INSERT INTO PERMISSIONS (PERMISSION_CODE, PERMISSION_TYPE, PERMISSION_ACTION, RESOURCE_PATH, MENU_NO, TENANT_ID, CREATED_DATE, CREATED_BY)
VALUES
('admin-menus', 'API', 'READ', '/api/v1/menus', (SELECT MENU_NO FROM MENUS WHERE MENU_CODE = 'admin-menus'), 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('admin-menus', 'MENU', 'READ', '/admin/menus', (SELECT MENU_NO FROM MENUS WHERE MENU_CODE = 'admin-menus'), 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('admin-menus', 'MENU', 'WRITE', '/admin/menus', (SELECT MENU_NO FROM MENUS WHERE MENU_CODE = 'admin-menus'), 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('admin-menus', 'MENU', 'DELETE', '/admin/menus', (SELECT MENU_NO FROM MENUS WHERE MENU_CODE = 'admin-menus'), 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM');

-- =====================================================
-- 6. ROLE-PERMISSION ASSIGNMENTS
-- =====================================================

-- Assign all permissions to SUPER_ADMIN role
INSERT INTO ROLE_PERMISSIONS (ROLE_ID, PERMISSION_ID, GRANTED_AT, GRANTED_BY, TENANT_ID)
SELECT 'ROLE_SUPER_ADMIN', PERMISSION_ID, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'
FROM PERMISSIONS
WHERE TENANT_ID = 'SYSTEM';

-- Assign dashboard read permission to basic USER role
INSERT INTO ROLE_PERMISSIONS (ROLE_ID, PERMISSION_ID, GRANTED_AT, GRANTED_BY, TENANT_ID)
SELECT 'ROLE_USER', PERMISSION_ID, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'
FROM PERMISSIONS
WHERE PERMISSION_CODE = 'dashboard' AND PERMISSION_ACTION = 'READ' AND TENANT_ID = 'SYSTEM';

-- =====================================================
-- 7. SYSTEM ADMIN USER
-- =====================================================

-- Create system admin user
-- Password: admin123 (BCrypt encoded)
-- Note: In production, this should be changed immediately after first login
INSERT INTO USERS (ESNTL_ID, USER_ID, PASSWORD, USER_NAME, EMAIL, PHONE, GROUP_ID, BRANCH_ID, POSITION_ID, STATUS_CODE, TENANT_ID, USE_AT, CREATED_DATE)
VALUES (
    'SYS_USER001',
    'admin',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
    'System Administrator',
    'admin@system.local',
    '+1-000-000-0000',
    'SYS_GRP001',
    'SYS_BR001',
    'SYS_POS001',
    'ACTIVE',
    'SYSTEM',
    'Y',
    CURRENT_TIMESTAMP
);

-- Assign SUPER_ADMIN role to admin user
INSERT INTO USER_ROLES (USER_ID, ROLE_ID, ASSIGNED_AT, ASSIGNED_BY, IS_PRIMARY, TENANT_ID)
VALUES ('SYS_USER001', 'ROLE_SUPER_ADMIN', CURRENT_TIMESTAMP, 'SYSTEM', 'Y', 'SYSTEM');

-- =====================================================
-- 8. COMMON CODES (SAMPLE)
-- =====================================================

-- Status codes
INSERT INTO COMMON_CODES (CODE_ID, CLASS_CODE, CODE_NAME, CODE_DESCRIPTION, USE_AT, TENANT_ID, CREATED_DATE)
VALUES
('STC001', 'STC', 'Active', 'Active status', 'Y', 'SYSTEM', CURRENT_TIMESTAMP),
('STC002', 'STC', 'Inactive', 'Inactive status', 'Y', 'SYSTEM', CURRENT_TIMESTAMP),
('STC003', 'STC', 'Suspended', 'Suspended status', 'Y', 'SYSTEM', CURRENT_TIMESTAMP);

-- User types
INSERT INTO COMMON_CODES (CODE_ID, CLASS_CODE, CODE_NAME, CODE_DESCRIPTION, USE_AT, TENANT_ID, CREATED_DATE)
VALUES
('UST001', 'UST', 'System', 'System user', 'Y', 'SYSTEM', CURRENT_TIMESTAMP),
('UST002', 'UST', 'Portal', 'Portal user', 'Y', 'SYSTEM', CURRENT_TIMESTAMP),
('UST003', 'UST', 'API', 'API user', 'Y', 'SYSTEM', CURRENT_TIMESTAMP);

-- =====================================================
-- END OF SEED DATA
-- =====================================================

-- Summary of created data:
-- - 1 System Tenant (SYSTEM)
-- - 1 Branch, 1 Group, 1 Position
-- - 3 Roles (SUPER_ADMIN, TENANT_ADMIN, USER)
-- - 5 Menus (Dashboard, Admin folder, Users, Roles, Menus management)
-- - 13 Permissions (auto-generated from menus)
-- - 1 Admin User (username: admin, password: admin123)
-- - Role-Permission assignments
-- - Sample common codes
