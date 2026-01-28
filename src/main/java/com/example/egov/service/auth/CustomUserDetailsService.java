package com.example.egov.service.auth;

import com.example.egov.domain.admin.RolePermissionRepository;
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
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom UserDetailsService implementation for Spring Security
 * - Loads user by userId for authentication
 * - Includes tenant, branch, group, position, roles, and permissions in CustomUserDetails
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RolePermissionRepository rolePermissionRepository;

    /**
     * Load user by username (userId)
     * Note: This method is used by Spring Security during authentication.
     * For login, use loadUserByUserIdForLogin to bypass tenant filter.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

        return createUserDetails(user);
    }

    /**
     * Load user by userId for login (bypasses tenant filter)
     * TenantId is retrieved from the user record
     */
    @Transactional(readOnly = true)
    public CustomUserDetails loadUserByUserIdForLogin(String userId) {
        User user = userRepository.findByUserIdNative(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

        return createUserDetails(user);
    }

    /**
     * Create CustomUserDetails from User entity with all context information
     */
    private CustomUserDetails createUserDetails(User user) {
        // Collect role IDs
        Set<String> roleIds = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getRoleId())
                .collect(Collectors.toSet());

        // Collect permissions from all roles
        Set<String> permissions = Collections.emptySet();
        if (!roleIds.isEmpty()) {
            permissions = rolePermissionRepository.findByRoleIdIn(roleIds).stream()
                    .map(rp -> rp.getPermission().getPermissionCode())
                    .collect(Collectors.toSet());
        }

        Collection<GrantedAuthority> authorities = getAuthorities(user);

        return CustomUserDetails.customBuilder()
                .userId(user.getUserId())
                .password(user.getPassword())
                .tenantId(user.getTenantId())
                .esntlId(user.getId())
                .branchId(user.getBranch() != null ? user.getBranch().getBranchId() : null)
                .groupId(user.getGroup() != null ? user.getGroup().getGroupId() : null)
                .positionId(user.getPosition() != null ? user.getPosition().getPositionId() : null)
                .roleIds(roleIds)
                .permissions(permissions)
                .authorities(authorities)
                .build();
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
