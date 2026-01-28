package com.example.egov.service.auth;

import com.example.egov.config.security.JwtTokenProvider;
import com.example.egov.domain.auth.CustomUserDetails;
import com.example.egov.web.auth.dto.LoginRequest;
import com.example.egov.web.auth.dto.RefreshTokenRequest;
import com.example.egov.web.auth.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication Service
 * - Handles login, logout, and token refresh operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * Login and generate JWT tokens
     */
    public TokenResponse login(LoginRequest request) {
        log.debug("Login attempt for userId: {}", request.getUserId());

        // Load user with full context (bypasses tenant filter via native query)
        CustomUserDetails userDetails = userDetailsService.loadUserByUserIdForLogin(request.getUserId());

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            log.warn("Invalid password for userId: {}", request.getUserId());
            throw new BadCredentialsException("Invalid password");
        }

        // Generate tokens with full user context
        String accessToken = jwtTokenProvider.createAccessToken(userDetails);

        String refreshToken = jwtTokenProvider.createRefreshToken(
                userDetails.getEsntlId(),
                userDetails.getTenantId()
        );

        log.info("Login successful for userId: {}, tenantId: {}",
                request.getUserId(), userDetails.getTenantId());

        return TokenResponse.of(
                accessToken,
                refreshToken,
                jwtTokenProvider.getAccessTokenValidityInSeconds(),
                jwtTokenProvider.getRefreshTokenValidityInSeconds()
        );
    }

    /**
     * Refresh access token using refresh token
     * Reloads user details from database to get fresh permissions
     */
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        // Extract info from refresh token
        String esntlId = jwtTokenProvider.getSubjectFromToken(refreshToken);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(refreshToken);

        // Get authentication from refresh token to extract userId
        Authentication auth = jwtTokenProvider.getAuthentication(refreshToken);
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();

        // Reload user details from database to get fresh context (permissions may have changed)
        CustomUserDetails freshUserDetails = userDetailsService.loadUserByUserIdForLogin(principal.getUserId());

        // Generate new access token with fresh user context
        String newAccessToken = jwtTokenProvider.createAccessToken(freshUserDetails);

        // Generate new refresh token (token rotation for security)
        String newRefreshToken = jwtTokenProvider.createRefreshToken(esntlId, tenantId);

        log.info("Token refreshed for esntlId: {}", esntlId);

        return TokenResponse.of(
                newAccessToken,
                newRefreshToken,
                jwtTokenProvider.getAccessTokenValidityInSeconds(),
                jwtTokenProvider.getRefreshTokenValidityInSeconds()
        );
    }

    /**
     * Get current user information from authentication
     */
    public CustomUserDetails getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("Not authenticated");
        }
        return (CustomUserDetails) authentication.getPrincipal();
    }
}
