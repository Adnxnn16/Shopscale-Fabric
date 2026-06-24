# ShopScale Fabric — Resilience Test Runbook

This runbook documents how to simulate, observe, and verify each failure scenario in the ShopScale Fabric platform. All commands assume the full Docker Compose stack is running (`docker compose up -d --build`).

> **Prerequisites**
> - Docker Desktop running with at least 8 GB memory allocated
> - `TOKEN` environment variable set to a valid JWT signed with the gateway secret
> - All 7 services showing `UP` in Eureka at `http://localhost:8761`

---

## Scenario 1: Price Service Failure (Circuit Breaker)

**Objective**: Verify that the Cart Service degrades gracefully when Price Service is unavailable, with no 500 errors and a response time < 50ms after the circuit opens.

**Architecture**: `Cart Service → [Circuit Breaker: priceService] → Price Service`

**Configuration** (`config-repo/cart-service.yml`):
```yaml
resilience4j:
  circuitbreaker:
    instances:
      priceService:
        slidingWindowType: COUNT_BASED
        slidingWindowSize: 10
        failureRateThreshold: 50          # opens at 5+ failures in 10 calls
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
```

### Steps

**1. Stop the Price Service**
```bash
docker compose stop price-service
```

**2. Trigger the circuit breaker (add items to cart)**
```bash
for i in $(seq 1 12); do
  curl -s -o /dev/null -w "Request $i: %{http_code} (%{time_total}s)\n" \
    -X POST http://localhost:8080/api/cart/test-user/items \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"productId":"prod-001","quantity":1}'
done
```

**Expected output:**
- All responses return `HTTP 200` (not 500)
- Response body contains `"priceAvailable": false`
- After ~5 failures, response time drops to < 10ms (fallback executes without waiting)

**3. Verify circuit is OPEN**
```bash
curl -s http://localhost:8086/actuator/health | python -m json.tool | grep -A3 "priceService"
```
**Expected:** `"status": "CIRCUIT_OPEN"`

```bash
curl -s http://localhost:8086/actuator/metrics/resilience4j.circuitbreaker.state
```
**Expected:** State metric shows `state=open`

**4. Restart Price Service**
```bash
docker compose start price-service
```

**5. Wait 30 seconds for HALF_OPEN transition**
```bash
sleep 30
```

**6. Trigger 3 trial calls (HALF_OPEN probe)**
```bash
for i in $(seq 1 3); do
  curl -s -X POST http://localhost:8080/api/cart/test-user/items \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"productId":"prod-001","quantity":1}' | python -m json.tool | grep priceAvailable
done
```
**Expected:** `"priceAvailable": true` — circuit transitions HALF_OPEN → CLOSED.

**7. Verify circuit is CLOSED**
```bash
curl -s http://localhost:8086/actuator/health | python -m json.tool | grep -A3 "priceService"
```
**Expected:** `"status": "CIRCUIT_CLOSED"` (may display as `UP`).

**8. Verify normal pricing is restored**
```bash
curl -s http://localhost:8080/api/cart/test-user \
  -H "Authorization: Bearer $TOKEN" | python -m json.tool | grep -E "price|available"
```
**Expected:** Real price populated, `"priceAvailable": true`.

---

## Scenario 2: Inventory Service Restart (Kafka Durability)

**Objective**: Verify that `OrderPlacedEvent` messages are durably retained in Kafka while Inventory Service is down, and consumed exactly once on restart (idempotency key enforced).

**Architecture**: `Order Service → [Kafka: orders topic] → Inventory Service (group: inventory-service-group)`

### Steps

**1. Stop Inventory Service**
```bash
docker compose stop inventory-service
```

**2. Place an order (Order Service is independent — should succeed)**
```bash
curl -s -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"userId":"recovery-test-user","paymentMethod":"CREDIT_CARD","shippingAddress":"456 Recovery Ln"}' \
  | python -m json.tool
```
**Expected:** HTTP 200/201, order has `"status": "PENDING"`.

**3. Verify Kafka has a pending message (LAG > 0)**
```bash
docker exec kafka kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --describe --group inventory-service-group
```
**Expected:** `LAG` column shows a value > 0 for the `orders` topic.

**4. Restart Inventory Service**
```bash
docker compose start inventory-service
sleep 30
```

**5. Verify Kafka lag is now 0 (message processed)**
```bash
docker exec kafka kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --describe --group inventory-service-group
```
**Expected:** `LAG = 0`

**6. Verify inventory was decremented exactly once**
```bash
curl -s http://localhost:8080/api/inventory/prod-001 \
  -H "Authorization: Bearer $TOKEN" | python -m json.tool | grep quantity
```
**Expected:** Stock reduced by the ordered quantity. Re-processing the same event must NOT reduce it a second time (idempotency key in `ProcessedEventJpa` prevents this).

**7. Verify notification was also delivered**
```bash
docker compose logs notification-service --tail=20 | grep "email"
```
**Expected:** Log shows mock email sent for the order (Notification Service uses its own consumer group `notification-service-group` and processed independently).

---

## Scenario 3: Rate Limit Enforcement (Redis Token Bucket)

**Objective**: Verify that the API Gateway enforces a burst limit of 10 requests with a replenish rate of 2/second per IP, returning HTTP 429 on excess requests.

**Configuration** (`RateLimiterConfig.kt`):
```kotlin
RedisRateLimiter(2, 10, 1)  // replenishRate=2/s, burstCapacity=10, cost=1
```

### Steps

**1. Send 105 rapid requests and count throttled responses**
```bash
THROTTLED=0
for i in $(seq 1 105); do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" \
    http://localhost:8080/api/products \
    -H "Authorization: Bearer $TOKEN")
  echo "Request $i: $STATUS"
  if [ "$STATUS" = "429" ]; then
    THROTTLED=$((THROTTLED + 1))
  fi
done
echo "Total throttled: $THROTTLED"
```
**Expected:** At least 1 (typically many more) `429 Too Many Requests` responses.

**2. Verify Redis rate limit keys exist**
```bash
docker exec redis redis-cli keys "*rate*"
```
**Expected:** Keys following the pattern `request_rate_limiter.{ip}.tokens` and `.timestamp` exist.

**3. Verify 429 response body**
```bash
curl -v http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN" 2>&1 | grep -E "< HTTP|429|Retry"
```
**Expected:** HTTP/1.1 429 response.

**4. Wait for token bucket to refill and verify requests succeed again**
```bash
sleep 10
curl -s -o /dev/null -w "%{http_code}" \
  http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN"
```
**Expected:** `200`

---

## Diagnostic Quick Reference

| Symptom | First Check | Command |
|:---|:---|:---|
| Cart returns 500 after price-service down | Circuit breaker not triggered yet (need 5+ failures) | `curl localhost:8086/actuator/health` |
| Circuit stuck OPEN after restart | `waitDurationInOpenState: 10s` — wait longer | `curl localhost:8086/actuator/health` |
| Inventory not processing after restart | Check consumer group offset committed | `kafka-consumer-groups.sh --describe ...` |
| No 429 despite many requests | Redis not connected to gateway | `docker compose logs gateway-service \| grep redis` |
| Zipkin shows no traces | Zipkin URL may be `localhost` in config | Check `config-repo/*.yml` for zipkin endpoint |
| Services not in Eureka | Config Server not serving configs | `curl localhost:8888/cart-service/default` |
