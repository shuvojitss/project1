package com.infy.scm360.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/service-unavailable")
    public ResponseEntity<Map<String, Object>> fallback(ServerWebExchange exchange) {
        String correlationId = exchange.getRequest().getHeaders().getFirst("X-Correlation-Id");
        if (correlationId == null) {
            correlationId = "N/A";
        }

        Map<String, Object> error = Map.of(
            "type", "https://scm360.infy/errors/service-unavailable",
            "title", "Service Unavailable",
            "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
            "detail", "The target service is temporarily unavailable or circuit breaker is open. Please retry later.",
            "instance", exchange.getRequest().getPath().value(),
            "correlationId", correlationId,
            "errors", List.of(),
            "timestamp", Instant.now().toString()
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }
}
