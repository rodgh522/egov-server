package com.example.egov.web.admin;

import com.example.egov.domain.admin.Group;
import com.example.egov.service.admin.GroupService;
import com.example.egov.web.admin.dto.GroupCreateRequest;
import com.example.egov.web.admin.dto.GroupResponse;
import com.example.egov.web.admin.dto.GroupUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Group management.
 *
 * Access control:
 * - SYSTEM tenant can access all groups across tenants
 * - Other tenants can only access their own groups
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody GroupCreateRequest request) {
        log.info("Creating group: {}", request.getGroupName());

        Group group = groupService.createGroup(request);
        GroupResponse response = GroupResponse.from(group);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupResponse> updateGroup(
            @PathVariable String groupId,
            @Valid @RequestBody GroupUpdateRequest request) {
        log.info("Updating group: {}", groupId);

        Group group = groupService.updateGroup(groupId, request);
        GroupResponse response = GroupResponse.from(group);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable String groupId) {
        log.debug("Fetching group: {}", groupId);

        return groupService.getGroup(groupId)
                .map(group -> ResponseEntity.ok(GroupResponse.from(group)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getAllGroups() {
        log.debug("Fetching all groups");

        List<Group> groups = groupService.getAllGroups();
        List<GroupResponse> responses = GroupResponse.fromList(groups);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    public ResponseEntity<List<GroupResponse>> getActiveGroups() {
        log.debug("Fetching active groups");

        List<Group> groups = groupService.getActiveGroups();
        List<GroupResponse> responses = GroupResponse.fromList(groups);

        return ResponseEntity.ok(responses);
    }

    /**
     * Get groups by tenant ID (SYSTEM tenant only).
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<GroupResponse>> getGroupsByTenant(@PathVariable String tenantId) {
        log.debug("Fetching groups for tenant: {}", tenantId);

        List<Group> groups = groupService.getGroupsByTenant(tenantId);
        List<GroupResponse> responses = GroupResponse.fromList(groups);

        return ResponseEntity.ok(responses);
    }

    /**
     * Get groups by branch ID.
     */
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<GroupResponse>> getGroupsByBranch(@PathVariable String branchId) {
        log.debug("Fetching groups for branch: {}", branchId);

        List<Group> groups = groupService.getGroupsByBranch(branchId);
        List<GroupResponse> responses = GroupResponse.fromList(groups);

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable String groupId) {
        log.info("Deleting group: {}", groupId);

        groupService.deleteGroup(groupId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{groupId}")
    public ResponseEntity<Boolean> existsByGroupId(@PathVariable String groupId) {
        log.debug("Checking if group exists: {}", groupId);

        boolean exists = groupService.existsByGroupId(groupId);

        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/code/{groupCode}")
    public ResponseEntity<Boolean> existsByGroupCode(@PathVariable String groupCode) {
        log.debug("Checking if group code exists: {}", groupCode);

        boolean exists = groupService.existsByGroupCode(groupCode);

        return ResponseEntity.ok(exists);
    }
}
