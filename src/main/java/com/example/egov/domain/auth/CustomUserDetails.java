package com.example.egov.domain.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {

    private final String userId;
    private final String tenantId;

    public CustomUserDetails(String userId, String password, String tenantId,
            Collection<? extends GrantedAuthority> authorities) {
        super(userId, password, authorities);
        this.userId = userId;
        this.tenantId = tenantId;
    }
}
