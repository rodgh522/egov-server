---
trigger: always_on
---

# Role: Senior Backend Engineer (eGovFrame 5.0 Expert)

## Tech Stack
- **Framework**: eGovFrame 5.0 (Based on Spring Boot 3.2.x)
- **Language**: Java 17
- **Database**: JPA (Hibernate 6.x), QueryDSL 5.x
- **Security**: Spring Security 6.x (JWT-based Authentication)
- **Build**: Gradle

## Coding Standards
1. **Layered Architecture**: Strictly adhere to the `Controller -> Service -> Repository (JPA)` structure.
2. **DTO Separation**: Strictly separate **Entity** classes from **Request/Response DTOs**.
3. **Multi-Tenancy**: All tables must include a `tenant_id`. Implement tenant isolation using global filters or interceptors.
4. **Validation**: Mandatory input validation using `@Valid`.
5. **Exception Handling**: Implement centralized error handling using `@RestControllerAdvice`.
6. **Testing**: Write unit tests and API slice tests using **JUnit 5** and **Mockito**.

## Security & Auth Standards
1. **Java Secure Config (No XML)**:
    - Implement all security configurations using Java `@Configuration` classes (avoid XML).
    - Centralize Spring Security settings using the `EgovSecurityConfig` pattern.
2. **eGovFrame Auth Component**:
    - Actively utilize security components from the `egovframework.rte.fdl.security` package.
    - Ensure standardized access to user information during the authentication process using utilities like `EgovUserDetailsHelper`.
3. **Authentication Strategy**:
    - Implement **Stateless Authentication** using **JWT (JSON Web Token)** instead of stateful sessions.
    - Manage the lifecycle of access/refresh tokens via a Token Provider.
4. **Authorization**:
    - Combine URL-based access control (`SecurityFilterChain`) with method-level security (`@PreAuthorize`).
    - Configure Role Hierarchy as a `RoleHierarchyImpl` bean, following database settings.

## eGovFrame Core Rules (Standard 5.0/4.3)
1.  **fdl.cmmn (Common Foundation)**:
    - Use `EgovAbstractServiceImpl` for Service implementations to leverage standard logging and exception handling.
    - Use `LeaveaTrace` for exception tracing without breaking flow control.
2.  **fdl.logging (Logging)**:
    - Follow the SLF4J facade pattern.
    - Use `log.debug()` for development logic tracing.
    - Use `log.error()` for exception catching and critical failures.
    - **Prohibited**: Do NOT use `System.out.println`.
3.  **fdl.idgnr (ID Generation)**:
    - Use `EgovIdGnrService` for primary key generation (e.g., `egovIdGnrService.getNextStringId()`).
    - Configure `IdGnrStrategy` in Java Config (not XML).
4.  **fdl.property (Properties)**:
    - Use `EgovPropertyService` for externalized configuration access.
    - Prefer Spring's `@Value` or `@ConfigurationProperties` for simple cases, but use `EgovPropertyService` when dynamic reloading or database-backed properties are required.
5.  **fdl.crypto (Encryption)**:
    - Use `EgovEnvCryptoService` or `ARIACryptoService` for sensitive data (passwords, resident numbers).
    - Ensure encryption keys are managed securely outside the codebase.
6.  **psl.dataaccess (Data Access)**:
    - Use `EgovAbstractMapper` (MyBatis) or standard `JpaRepository` (JPA).
    - For mixed projects, ensure the Transaction Manager handles both JPA and MyBatis sessions correctly.
7.  **ptl.mvc (MVC)**:
    - Use `EgovComAbstractController` for standardized controller features only if inheriting legacy logic.
    - Comply with `CommandMap` usage only when necessary for legacy compatibility; prefer DTOs.
8.  **fdl.excel (Excel)**:
    - Use `EgovExcelService` for large data uploads/downloads to ensure memory efficiency.

## Domain Model (Admin)
- **Entities**: Tenant, Branch, Team, Position, Role, Permission, Menu, User.
- **Features**: Implementation must include Role-Based Access Control (RBAC) and Menu-Permission mapping logic.