package com.example.egov.web.auth;

import com.example.egov.domain.auth.CustomUserDetails;
import com.example.egov.service.auth.AuthService;
import com.example.egov.web.auth.dto.LoginRequest;
import com.example.egov.web.auth.dto.RefreshTokenRequest;
import com.example.egov.web.auth.dto.TokenResponse;
import com.example.egov.web.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Controller
 * - Handles login, logout, token refresh, and current user info
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {

    private final AuthService authService;

    /**
     * Login and get JWT tokens
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return JWT tokens")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.debug("Login request for userId: {}", request.getUserId());
        TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }

    /**
     * Logout (client should discard tokens)
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout user (client should discard tokens)")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // For stateless JWT, logout is handled client-side by discarding tokens
        // Server-side token blacklisting can be implemented if needed
        log.debug("Logout request");
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("Logged out successfully")
                .build());
    }

    /**
     * Refresh access token using refresh token
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Get new access token using refresh token")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.debug("Token refresh request");
        TokenResponse tokenResponse = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }

    /**
     * Get current authenticated user info
     */
    @GetMapping("/me")
    @Operation(summary = "Get Current User", description = "Get information about the currently authenticated user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser(Authentication authentication) {
        CustomUserDetails userDetails = authService.getCurrentUser(authentication);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("esntlId", userDetails.getEsntlId());
        userInfo.put("userId", userDetails.getUserId());
        userInfo.put("tenantId", userDetails.getTenantId());
        userInfo.put("roles", userDetails.getAuthorities().stream()
                .map(Object::toString)
                .toList());

        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }
}
