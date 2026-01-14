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
    - 권한 계층(Role Hierarchy)은 DB 설정을 따르며 `RoleHierarchyImpl` 빈으로 구성.


## Domain Model (Admin)
- Tenant, Branch, Team, Position, Role, Permission, Menu, User
- Role-based Access Control (RBAC) 및 Menu-Permission 매핑 로직 포함.