package IOT.coldchain.application.port1.out;

import IOT.coldchain.domain.event.DomainEvent;

import java.util.List;

/**
 * OUTBOUND PORT — EventPublisher
 *
 * Defines the contract for publishing domain events to the outside world.
 * This interface is implemented by the Infrastructure layer (messaging adapter).
 *
 * ─── DIP Application ──────────────────────────────────────────────────────────
 * The Application layer depends ONLY on this interface, never on the concrete
 * messaging implementation (Spring Events, Kafka, RabbitMQ, etc.). This allows
 * the domain logic to remain pure and framework-agnostic.
 *
 * ─── Domain Events vs. Integration Events ─────────────────────────────────────
 * This port publishes Domain Events — events that represent something that
 * happened within the domain. The infrastructure layer may translate these
 * to Integration Events for external systems.
 *
 * ─── Event Types Published ────────────────────────────────────────────────────
 *   - ZoneAlertEvent: Temperature crossed alert threshold
 *   - ZoneBoundaryEvent: Temperature crossed boundary threshold
 *   - ZoneEmergencyEvent: Temperature crossed emergency threshold
 *   - TruckSpoiledEvent: Truck or zone entered SPOILED status
 *   - TruckScrappedEvent: Truck was decommissioned and deleted
 *   - DriverCommandIssuedEvent: Admin issued a command to driver
 *
 * ─── Handler Responsibility ───────────────────────────────────────────────────
 * Handlers that mutate state and trigger domain events MUST:
 *   1. Save the aggregate FIRST.
 *   2. Collect events via aggregate.getDomainEvents().
 *   3. Publish events via this port.
 *   4. Clear events via aggregate.clearEvents().
 *
 * Implementations:
 *   @see IOT.coldchain.infrastructure.messaging.SpringEventPublisher
 */
public interface EventPublisher {

    /**
     * Publishes a single domain event.
     *
     * The implementation must ensure the event is delivered to all registered
     * listeners/subscribers. Delivery may be synchronous or asynchronous
     * depending on the infrastructure configuration.
     *
     * @param event the domain event to publish
     */
    void publish(DomainEvent event);

    /**
     * Publishes multiple domain events in batch.
     *
     * This is the preferred method when a single operation generates multiple
     * events (e.g., a spoil cascade affecting multiple zones).
     *
     * The implementation should ensure all events are published or none,
     * maintaining atomicity with the transaction.
     *
     * @param events the list of domain events to publish
     */
    void publishAll(List<DomainEvent> events);
}
