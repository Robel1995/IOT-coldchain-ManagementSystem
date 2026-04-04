package IOT.coldchain.domain.event;

import java.time.Instant;

/**
 * DOMAIN EVENT — TruckScrappedEvent
 *
 * Raised when a SPOILED truck is permanently decommissioned and deleted.
 * Used for audit trail purposes — the system records that this truck existed
 * and why it was scrapped, even after its database record is removed.
 *
 * The Application layer routes this to the audit log and admin dashboard.
 */
public final class TruckScrappedEvent implements DomainEvent {

    private final String truckId;
    private final Instant occurredAt;

    public TruckScrappedEvent(String truckId, Instant occurredAt) {
        this.truckId = truckId;
        this.occurredAt = occurredAt;
    }

    @Override public String getAggregateId() { return truckId; }
    @Override public Instant getOccurredAt() { return occurredAt; }

    public String getTruckId() { return truckId; }

    @Override
    public String toString() {
        return "TruckScrappedEvent[truck=" + truckId + ", at=" + occurredAt + "]";
    }
}