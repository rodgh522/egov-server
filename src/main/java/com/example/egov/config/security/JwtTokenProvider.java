package com.example.egov.config.security;

import com.example.egov.domain.auth.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JWT Token Provider
 * - Handles JWT token generation and validation
 * - Stores userId, tenantId, branchId, groupId, positionId, roleIds, permissions, and roles as claims
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenValidityInSeconds;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;

    private SecretKey key;

    private static final String AUTHORITIES_KEY = "roles";
    private static final String USER_ID_KEY = "userId";
    private static final String TENANT_ID_KEY = "tenantId";
    private static final String BRANCH_ID_KEY = "branchId";
    private static final String GROUP_ID_KEY = "groupId";
    private static final String POSITION_ID_KEY = "positionId";
    private static final String ROLE_IDS_KEY = "roleIds";
    private static final String PERMISSIONS_KEY = "permissions";

    @PostConstruct
    protected void init() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Create access token with full user context from CustomUserDetails
     */
    public String createAccessToken(CustomUserDetails userDetails) {
        String authoritiesString = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();
        Date validity = new Date(now + (accessTokenValidityInSeconds * 1000));

        return Jwts.builder()
                .subject(userDetails.getEsntlId())
                .claim(USER_ID_KEY, userDetails.getUserId())
                .claim(TENANT_ID_KEY, userDetails.getTenantId())
                .claim(BRANCH_ID_KEY, userDetails.getBranchId())
                .claim(GROUP_ID_KEY, userDetails.getGroupId())
                .claim(POSITION_ID_KEY, userDetails.getPositionId())
                .claim(ROLE_IDS_KEY, new ArrayList<>(userDetails.getRoleIds()))
                .claim(PERMISSIONS_KEY, new ArrayList<>(userDetails.getPermissions()))
                .claim(AUTHORITIES_KEY, authoritiesString)
                .issuedAt(new Date(now))
                .expiration(validity)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Create access token with user information and authorities (legacy method for backward compatibility)
     */
    public String createAccessToken(String esntlId, String userId, String tenantId,
                                    Collection<? extends GrantedAuthority> authorities) {
        String authoritiesString = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();
        Date validity = new Date(now + (accessTokenValidityInSeconds * 1000));

        return Jwts.builder()
                .subject(esntlId)
                .claim(USER_ID_KEY, userId)
                .claim(TENANT_ID_KEY, tenantId)
                .claim(AUTHORITIES_KEY, authoritiesString)
                .issuedAt(new Date(now))
                .expiration(validity)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Create refresh token with minimal information
     */
    public String createRefreshToken(String esntlId, String tenantId) {
        long now = System.currentTimeMillis();
        Date validity = new Date(now + (refreshTokenValidityInSeconds * 1000));

        return Jwts.builder()
                .subject(esntlId)
                .claim(TENANT_ID_KEY, tenantId)
                .issuedAt(new Date(now))
                .expiration(validity)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Extract Authentication from token
     */
    @SuppressWarnings("unchecked")
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        String esntlId = claims.getSubject();
        String userId = claims.get(USER_ID_KEY, String.class);
        String tenantId = claims.get(TENANT_ID_KEY, String.class);
        String branchId = claims.get(BRANCH_ID_KEY, String.class);
        String groupId = claims.get(GROUP_ID_KEY, String.class);
        String positionId = claims.get(POSITION_ID_KEY, String.class);
        String authoritiesString = claims.get(AUTHORITIES_KEY, String.class);

        // Parse roleIds and permissions from JWT claims (stored as JSON arrays)
        List<String> roleIdsList = claims.get(ROLE_IDS_KEY, List.class);
        List<String> permissionsList = claims.get(PERMISSIONS_KEY, List.class);

        Set<String> roleIds = roleIdsList != null ? new HashSet<>(roleIdsList) : Collections.emptySet();
        Set<String> permissions = permissionsList != null ? new HashSet<>(permissionsList) : Collections.emptySet();

        Collection<? extends GrantedAuthority> authorities;
        if (authoritiesString != null && !authoritiesString.isEmpty()) {
            authorities = Arrays.stream(authoritiesString.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } else {
            authorities = Collections.emptyList();
        }

        CustomUserDetails principal = CustomUserDetails.customBuilder()
                .userId(userId)
                .password("")
                .tenantId(tenantId)
                .esntlId(esntlId)
                .branchId(branchId)
                .groupId(groupId)
                .positionId(positionId)
                .roleIds(roleIds)
                .permissions(permissions)
                .authorities(authorities)
                .build();

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * Validate token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Get claims from token
     */
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Get subject (esntlId) from token
     */
    public String getSubjectFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Get tenantId from token
     */
    public String getTenantIdFromToken(String token) {
        return getClaims(token).get(TENANT_ID_KEY, String.class);
    }

    /**
     * Get access token validity in milliseconds
     */
    public long getAccessTokenValidityInMilliseconds() {
        return accessTokenValidityInSeconds * 1000;
    }

    /**
     * Get refresh token validity in milliseconds
     */
    public long getRefreshTokenValidityInMilliseconds() {
        return refreshTokenValidityInSeconds * 1000;
    }

    /**
     * Get access token validity in seconds
     */
    public long getAccessTokenValidityInSeconds() {
        return accessTokenValidityInSeconds;
    }

    /**
     * Get refresh token validity in seconds
     */
    public long getRefreshTokenValidityInSeconds() {
        return refreshTokenValidityInSeconds;
    }
}
