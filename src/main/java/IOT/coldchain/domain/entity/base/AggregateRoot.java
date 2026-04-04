package IOT.coldchain.domain.entity.base;

import IOT.coldchain.domain.event.DomainEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * ABSTRACT BASE CLASS — AggregateRoot
 *
 * Base class for all aggregate roots in the domain.
 * Provides domain event collection and management.
 *
 *
 * Subclasses should override getAggregateId() to provide their identity.
 */
public abstract class AggregateRoot {
    private final transient List<DomainEvent> domainEvents = new ArrayList<>();

    protected void registerEvent(DomainEvent event) {
        if (event != null) {
            this.domainEvents.add(event);
        }
    }

    public void clearEvents() {
        this.domainEvents.clear();
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * Returns the unique identifier of this aggregate.
     * Subclasses must implement this for identity-based equality.
     */
    public abstract String getAggregateId();

    // -------------------------------------------------------------------------
    // ENTITY CONTRACT
    // -------------------------------------------------------------------------

    /**
     * ENHANCEMENT: Identity-based equality for aggregate roots.
     * Two aggregates are equal if they have the same ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateRoot that = (AggregateRoot) o;
        return Objects.equals(getAggregateId(), that.getAggregateId());
    }

    /**
     * ENHANCEMENT: Identity-based hashCode for aggregate roots.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getAggregateId());
    }
}