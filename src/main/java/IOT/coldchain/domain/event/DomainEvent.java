package IOT.coldchain.domain.event;

import java.time.Instant;

/**
 * INTERFACE — DomainEvent
 *
 * Marker interface for all domain events raised by aggregates.
 *
 * Domain events represent something that HAPPENED in the domain — past tense.
 * They are raised INSIDE the aggregate and collected by the Application layer
 * after the aggregate is saved. The Application layer then dispatches them
 * to notification handlers, other aggregates, or external services.
 *
 * DIP compliance:
 *   The Domain layer defines this interface. The Application and Infrastructure
 *   layers depend on it. Neither Spring's ApplicationEvent nor any framework
 *   class appears here — zero framework dependency.
 *
 * ISP compliance:
 *   Each event type carries only the data relevant to that specific event.
 *   No fat base class that forces unused fields on every event.
 */
public interface DomainEvent {

    /**
     * @return the ID of the aggregate that raised this event
     */
    String getAggregateId();

    /**
     * @return the exact instant this event occurred
     */
    Instant getOccurredAt();
}