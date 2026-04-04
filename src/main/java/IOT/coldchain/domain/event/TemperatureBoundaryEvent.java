package IOT.coldchain.domain.event;

import IOT.coldchain.domain.enums.NotificationLevel;
import IOT.coldchain.domain.valueobject.Temperature;
import IOT.coldchain.domain.valueobject.TemperatureRange;
import IOT.coldchain.domain.enums.BoundaryType;

import java.time.Instant;

/**
 * DOMAIN EVENT — TemperatureBoundaryEvent (Phase 2: NOTIFY)
 *
 * Raised when a zone's temperature reaches EXACTLY the minimum or maximum boundary.
 * The temperature has not yet gone beyond the safe range, but the threshold has
 * been touched — one more degree and it becomes a breach.
 *
 * Notification routing (Application layer handles this):
 *   - Admin:       Dashboard + SMS
 *   - Dispatcher:  SMS
 *   - Driver:      SMS + Dashboard
 *   - HealthCenter: Email — "your shipment is under monitoring"
 */
public final class TemperatureBoundaryEvent implements DomainEvent {

    /** Indicates which boundary was reached */
    public enum BoundaryType { UPPER, LOWER }

    private final String truckId;
    private final String zoneId;
    private final String zoneName;
    private final Temperature currentReading;
    private final TemperatureRange threshold;
    private final BoundaryType boundaryType;
    private final NotificationLevel level = NotificationLevel.NOTIFY;
    private final Instant occurredAt;

    public TemperatureBoundaryEvent(
            String truckId,
            String zoneId,
            String zoneName,
            Temperature currentReading,
            TemperatureRange threshold,
            Instant occurredAt
    ) {
        this.truckId = truckId;
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.currentReading = currentReading;
        this.threshold = threshold;
        this.occurredAt = occurredAt;
        // Determine which boundary was touched
        this.boundaryType = currentReading.isEqualTo(threshold.getMaxTemp())
                ? BoundaryType.UPPER
                : BoundaryType.LOWER;
    }

    @Override public String getAggregateId() { return truckId; }
    @Override public Instant getOccurredAt() { return occurredAt; }

    public String getTruckId()                    { return truckId; }
    public String getZoneId()                     { return zoneId; }
    public String getZoneName()                   { return zoneName; }
    public Temperature getCurrentReading()        { return currentReading; }
    public TemperatureRange getThreshold()    { return threshold; }
    public BoundaryType getBoundaryType()         { return boundaryType; }
    public NotificationLevel getLevel()           { return level; }

    @Override
    public String toString() {
        return "TemperatureBoundaryEvent[truck=" + truckId + ", zone=" + zoneName
                + ", boundary=" + boundaryType + ", reading=" + currentReading + "]";
    }
}