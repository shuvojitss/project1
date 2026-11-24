package com.infy.scm360.identity.service;

import com.infy.scm360.common.exception.DomainException;
import com.infy.scm360.common.exception.StateConflictException;
import com.infy.scm360.common.exception.ValidationException;
import com.infy.scm360.identity.domain.RefreshToken;
import com.infy.scm360.identity.domain.Role;
import com.infy.scm360.identity.domain.User;
import com.infy.scm360.identity.domain.UserLocation;
import com.infy.scm360.identity.dto.LoginRequest;
import com.infy.scm360.identity.dto.LoginResponse;
import com.infy.scm360.identity.dto.RefreshTokenRequest;
import com.infy.scm360.identity.dto.UserResponse;
import com.infy.scm360.identity.repository.RefreshTokenRepository;
import com.infy.scm360.identity.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuditService auditService;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService,
            AuditService auditService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.auditService = auditService;
    }

    @Transactional(noRollbackFor = {ValidationException.class, StateConflictException.class})
    public LoginResponse login(LoginRequest request, String ipAddress) {
        User user = userRepository.findByUsernameOrEmail(request.usernameOrEmail())
                .orElseThrow(() -> new ValidationException("Invalid username/email or password"));

        // Check account lockout
        if (user.isAccountLocked()) {
            auditService.record(user.getId(), "LOGIN_LOCKED_ATTEMPT", "Attempt to login while locked until " + user.getLockedUntil(), ipAddress);
            throw new StateConflictException("Account is locked due to multiple failed login attempts. Try again after " + user.getLockedUntil());
        }

        // Check password
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= 5) {
                user.setLockedUntil(Instant.now().plus(15, ChronoUnit.MINUTES));
                auditService.record(user.getId(), "ACCOUNT_LOCKED", "Account locked after 5 failed attempts", ipAddress);
            } else {
                auditService.record(user.getId(), "LOGIN_FAILED", "Failed attempt " + attempts, ipAddress);
            }
            userRepository.save(user);
            throw new ValidationException("Invalid username/email or password");
        }

        // Reset failed attempts on success
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        String accessToken = tokenService.generateAccessToken(user);
        RefreshToken refreshToken = tokenService.generateRefreshToken(user);

        auditService.record(user.getId(), "LOGIN_SUCCESS", "User logged in successfully", ipAddress);

        return new LoginResponse(
            accessToken,
            refreshToken.getToken(),
            900, // 15 minutes
            mapToUserResponse(user)
        );
    }

    @Transactional
    public LoginResponse refresh(RefreshTokenRequest request, String ipAddress) {
        RefreshToken token = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new ValidationException("Invalid or unknown refresh token"));

        if (token.isRevoked() || token.isExpired()) {
            auditService.record(token.getUser().getId(), "REFRESH_TOKEN_INVALID", "Attempted use of expired or revoked token", ipAddress);
            throw new ValidationException("Refresh token is expired or revoked. Please log in again.");
        }

        User user = token.getUser();

        // Rotate token: revoke old token
        token.setRevoked(true);
        refreshTokenRepository.save(token);

        // Issue new tokens
        String newAccessToken = tokenService.generateAccessToken(user);
        RefreshToken newRefreshToken = tokenService.generateRefreshToken(user);

        auditService.record(user.getId(), "TOKEN_REFRESH", "Token rotated successfully", ipAddress);

        return new LoginResponse(
            newAccessToken,
            newRefreshToken.getToken(),
            900,
            mapToUserResponse(user)
        );
    }

    @Transactional
    public void logout(String userId) {
        refreshTokenRepository.revokeAllUserTokens(userId);
        auditService.record(userId, "LOGOUT", "User logged out, all refresh tokens revoked", null);
    }

    public static UserResponse mapToUserResponse(User user) {
        List<String> roles = user.getRoles().stream().map(Role::getName).toList();
        List<String> locations = user.getLocations().stream().map(UserLocation::getLocationId).toList();
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName(),
            user.getStatus(),
            roles,
            locations,
            user.getSupplierId()
        );
    }
}
