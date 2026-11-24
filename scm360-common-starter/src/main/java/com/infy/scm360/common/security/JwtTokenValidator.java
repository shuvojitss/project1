package com.infy.scm360.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtTokenValidator {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenValidator.class);

    private final PublicKey publicKey;
    private final JwtParser jwtParser;

    public JwtTokenValidator(@Value("${jwt.public-key:}") String publicKeyPem) {
        this.publicKey = RsaKeyUtils.parsePublicKey(publicKeyPem);
        this.jwtParser = Jwts.parser()
                .verifyWith(this.publicKey)
                .build();
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public ScmUserPrincipal validateAndExtract(String token) {
        try {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            String userId = claims.getSubject();
            String username = claims.get("username", String.class);
            if (username == null) {
                username = claims.get("preferred_username", String.class);
            }
            if (username == null) {
                username = userId;
            }

            List<String> roles = new ArrayList<>();
            Object rolesObj = claims.get("roles");
            if (rolesObj instanceof List<?> list) {
                for (Object item : list) {
                    roles.add(item.toString());
                }
            }

            List<String> locationIds = new ArrayList<>();
            Object locObj = claims.get("locationIds");
            if (locObj instanceof List<?> list) {
                for (Object item : list) {
                    locationIds.add(item.toString());
                }
            }

            String supplierId = claims.get("supplierId", String.class);

            return new ScmUserPrincipal(userId, username, roles, locationIds, supplierId);
        } catch (Exception e) {
            log.debug("JWT verification failed: {}", e.getMessage());
            return null;
        }
    }
}
