package IOT.coldchain.domain.event;

import IOT.coldchain.domain.enums.NotificationLevel;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * DOMAIN EVENT — ZoneSpoiledEvent (SPOILED — Terminal)
 *
 * Raised when a zone has been declared SPOILED after 30 consecutive minutes
 * of a temperature breach. The vaccines in this zone are confirmed ruined.
 *
 * This event triggers the most urgent notifications — all parties on all channels.
 * HealthCenter receives a formal notification to initiate reorder immediately.
 *
 * Notification routing (Application layer handles this):
 *   - Admin:       Dashboard + SMS + Telegram + Email
 *   - Dispatcher:  SMS + Telegram + Email
 *   - Driver:      SMS + Dashboard
 *   - HealthCenter: Email + Telegram — "CONFIRMED SPOILED: initiate reorder"
 *
 * The affectedVaccineSerials list enables precise reorder tracking.
 */
public final class ZoneSpoiledEvent implements DomainEvent {

    private final String truckId;
    private final String zoneId;
    private final String zoneName;
    private final List<String> affectedVaccineSerials;   // serial numbers of all ruined boxes
    private final NotificationLevel level = NotificationLevel.SPOILED;
    private final Instant occurredAt;

    public ZoneSpoiledEvent(
            String truckId,
            String zoneId,
            String zoneName,
            List<String> affectedVaccineSerials,
            Instant occurredAt
    ) {
        this.truckId = truckId;
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.affectedVaccineSerials = Collections.unmodifiableList(affectedVaccineSerials);
        this.occurredAt = occurredAt;
    }

    @Override public String getAggregateId() { return truckId; }
    @Override public Instant getOccurredAt() { return occurredAt; }

    public String getTruckId()                        { return truckId; }
    public String getZoneId()                         { return zoneId; }
    public String getZoneName()                       { return zoneName; }
    public List<String> getAffectedVaccineSerials()   { return affectedVaccineSerials; }
    public int getRuinedBoxCount()                    { return affectedVaccineSerials.size(); }
    public NotificationLevel getLevel()               { return level; }

    @Override
    public String toString() {
        return "ZoneSpoiledEvent[truck=" + truckId + ", zone=" + zoneName
                + ", ruinedBoxes=" + affectedVaccineSerials.size() + "]";
    }
}