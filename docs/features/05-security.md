# Feature: Security & Authentication

## 1. Authentication Strategy
- **Mechanism**: Stateless JWT (JSON Web Token).
- **No XML**: All config via Java `@Configuration`.
- **User Object**: `CustomUserDetails` must contain `tenantId`, `roles`, and flattened `authorities` (permissions).

## 2. Authorization Rules (RBAC)
- **Method Security**: Use `@PreAuthorize` heavily.
    - Format: `@PreAuthorize("hasAuthority('API:business:READ')")`
- **Dynamic Eval**: Optional `PermissionEvaluator` for complex logic.
- **Tenant Scope**: Admin can only assign roles/permissions within their own Tenant.

## 3. Implementation Details

### CustomUserDetailsService
```java
// Logic:
// 1. Find User
// 2. Load UserRoles -> Roles -> RolePermissions -> Permissions
// 3. Flatten Permissions to List<SimpleGrantedAuthority>
// 4. Return CustomUserDetails(username, password, tenantId, authorities)
```

### Security Config (eGovFrame 5.0)
```java
@Configuration
@EnableWebSecurity
public class EgovSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

## 4. Security Checklist
- [ ] TenantInterceptor active (sets Context).
- [ ] Passwords BCrypt encoded.
- [ ] JWT signed with strong key.
- [ ] Input validation (`@Valid`) on all DTOs.
