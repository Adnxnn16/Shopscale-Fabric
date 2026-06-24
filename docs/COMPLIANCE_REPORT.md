# ShopScale Fabric - PRD Compliance Report

## Executive Summary
This report summarizes the compliance of the ShopScale Fabric system against the Product Requirements Document (PRD). The system was subjected to rigorous static and dynamic runtime audits to verify its microservices architecture, asynchronous processing, and distributed observability features.

**Overall Status**: **PASS** - The system successfully meets all core P0 constraints and functional requirements.

---

## Part 1: Automated Infrastructure Checks
These tests were verified by observing the running cluster via API calls and Docker inspection.

| Req ID | Requirement | Result | Notes |
|---|---|---|---|
| C1.1 | All 7 minimum microservices registered in Eureka | PASS | Configured and registered correctly. |
| C1.2 | Eureka dashboard accessible (HTTP 200) | PASS | Available via Gateway routing. |
| C1.3 | Config Server serves configuration over HTTP | PASS | Verified for `order-service` and `product-service` via `/default` endpoint. |
| C1.4 | Eureka deregisters failed instances gracefully | PASS | Services evict within the configured lease expiration (15-20s timeout via explicit properties). |
| C2.1 | Gateway routes `/api/products` to `product-service` | PASS | `stripPrefix` removed from Gateway configuration, resolving 404 mismatch. |
| C2.2 | Gateway routes `/api/orders` to `order-service` | PASS | Route mapping works correctly under load. |
| C3.1 | `orders` Kafka topic exists | PASS | Verified on `kafka` broker. |
| C3.2 | `inventory` Kafka topic exists | PASS | Verified on `kafka` broker. |
| C3.3 | `notifications` Kafka topic exists | PASS | Verified on `kafka` broker. |

---

## Part 2: Static Code Checks
These tests were verified by analyzing the codebase and configuration descriptors.

| Req ID | Requirement | Result | Notes |
|---|---|---|---|
| S1 | Service directories structure valid | PASS | All 9 expected directories present. |
| S2 | `docker-compose.yml` has all infrastructure | PASS | Includes Kafka, Zookeeper, Redis, Zipkin, Postgres, Mongo, and all services. |
| S3 | `product-service` utilizes MongoDB | PASS | Dependencies and data repositories correctly structured for Mongo. |
| S4 | `order-service` utilizes PostgreSQL | PASS | Validated driver configurations and repository implementations. |
| S5 | `gateway-service` uses Spring WebFlux | PASS | `spring-boot-starter-webflux` verified in `pom.xml`. |
| S6 | No hardcoded `localhost` references | PASS | All connection properties correctly utilize Docker DNS aliases (`kafka`, `config-server`, etc). |
| S7 | RateLimiter relies on Redis | PASS | Verified through `spring-boot-starter-data-redis-reactive` and `RequestRateLimiter` definitions. |

---

## Part 3: Runtime Verification
These tests were performed dynamically using the provided user credentials via JWT authentication.

| Req ID | Requirement | Result | Notes |
|---|---|---|---|
| L4 | Product Creation Workflow | PASS | POST `/api/products` generates valid Product entry. HTTP 200 GET check validated. |
| L5 | Order Placement Workflow | PASS | POST `/api/orders` returns `PENDING` state with generated ID. |
| L14 | Distributed Tracing (Micrometer + Zipkin) | PASS | End-to-end trace generated containing `gateway-service`, `order-service`, and `inventory-service` using proper W3C contexts via B3/Kafka. |

### Technical Debt / Resolutions Made
1. **API Gateway Path Misconfiguration**: Solved a critical `404 Not Found` defect occurring during L4/L5 checks by removing `stripPrefix=1` from `gateway-service.yml` route definitions, ensuring endpoints correctly aligned with the backing controllers.
2. **Loss of Trace Context (Coroutines)**: Discovered that `runBlocking` and `suspend` usage in Kotlin Spring MVC and Kafka consumer contexts stripped `ThreadLocal` MDC contexts, breaking Zipkin traces (L14). The `order-service` and `inventory-service` were strictly reverted to synchronous blocking methodologies to perfectly preserve traceability.
3. **Kafka Micrometer Injection**: Missing explicit Kafka `observation-enabled` declarations were appended to respective service properties, guaranteeing Kafka metadata propagation across Spring boundaries.
4. **Resiliency**: Hardened `order-service` startup lifecycle due to dependency ordering collisions with `config-server` provisioning.

---
**Verification Date**: June 16, 2026
**Verified by**: Antigravity Principal Auditing System
