package com.infy.scm360.identity.service;

import com.infy.scm360.common.security.RsaKeyUtils;
import com.infy.scm360.identity.domain.RefreshToken;
import com.infy.scm360.identity.domain.Role;
import com.infy.scm360.identity.domain.User;
import com.infy.scm360.identity.domain.UserLocation;
import com.infy.scm360.identity.repository.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PrivateKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class TokenService {

    private final PrivateKey privateKey;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenService(
            @Value("${jwt.private-key:}") String privateKeyPem,
            RefreshTokenRepository refreshTokenRepository) {
        this.privateKey = RsaKeyUtils.parsePrivateKey(privateKeyPem);
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plus(15, ChronoUnit.MINUTES);

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        List<String> locationIds = user.getLocations().stream()
                .map(UserLocation::getLocationId)
                .toList();

        var builder = Jwts.builder()
                .subject(user.getId())
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .claim("roles", roles)
                .claim("locationIds", locationIds)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp));

        if (user.getSupplierId() != null && !user.getSupplierId().isBlank()) {
            builder.claim("supplierId", user.getSupplierId());
        }

        return builder.signWith(privateKey, Jwts.SIG.RS256).compact();
    }

    @Transactional
    public RefreshToken generateRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);

        RefreshToken refreshToken = new RefreshToken(
            UUID.randomUUID().toString(),
            user,
            token,
            expiresAt
        );

        return refreshTokenRepository.save(refreshToken);
    }
}
