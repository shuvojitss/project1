package com.infy.scm360.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;

@Component
public class JwtAuthenticationGatewayFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationGatewayFilter.class);

    private static final List<String> PUBLIC_PATH_PREFIXES = List.of(
        "/api/v1/auth/login",
        "/api/v1/auth/refresh",
        "/fallback",
        "/actuator"
    );

    private final PublicKey publicKey;
    private final JwtParser jwtParser;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationGatewayFilter(@Value("${jwt.public-key:}") String publicKeyPem) {
        PublicKey key;
        try {
            if (publicKeyPem != null && !publicKeyPem.isBlank()) {
                String clean = publicKeyPem.replace("-----BEGIN PUBLIC KEY-----", "")
                                           .replace("-----END PUBLIC KEY-----", "")
                                           .replaceAll("\\s+", "");
                byte[] decoded = Base64.getDecoder().decode(clean);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                key = kf.generatePublic(new X509EncodedKeySpec(decoded));
            } else {
                key = getDevPublicKey();
            }
        } catch (Exception e) {
            log.warn("Failed to parse public key, falling back to development RSA key", e);
            key = getDevPublicKey();
        }
        this.publicKey = key;
        this.jwtParser = Jwts.parser().verifyWith(this.publicKey).build();
    }

    private static PublicKey getDevPublicKey() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048, new SecureRandom(new byte[]{42, 13, 37, 99}));
            return generator.generateKeyPair().getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 1. Ensure Correlation ID
        String correlationId = request.getHeaders().getFirst("X-Correlation-Id");
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        // Add correlation ID to response
        exchange.getResponse().getHeaders().set("X-Correlation-Id", correlationId);

        // Check if public path
        for (String prefix : PUBLIC_PATH_PREFIXES) {
            if (path.startsWith(prefix)) {
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-Correlation-Id", correlationId)
                        .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            }
        }

        // 2. Validate Bearer Token
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorizedResponse(exchange, "Missing or invalid Authorization header", correlationId);
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            String userId = claims.getSubject();
            List<?> rolesList = claims.get("roles", List.class);
            String rolesStr = rolesList != null ? String.join(",", rolesList.stream().map(Object::toString).toList()) : "";
            List<?> locList = claims.get("locationIds", List.class);
            String locStr = locList != null ? String.join(",", locList.stream().map(Object::toString).toList()) : "";
            String supplierId = claims.get("supplierId", String.class);

            ServerHttpRequest.Builder reqBuilder = request.mutate()
                    .header("X-Correlation-Id", correlationId)
                    .header("X-User-Id", userId)
                    .header("X-User-Roles", rolesStr)
                    .header("X-Location-Ids", locStr);

            if (supplierId != null && !supplierId.isBlank()) {
                reqBuilder.header("X-Supplier-Id", supplierId);
            }

            return chain.filter(exchange.mutate().request(reqBuilder.build()).build());

        } catch (Exception e) {
            log.debug("JWT verification failed: {}", e.getMessage());
            return unauthorizedResponse(exchange, "Token signature invalid or expired: " + e.getMessage(), correlationId);
        }
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String detail, String correlationId) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> errorBody = Map.of(
            "type", "https://scm360.infy/errors/unauthorized",
            "title", "Unauthorized",
            "status", HttpStatus.UNAUTHORIZED.value(),
            "detail", detail,
            "instance", exchange.getRequest().getPath().value(),
            "correlationId", correlationId,
            "errors", List.of(),
            "timestamp", Instant.now().toString()
        );

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(errorBody);
        } catch (JsonProcessingException e) {
            bytes = "{}".getBytes(StandardCharsets.UTF_8);
        }

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
