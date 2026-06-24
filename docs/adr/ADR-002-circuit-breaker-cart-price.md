# ADR 002: Circuit Breaker Pattern between Cart and Price Services

## Status
Accepted

## Context
The `cart-service` depends on the `price-service` to retrieve the latest product prices before finalizing cart subtotal. If the `price-service` is slow or unavailable, the `cart-service` thread pool could exhaust, bringing down the cart functionality entirely.

## Decision
We implemented the Circuit Breaker pattern using Resilience4j around the `cart-service` client calls to `price-service`. 
If the `price-service` fails frequently, the circuit opens. During this time, the `cart-service` falls back to cached prices or allows the item to be added to the cart with a flag `priceAvailable: false` (to be resolved at checkout).

## Consequences
- **Positive:** Cart remains functional even if pricing is down. Prevents cascading failures.
- **Negative:** Requires handling "price unavailable" scenarios in the frontend UI.
