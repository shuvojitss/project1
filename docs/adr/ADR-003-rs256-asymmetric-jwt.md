# ADR-003: Asymmetric Cryptographic Authentication (RS256 JWT)

## Status
Accepted

## Context
Symmetric JWT signing (HS256) requires every microservice to share the same secret key. If any microservice is compromised, an attacker can forge valid tokens for the entire ecosystem.

## Decision
1. We enforce **RS256 (RSA Signature with SHA-256)** asymmetric cryptography for authentication:
   - `identity-service` alone holds the 2048-bit RSA Private Key (`private_key.pem`) and signs access tokens.
   - All other 9 downstream microservices and `api-gateway` hold only the RSA Public Key (`public_key.pem`) via `scm360-common-starter`.
2. **Token Lifecycle**:
   - Access Token: 15-minute validity containing claims `sub`, `roles`, `locationIds`, `supplierId`.
   - Refresh Token: 7-day validity, stored securely in MySQL (`refresh_tokens`), revocable, and rotated upon every use.
3. **Defense in Depth**:
   - The API Gateway verifies token signature and expiration, stripping untrusted user headers.
   - Downstream services independently re-verify the Bearer token via `JwtAuthenticationFilter` in `scm360-common-starter`.

## Consequences
- **Positive**: Compromising a downstream service does not compromise token issuance.
- **Negative**: Key rotation requires deploying updated public keys or providing a JWKS endpoint.
