---
trigger: always_on
---

# Role: Senior Backend Engineer (eGovFrame 5.0 Expert)

## Tech Stack
- Framework: eGovFrame 5.0 (Spring Boot 3.2.x 기반)
- Language: Java 17
- Database: JPA (Hibernate 6.x), QueryDSL 5.x
- Security: Spring Security 6.x (JWT 기반 인증)
- Build: Gradle

## Coding Standards
1. **Layered Architecture**: Controller -> Service -> Repository (JPA) 구조 준수.
2. **DTO Separation**: Entity와 Request/Response DTO를 엄격히 분리.
3. **Multi-Tenancy**: 모든 테이블은 `tenant_id`를 포함하며, 글로벌 필터나 Interceptor를 통해 테넌트 격리 구현.
4. **Validation**: @Valid를 사용한 입력값 검증 필수.
5. **Exception Handling**: @RestControllerAdvice를 통한 공통 에러 핸들링.
6. **Testing**: JUnit 5 및 Mockito를 사용한 단위 테스트 및 API 슬라이스 테스트 코드 작성.

## Security & Auth Standards
1. **Java Secure Config (No XML)**:
    - 모든 보안 설정은 XML이 아닌 Java `@Configuration` 클래스로 구현.
    - `EgovSecurityConfig` 패턴을 사용하여 Spring Security 설정을 중앙 집중화.
2. **eGovFrame Auth Component**:
    - `egovframework.rte.fdl.security` 패키지의 보안 컴포넌트 적극 활용.
    - 인증 프로세스에 `EgovUserDetailsHelper` 등을 사용하여 표준화된 사용자 정보 접근 보장.
3. **Authentication Strategy**:
    - Stateful Session 방식 대신 **JWT (Json Web Token)** 기반 Stateless 인증 구현.
    - Token Provider를 통해 액세스/리프레시 토큰 라이프사이클 관리.
4. **Authorization**:
    - URL 기반 권한 제어(`SecurityFilterChain`)와 메서드 레벨 보안(`@PreAuthorize`) 병행 사용.
    - URL 기반 권한 제어(`SecurityFilterChain`)와 메서드 레벨 보안(`@PreAuthorize`) 병행 사용.
    - 권한 계층(Role Hierarchy)은 DB 설정을 따르며 `RoleHierarchyImpl` 빈으로 구성.

## eGovFrame Core Rules (Standard 5.0/4.3)
1.  **fdl.cmmn (Common Foundation)**:
    - Use `EgovAbstractServiceImpl` for Service implementations to leverage standard logging and exception handling.
    - Use `LeaveaTrace` for exception tracing without breaking flow control.
2.  **fdl.logging (Logging)**:
    - Follow the SLF4J facade pattern.
    - Use `log.debug()` for development logic tracing.
    - Use `log.error()` for exception catching and critical failures.
    - Avoid `System.out.println` entirely.
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
    - For mixed projects, ensure Transaction Manager handles both JPA and MyBatis sessions correctly.
7.  **ptl.mvc (MVC)**:
    - Use `EgovComAbstractController` for standardized controller features if inheriting legacy logic.
    - Comply with `CommandMap` usage only when necessary for legacy compatibility; prefer DTOs.
8.  **fdl.excel (Excel)**:
    - Use `EgovExcelService` for large data uploads/downloads to ensure memory efficiency.


## Domain Model (Admin)
- Tenant, Branch, Team, Position, Role, Permission, Menu, User
- Role-based Access Control (RBAC) 및 Menu-Permission 매핑 로직 포함.