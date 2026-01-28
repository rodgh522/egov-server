package com.example.egov.domain.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Getter
public class CustomUserDetails extends User {

    private final String userId;
    private final String tenantId;
    private final String esntlId;
    private final String branchId;
    private final String groupId;
    private final String positionId;
    private final Set<String> roleIds;
    private final Set<String> permissions;

    private CustomUserDetails(Builder builder) {
        super(builder.userId, builder.password, builder.authorities);
        this.userId = builder.userId;
        this.tenantId = builder.tenantId;
        this.esntlId = builder.esntlId;
        this.branchId = builder.branchId;
        this.groupId = builder.groupId;
        this.positionId = builder.positionId;
        this.roleIds = builder.roleIds != null ? builder.roleIds : Collections.emptySet();
        this.permissions = builder.permissions != null ? builder.permissions : Collections.emptySet();
    }

    public static Builder customBuilder() {
        return new Builder();
    }

    public boolean hasPermission(String permissionCode) {
        return permissions.contains(permissionCode);
    }

    public boolean hasRole(String roleId) {
        return roleIds.contains(roleId);
    }

    public static class Builder {
        private String userId;
        private String password;
        private String tenantId;
        private String esntlId;
        private String branchId;
        private String groupId;
        private String positionId;
        private Set<String> roleIds;
        private Set<String> permissions;
        private Collection<? extends GrantedAuthority> authorities;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder esntlId(String esntlId) {
            this.esntlId = esntlId;
            return this;
        }

        public Builder branchId(String branchId) {
            this.branchId = branchId;
            return this;
        }

        public Builder groupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder positionId(String positionId) {
            this.positionId = positionId;
            return this;
        }

        public Builder roleIds(Set<String> roleIds) {
            this.roleIds = roleIds;
            return this;
        }

        public Builder permissions(Set<String> permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder authorities(Collection<? extends GrantedAuthority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public CustomUserDetails build() {
            if (authorities == null) {
                authorities = Collections.emptyList();
            }
            return new CustomUserDetails(this);
        }
    }
}
