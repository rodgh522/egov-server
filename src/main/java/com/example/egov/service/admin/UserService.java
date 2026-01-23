package com.example.egov.service.admin;

import com.example.egov.config.multitenancy.TenantContext;
import com.example.egov.domain.admin.*;
import com.example.egov.web.admin.dto.PasswordChangeRequest;
import com.example.egov.web.admin.dto.UserCreateRequest;
import com.example.egov.web.admin.dto.UserUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User Service
 * - Handles User CRUD operations
 * - Multi-tenant aware via TenantContext
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final GroupRepository groupRepository;
    private final BranchRepository branchRepository;
    private final PositionRepository positionRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create a new user
     */
    @Transactional
    public User createUser(UserCreateRequest request) {
        log.debug("Creating user with userId: {}", request.getUserId());

        // Check if userId already exists
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("User ID already exists: " + request.getUserId());
        }

        String tenantId = TenantContext.getCurrentTenantId();

        User user = new User();
        user.setId(generateEsntlId());
        user.setUserId(request.getUserId());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setTenantId(tenantId);
        user.setUseAt("Y");

        // Set relationships
        setUserRelationships(user, request.getGroupId(), request.getBranchId(),
                request.getPositionId(), request.getManagerId());

        User savedUser = userRepository.save(user);

        // Assign roles if provided
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            assignRolesToUser(savedUser, request.getRoleIds(), tenantId);
        }

        log.info("User created successfully: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * Get user by esntlId
     */
    public Optional<User> getUser(String esntlId) {
        return userRepository.findByIdAndUseAt(esntlId, "Y");
    }

    /**
     * Get user by userId
     */
    public Optional<User> getUserByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    /**
     * Get all active users with pagination
     */
    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findByUseAt("Y", pageable);
    }

    /**
     * Update user
     */
    @Transactional
    public User updateUser(String esntlId, UserUpdateRequest request) {
        log.debug("Updating user: {}", esntlId);

        User user = userRepository.findById(esntlId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + esntlId));

        // Update fields if provided
        if (request.getUserName() != null) {
            user.setUserName(request.getUserName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getStatusCode() != null) {
            user.setStatusCode(request.getStatusCode());
        }
        if (request.getUseAt() != null) {
            user.setUseAt(request.getUseAt());
        }

        // Update relationships
        setUserRelationships(user, request.getGroupId(), request.getBranchId(),
                request.getPositionId(), request.getManagerId());

        // Update roles if provided
        if (request.getRoleIds() != null) {
            updateUserRoles(user, request.getRoleIds());
        }

        log.info("User updated successfully: {}", esntlId);
        return userRepository.save(user);
    }

    /**
     * Soft delete user (set useAt = 'N')
     */
    @Transactional
    public void deleteUser(String esntlId) {
        log.debug("Deleting user: {}", esntlId);

        User user = userRepository.findById(esntlId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + esntlId));

        user.setUseAt("N");
        userRepository.save(user);

        log.info("User soft deleted: {}", esntlId);
    }

    /**
     * Change user password
     */
    @Transactional
    public void changePassword(String esntlId, PasswordChangeRequest request) {
        log.debug("Changing password for user: {}", esntlId);

        // Validate password confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        User user = userRepository.findById(esntlId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + esntlId));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed for user: {}", esntlId);
    }

    /**
     * Check if userId exists
     */
    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    // Private helper methods

    /**
     * Generate unique ESNTL_ID
     */
    private String generateEsntlId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "USR" + timestamp.substring(timestamp.length() - 10) + suffix;
    }

    /**
     * Set user relationships (group, branch, position, manager)
     */
    private void setUserRelationships(User user, String groupId, String branchId,
                                       String positionId, String managerId) {
        if (groupId != null) {
            groupRepository.findById(groupId).ifPresent(user::setGroup);
        }
        if (branchId != null) {
            branchRepository.findById(branchId).ifPresent(user::setBranch);
        }
        if (positionId != null) {
            positionRepository.findById(positionId).ifPresent(user::setPosition);
        }
        if (managerId != null) {
            userRepository.findById(managerId).ifPresent(user::setManager);
        }
    }

    /**
     * Assign roles to a newly created user
     */
    private void assignRolesToUser(User user, List<String> roleIds, String tenantId) {
        boolean isFirst = true;
        for (String roleId : roleIds) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleId));

            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            userRole.setTenantId(tenantId);
            userRole.setIsPrimary(isFirst ? "Y" : "N");
            isFirst = false;

            userRoleRepository.save(userRole);
        }
    }

    /**
     * Update user roles (remove old, add new)
     */
    private void updateUserRoles(User user, List<String> roleIds) {
        String tenantId = TenantContext.getCurrentTenantId();

        // Clear existing roles
        user.getUserRoles().clear();

        // Add new roles
        if (!roleIds.isEmpty()) {
            boolean isFirst = true;
            for (String roleId : roleIds) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleId));

                UserRole userRole = new UserRole();
                userRole.setUser(user);
                userRole.setRole(role);
                userRole.setTenantId(tenantId);
                userRole.setIsPrimary(isFirst ? "Y" : "N");
                isFirst = false;

                user.getUserRoles().add(userRole);
            }
        }
    }
}
