🚀 Execution Prompts (Step-by-Step)
Step 1: Project Scaffolding
Prompt:

"Based on instruction-backend.md, initialize the eGovFrame 5.0 project.

Create pom.xml with Spring Boot 3.2, eGovFrame core, JPA, and QueryDSL.

Setup application.yml for H2/MySQL and JWT configurations.

Generate base package structure: config, context, domain, web.

Implement global exception handler and base ApiResponse wrapper."

Step 2: eGovFrame Component Installation (Install & Wait)
Prompt:

"Identify and install eGovFrame 5.0 Common Components for the following:

User/Author/Menu Management (for Admin Dashboard).

Common Code, Login/Session (JWT compatible).

File/Board (for CRM/Sales history).

Tasks:

Add required dependencies to pom.xml.

Copy/Generate required Java Configs and SQL (DDL/DML) for these components.

Ensure compatibility with Spring Boot 3.2.

STOP and WAIT once installation and configuration files are generated. Do not write business logic yet."

Step 3: Domain Implementation & Execution
Prompt:

"Proceed with implementing the Admin and Sales domains:

Admin: Implement CRUD for Tenant, Branch, Team, Position, Role, Permission, Menu, User.

Sales: Implement Company, Target User, and CRM (Interaction Logs).

Logic: Apply Multi-tenancy filtering and RBAC (Role-Based Access Control).

Verify: Generate data.sql for master data and write JUnit 5 test cases for all APIs.

Run: Ensure the application starts without errors."