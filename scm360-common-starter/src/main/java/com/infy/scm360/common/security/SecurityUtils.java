package com.infy.scm360.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static Optional<ScmUserPrincipal> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof ScmUserPrincipal principal) {
            return Optional.of(principal);
        }
        return Optional.empty();
    }

    public static String getCurrentUserId() {
        return getCurrentUser().map(ScmUserPrincipal::getUserId).orElse("anonymous");
    }

    public static boolean hasRole(String role) {
        String target = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase(target));
    }

    public static boolean isAdminOrExecutive() {
        return hasRole("ADMIN") || hasRole("EXECUTIVE");
    }

    public static boolean hasLocationAccess(String locationId) {
        if (locationId == null || isAdminOrExecutive()) {
            return true;
        }
        return getCurrentUser()
                .map(u -> u.getLocationIds().contains(locationId))
                .orElse(false);
    }

    public static List<String> getCurrentUserLocationIds() {
        return getCurrentUser().map(ScmUserPrincipal::getLocationIds).orElse(List.of());
    }

    public static String getCurrentSupplierId() {
        return getCurrentUser().map(ScmUserPrincipal::getSupplierId).orElse(null);
    }
}
