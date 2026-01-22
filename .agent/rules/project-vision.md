1. Executive Summary
crm-project is a Multi-tenant Customer Relationship Management (CRM) dashboard built on eGovFrame 5.0 Beta (Java Spring Boot). It features a complex organizational hierarchy and an automated permission generation system linked to menu management, ensuring high security and administrative efficiency.

2. Tech Stack
Backend: Java 17+, Spring Boot 3.x with eGovFrame 5.0 Beta (utilizing core components).

Database: PostgreSQL (Multi-tenancy isolation).

Frontend: React 19.2, vercel, TypeScript.

UI Library: shadcn/ui (Tailwind CSS).

3. Core Domain & Data Model
Organizational Hierarchy: Tenant > Branch > Group > Position > User.

Multi-tenancy: Strict data isolation; users can only access/write data belonging to their own Tenant.

Advanced RBAC (Role-Based Access Control):

Multiple Roles: Users can be assigned multiple roles at both User and Group levels.

Role Mapping: Roles bundle Permissions and Menus.

Tenant Admin: Can manage roles and assignments within their own tenant.

4. System Core: Automated Permission Logic
Menu-Driven Permissions: When a System Admin (Developer) creates a new menu, the system automatically generates corresponding permissions.

Example: Menu business-list (/business/list) automatically creates:

type: API, code: business-list, value: read

type: MENU, code: business-list, value: read/write/download

Granular Control: Permissions are categorized by types (API vs. MENU) and actions (Read, Write, Download).

5. Key User Stories
Developer: I register a new feature in the menu management module, and the system instantly populates the permission table with required action codes.

Tenant Admin: I assign a "Sales Manager" role to a user in my branch, which automatically grants them access to specific UI menus and backend API endpoints.

User: Upon login, I see a dashboard filtered by my Tenant ID, with menus visible only if my assigned roles contain the required permissions.