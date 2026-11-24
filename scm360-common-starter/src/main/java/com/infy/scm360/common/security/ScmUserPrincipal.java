package com.infy.scm360.common.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

public class ScmUserPrincipal implements UserDetails, Principal {

    private final String userId;
    private final String username;
    private final List<String> roles;
    private final List<String> locationIds;
    private final String supplierId;
    private final Collection<? extends GrantedAuthority> authorities;

    public ScmUserPrincipal(String userId, String username, List<String> roles, List<String> locationIds, String supplierId) {
        this.userId = userId;
        this.username = username != null ? username : userId;
        this.roles = roles != null ? roles : List.of();
        this.locationIds = locationIds != null ? locationIds : List.of();
        this.supplierId = supplierId;
        this.authorities = this.roles.stream()
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public String getUserId() { return userId; }
    public List<String> getRoles() { return roles; }
    public List<String> getLocationIds() { return locationIds; }
    public String getSupplierId() { return supplierId; }

    @Override public String getName() { return username; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return ""; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
