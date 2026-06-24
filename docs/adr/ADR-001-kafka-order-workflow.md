# ADR 001: Kafka Event-Driven Architecture for Order Workflow

## Status
Accepted

## Context
When a customer places an order, the system needs to orchestrate multiple steps: validating inventory, deducting stock, calculating prices, clearing the cart, and sending a notification. Using synchronous REST calls for this entire workflow creates tight coupling and increases the risk of cascading failures.

## Decision
We decided to use Apache Kafka as the event broker to implement an event-driven choreography pattern for the order workflow.
1. The `order-service` creates the order in a `CREATED` state and publishes an `OrderPlacedEvent` to the `orders` topic.
2. The `inventory-service` listens to this event, validates and deducts stock, and publishes `InventoryUpdatedEvent`.
3. The `notification-service` listens to `OrderPlacedEvent` to send emails.

## Consequences
- **Positive:** Loose coupling between services. Improved fault tolerance (if notification service is down, events are queued and processed later).
- **Negative:** Increased operational complexity (need to manage Kafka clusters). Eventual consistency makes UI updates slightly more complex.
