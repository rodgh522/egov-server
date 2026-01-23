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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT Token Provider
 * - Handles JWT token generation and validation
 * - Stores userId, tenantId, and roles as claims
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

    @PostConstruct
    protected void init() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Create access token with user information and authorities
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
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        String esntlId = claims.getSubject();
        String userId = claims.get(USER_ID_KEY, String.class);
        String tenantId = claims.get(TENANT_ID_KEY, String.class);
        String authoritiesString = claims.get(AUTHORITIES_KEY, String.class);

        Collection<? extends GrantedAuthority> authorities;
        if (authoritiesString != null && !authoritiesString.isEmpty()) {
            authorities = Arrays.stream(authoritiesString.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } else {
            authorities = java.util.Collections.emptyList();
        }

        CustomUserDetails principal = new CustomUserDetails(
                userId,
                "",
                tenantId,
                authorities
        );
        principal.setEsntlId(esntlId);

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
