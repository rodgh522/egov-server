package com.example.egov.service.auth;

import com.example.egov.domain.admin.User;
import com.example.egov.domain.admin.UserRepository;
import com.example.egov.domain.admin.UserRole;
import com.example.egov.domain.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Custom UserDetailsService implementation for Spring Security
 * - Loads user by userId for authentication
 * - Includes tenant information in CustomUserDetails
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user by username (userId)
     * Note: This method is used by Spring Security during authentication.
     * For login, use loadUserByUserIdAndTenantId to bypass tenant filter.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

        return createUserDetails(user);
    }

    /**
     * Load user by userId and tenantId (for login - bypasses tenant filter)
     */
    @Transactional(readOnly = true)
    public CustomUserDetails loadUserByUserIdAndTenantId(String userId, String tenantId) {
        User user = userRepository.findByUserIdAndTenantIdNative(userId, tenantId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with userId: " + userId + " and tenantId: " + tenantId));

        return createUserDetails(user);
    }

    /**
     * Create CustomUserDetails from User entity
     */
    private CustomUserDetails createUserDetails(User user) {
        Collection<GrantedAuthority> authorities = getAuthorities(user);

        return new CustomUserDetails(
                user.getUserId(),
                user.getPassword(),
                user.getTenantId(),
                user.getId(),
                authorities
        );
    }

    /**
     * Get authorities (roles) from user's UserRole associations
     */
    private Collection<GrantedAuthority> getAuthorities(User user) {
        return user.getUserRoles().stream()
                .map(UserRole::getRole)
                .map(role -> new SimpleGrantedAuthority(role.getRoleId()))
                .collect(Collectors.toList());
    }
}
