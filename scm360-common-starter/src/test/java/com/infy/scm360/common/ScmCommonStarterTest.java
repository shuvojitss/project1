package com.infy.scm360.common;

import com.infy.scm360.common.error.ErrorResponse;
import com.infy.scm360.common.event.EventEnvelope;
import com.infy.scm360.common.event.EventTypes;
import com.infy.scm360.common.security.JwtTokenValidator;
import com.infy.scm360.common.security.RsaKeyUtils;
import com.infy.scm360.common.security.ScmUserPrincipal;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import java.security.PrivateKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScmCommonStarterTest {

    @Test
    void testEventEnvelopeCreation() {
        EventEnvelope<String> event = EventEnvelope.of(
            EventTypes.INVENTORY_STOCK_RESERVED,
            "inventory-service",
            "{\"sku\": \"SKU-1001\"}",
            "corr-123",
            "cause-456"
        );

        assertNotNull(event.eventId());
        assertEquals(EventTypes.INVENTORY_STOCK_RESERVED, event.eventType());
        assertEquals("inventory-service", event.producer());
        assertEquals("corr-123", event.correlationId());
        assertEquals("cause-456", event.causationId());
        assertEquals(1, event.eventVersion());
    }

    @Test
    void testErrorResponseFormat() {
        ErrorResponse error = ErrorResponse.of(
            "https://scm360.infy/errors/insufficient-stock",
            "Insufficient stock",
            409,
            "Requested 50 of SKU-1001 but only 32 available at WH-BLR",
            "/api/v1/orders",
            "c1f6c4de-1f0a-4a3a-8e7c-b6e0d5e0a111",
            List.of()
        );

        assertEquals(409, error.status());
        assertEquals("https://scm360.infy/errors/insufficient-stock", error.type());
        assertEquals("/api/v1/orders", error.instance());
        assertEquals("c1f6c4de-1f0a-4a3a-8e7c-b6e0d5e0a111", error.correlationId());
    }

    @Test
    void testJwtTokenValidation() {
        PrivateKey privateKey = RsaKeyUtils.getDevPrivateKey();
        String token = Jwts.builder()
            .subject("usr-12345")
            .claim("username", "admin_user")
            .claim("roles", List.of("ADMIN", "PROCUREMENT_MANAGER"))
            .claim("locationIds", List.of("wh-blr-1", "wh-bom-1"))
            .issuedAt(new Date())
            .expiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
            .signWith(privateKey, Jwts.SIG.RS256)
            .compact();

        JwtTokenValidator validator = new JwtTokenValidator("");
        ScmUserPrincipal principal = validator.validateAndExtract(token);

        assertNotNull(principal);
        assertEquals("usr-12345", principal.getUserId());
        assertEquals("admin_user", principal.getUsername());
        assertTrue(principal.getRoles().contains("ADMIN"));
        assertTrue(principal.getLocationIds().contains("wh-blr-1"));
        assertEquals(2, principal.getAuthorities().size());
    }
}
