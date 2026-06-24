# ADR 003: Centralized JWT Authentication in API Gateway

## Status
Accepted

## Context
Multiple microservices (order, cart, inventory, etc.) need to ensure that incoming requests are authenticated and authorized. Re-implementing auth logic in every service leads to duplication and potential security vulnerabilities.

## Decision
We implemented centralized authentication at the API Gateway level using a Spring Cloud Gateway `GlobalFilter` (the `JwtAuthenticationFilter`).
The Gateway validates the JWT token. If valid, it extracts the user ID and roles, adds them as HTTP headers (e.g., `X-User-Id`), and forwards the request to downstream services. Downstream services trust the internal network and read these headers.

## Consequences
- **Positive:** Simplified downstream services (no security configuration needed). Central point of control for auth.
- **Negative:** The Gateway becomes a single point of failure and a potential bottleneck. Internal traffic must be secured (e.g., via mTLS) if deployed in an untrusted environment.
