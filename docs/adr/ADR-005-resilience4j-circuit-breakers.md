# ADR-005: Resilience4j Circuit Breakers & Gateway Fallbacks

## Status
Accepted

## Context
Synchronous REST dependencies and inter-service HTTP queries can trigger cascading outages if downstream services experience latency spikes or downtime.

## Decision
1. **Resilience4j Circuit Breakers**:
   - Configured on all OpenFeign clients and API Gateway routes.
   - Sliding window size: 20 calls.
   - Failure rate threshold: 50%.
   - Wait duration in open state: 30 seconds.
2. **Timeouts**:
   - Connect timeout: 2,000 ms.
   - Read timeout: 5,000 ms.
3. **Idempotent Retry**:
   - Configured for HTTP GET requests only (maximum 2 retries with exponential backoff).
4. **Standard Fallbacks**:
   - Gateway returns HTTP 503 Service Unavailable with an RFC 7807 standard error body when a circuit breaker opens.
   - Frontend SPA gracefully catches 503 and presents cached last-known state with an offline warning banner.

## Consequences
- **Positive**: Fast failure; prevents thread exhaustion across the API Gateway.
- **Negative**: Requires careful tuning of sliding windows for low-traffic endpoints.
