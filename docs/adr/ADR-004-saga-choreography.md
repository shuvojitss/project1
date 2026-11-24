# ADR-004: Event Choreography for Order Fulfilment Saga

## Status
Accepted

## Context
Order fulfilment spans multiple business domains: order creation, stock reservation, picking/packing, shipment routing, and delivery. Orchestrator-based sagas can introduce single points of failure, while choreography requires clear state ownership.

## Decision
We adopt **Event-Driven Choreography with Central Business State Ownership**:
1. `order-service` is the sole writer of customer order status.
2. Participating services publish domain facts:
   - `order-service` -> `order.order.created`
   - `inventory-service` -> `inventory.stock.reserved` or `inventory.stock.insufficient`
   - `warehouse-service` -> `warehouse.order.packed`
   - `transportation-service` -> `transport.shipment.dispatched` and `transport.shipment.delivered`
3. If stock is insufficient, `inventory-service` publishes `inventory.stock.insufficient`. `order-service` transitions the order to `BACKORDERED` and triggers compensating events to release any partial reservations.
4. If an order is cancelled by user action, `order-service` emits `order.order.cancelled`, instructing `inventory-service` to immediately return reserved stock to available inventory.

## Consequences
- **Positive**: Loose coupling; high throughput; zero blocking RPC calls during checkout.
- **Negative**: Tracing requires mandatory correlation ID propagation (`X-Correlation-Id`).
