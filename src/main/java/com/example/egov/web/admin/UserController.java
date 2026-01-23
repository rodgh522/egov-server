package com.example.egov.web.admin;

import com.example.egov.domain.admin.User;
import com.example.egov.service.admin.UserService;
import com.example.egov.web.admin.dto.PasswordChangeRequest;
import com.example.egov.web.admin.dto.UserCreateRequest;
import com.example.egov.web.admin.dto.UserResponse;
import com.example.egov.web.admin.dto.UserUpdateRequest;
import com.example.egov.web.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 * - Handles User CRUD operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User CRUD API")
public class UserController {

    private final UserService userService;

    /**
     * Create a new user
     */
    @PostMapping
    @Operation(summary = "Create User", description = "Create a new user")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserCreateRequest request) {
        log.debug("Create user request: {}", request.getUserId());
        User user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(UserResponse.from(user)));
    }

    /**
     * Get user by esntlId
     */
    @GetMapping("/{esntlId}")
    @Operation(summary = "Get User", description = "Get user by ESNTL_ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable String esntlId) {
        log.debug("Get user request: {}", esntlId);
        return userService.getUser(esntlId)
                .map(user -> ResponseEntity.ok(ApiResponse.success(UserResponse.from(user))))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all users with pagination
     */
    @GetMapping
    @Operation(summary = "Get Users", description = "Get all users with pagination")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.debug("Get users request: page={}, size={}", page, size);

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userPage = userService.getUsers(pageable);
        Page<UserResponse> responsePage = userPage.map(UserResponse::from);

        return ResponseEntity.ok(ApiResponse.success(responsePage));
    }

    /**
     * Update user
     */
    @PutMapping("/{esntlId}")
    @Operation(summary = "Update User", description = "Update user information")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable String esntlId,
            @Valid @RequestBody UserUpdateRequest request) {
        log.debug("Update user request: {}", esntlId);
        User user = userService.updateUser(esntlId, request);
        return ResponseEntity.ok(ApiResponse.success(UserResponse.from(user)));
    }

    /**
     * Delete user (soft delete)
     */
    @DeleteMapping("/{esntlId}")
    @Operation(summary = "Delete User", description = "Soft delete user (set useAt to 'N')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String esntlId) {
        log.debug("Delete user request: {}", esntlId);
        userService.deleteUser(esntlId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("User deleted successfully")
                .build());
    }

    /**
     * Change user password
     */
    @PutMapping("/{esntlId}/password")
    @Operation(summary = "Change Password", description = "Change user password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable String esntlId,
            @Valid @RequestBody PasswordChangeRequest request) {
        log.debug("Change password request for user: {}", esntlId);
        userService.changePassword(esntlId, request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("Password changed successfully")
                .build());
    }

    /**
     * Check if userId exists
     */
    @GetMapping("/exists/{userId}")
    @Operation(summary = "Check User ID Exists", description = "Check if a user ID already exists")
    public ResponseEntity<ApiResponse<Boolean>> existsByUserId(@PathVariable String userId) {
        log.debug("Check userId exists: {}", userId);
        boolean exists = userService.existsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}
