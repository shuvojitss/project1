# ADR-001: Transactional Outbox Pattern with Scheduled Polling & SKIP LOCKED

## Status
Accepted

## Context
In a distributed microservice architecture, publishing domain events directly to a message broker (RabbitMQ) within a database transaction can lead to critical dual-write failure modes:
1. The database transaction commits, but the network fails before publishing the event to RabbitMQ (causing lost updates).
2. The event is published to RabbitMQ, but the database transaction rolls back due to a constraint violation (causing phantom events).

Distributed two-phase commits (XA transactions) introduce high latency, locking bottlenecks, and operational brittleness.

## Decision
We mandate the **Transactional Outbox Pattern** across all 10 domain services using `outbox_event` tables:
1. Each business mutation inserts or updates domain entities and simultaneously inserts an `outbox_event` record within the same MySQL ACID transaction.
2. An asynchronous scheduler runs every 500ms, reading up to 100 `PENDING` outbox events using:
   ```sql
   SELECT * FROM outbox_event WHERE status = 'PENDING' ORDER BY created_at ASC LIMIT 100 FOR UPDATE SKIP LOCKED
   ```
3. `SKIP LOCKED` ensures multiple service instances safely process disjoint slices of outbox rows without row lock contention.
4. The relay publishes the event to RabbitMQ with publisher confirms enabled. Upon confirm, the status is set to `SENT`. On failure, `attempts` is incremented, and after 10 failed attempts, it is flagged as `FAILED`.
5. A nightly job deletes `SENT` rows older than 7 days.

## Consequences
- **Positive**: Absolute zero lost events; guarantees at-least-once message delivery; no distributed locking.
- **Negative**: Consumers must be idempotent (addressed by ADR-002 and `processed_message` table).
