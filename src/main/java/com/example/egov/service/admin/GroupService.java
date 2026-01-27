package com.example.egov.service.admin;

import com.example.egov.config.multitenancy.TenantContext;
import com.example.egov.domain.admin.Branch;
import com.example.egov.domain.admin.BranchRepository;
import com.example.egov.domain.admin.Group;
import com.example.egov.domain.admin.GroupRepository;
import com.example.egov.domain.admin.TenantRepository;
import com.example.egov.web.admin.dto.GroupCreateRequest;
import com.example.egov.web.admin.dto.GroupUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private static final String SYSTEM_TENANT_ID = "SYSTEM";

    private final GroupRepository groupRepository;
    private final BranchRepository branchRepository;
    private final TenantRepository tenantRepository;

    private boolean isSystemTenant() {
        return SYSTEM_TENANT_ID.equals(TenantContext.getCurrentTenantId());
    }

    private void verifyGroupAccess(Group group) {
        if (!isSystemTenant() && !group.getTenantId().equals(TenantContext.getCurrentTenantId())) {
            throw new AccessDeniedException("Access denied to group: " + group.getGroupId());
        }
    }

    @Transactional
    public Group createGroup(GroupCreateRequest request) {
        String currentTenantId = TenantContext.getCurrentTenantId();

        if (groupRepository.existsByGroupCode(request.getGroupCode())) {
            throw new IllegalArgumentException("Group code already exists: " + request.getGroupCode());
        }

        // Determine tenant ID: SYSTEM can specify, others use current tenant
        String tenantId;
        if (isSystemTenant() && request.getTenantId() != null) {
            if (!tenantRepository.existsByTenantId(request.getTenantId())) {
                throw new IllegalArgumentException("Tenant not found: " + request.getTenantId());
            }
            tenantId = request.getTenantId();
        } else {
            tenantId = currentTenantId;
        }

        // Validate branch
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + request.getBranchId()));

        // Verify branch belongs to the same tenant
        if (!branch.getTenantId().equals(tenantId)) {
            throw new IllegalArgumentException("Branch must belong to the same tenant");
        }

        String groupId = generateGroupId();
        log.info("Creating group: {} for tenant: {}", groupId, tenantId);

        Group group = new Group();
        group.setGroupId(groupId);
        group.setGroupName(request.getGroupName());
        group.setGroupCode(request.getGroupCode());
        group.setGroupDescription(request.getGroupDescription());
        group.setBranch(branch);
        group.setUseAt(request.getUseAt());
        group.setTenantId(tenantId);

        Group savedGroup = groupRepository.save(group);
        log.info("Group created successfully: {}", savedGroup.getGroupId());

        return savedGroup;
    }

    @Transactional
    public Group updateGroup(String groupId, GroupUpdateRequest request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupId));

        verifyGroupAccess(group);

        log.info("Updating group: {}", groupId);

        if (request.getGroupName() != null) {
            group.setGroupName(request.getGroupName());
        }
        if (request.getGroupCode() != null) {
            Optional<Group> existingGroup = groupRepository.findByGroupCode(request.getGroupCode());
            if (existingGroup.isPresent() && !existingGroup.get().getGroupId().equals(groupId)) {
                throw new IllegalArgumentException("Group code already exists: " + request.getGroupCode());
            }
            group.setGroupCode(request.getGroupCode());
        }
        if (request.getGroupDescription() != null) {
            group.setGroupDescription(request.getGroupDescription());
        }
        if (request.getBranchId() != null) {
            Branch branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + request.getBranchId()));
            if (!branch.getTenantId().equals(group.getTenantId())) {
                throw new IllegalArgumentException("Branch must belong to the same tenant");
            }
            group.setBranch(branch);
        }
        if (request.getUseAt() != null) {
            group.setUseAt(request.getUseAt());
        }

        Group updatedGroup = groupRepository.save(group);
        log.info("Group updated successfully: {}", updatedGroup.getGroupId());

        return updatedGroup;
    }

    public Optional<Group> getGroup(String groupId) {
        Optional<Group> group = groupRepository.findById(groupId);
        group.ifPresent(this::verifyGroupAccess);
        return group;
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public List<Group> getActiveGroups() {
        return groupRepository.findByUseAt("Y");
    }

    public List<Group> getGroupsByTenant(String tenantId) {
        if (!isSystemTenant()) {
            throw new AccessDeniedException("Only SYSTEM tenant can query by tenant ID");
        }
        return groupRepository.findByTenantId(tenantId);
    }

    public List<Group> getGroupsByBranch(String branchId) {
        return groupRepository.findByBranch_BranchId(branchId);
    }

    @Transactional
    public void deleteGroup(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupId));

        verifyGroupAccess(group);

        log.info("Deleting group: {}", groupId);
        groupRepository.delete(group);
        log.info("Group deleted successfully: {}", groupId);
    }

    public boolean existsByGroupId(String groupId) {
        return groupRepository.existsByGroupId(groupId);
    }

    public boolean existsByGroupCode(String groupCode) {
        return groupRepository.existsByGroupCode(groupCode);
    }

    private String generateGroupId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "GRP" + timestamp.substring(timestamp.length() - 10) + suffix;
    }
}
