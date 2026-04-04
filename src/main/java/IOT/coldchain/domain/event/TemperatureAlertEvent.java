package IOT.coldchain.domain.event;

import IOT.coldchain.domain.enums.NotificationLevel;
import IOT.coldchain.domain.valueobject.Temperature;
import IOT.coldchain.domain.valueobject.TemperatureRange;

import java.time.Instant;

/**
 * DOMAIN EVENT — TemperatureAlertEvent (Phase 1: ALERT)
 *
 * Raised when a zone's temperature is approaching its safe limit —
 * within 2°C of either the minimum or maximum threshold.
 *
 * This is a WARNING SIGN, not a breach. The situation is still recoverable
 * without cargo loss. Notifies internal stakeholders only (Admin, Dispatcher, Driver).
 *
 * Notification routing (Application layer handles this):
 *   - Admin:      Dashboard + Email
 *   - Dispatcher: Email
 *   - Driver:     Dashboard
 *   - HealthCenter: NOT notified
 */
public final class TemperatureAlertEvent implements DomainEvent {

    private final String truckId;
    private final String zoneId;
    private final String zoneName;
    private final Temperature currentReading;
    private final TemperatureRange threshold;
    private final NotificationLevel level = NotificationLevel.ALERT;
    private final Instant occurredAt;

    public TemperatureAlertEvent(
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
    }

    @Override public String getAggregateId() { return truckId; }
    @Override public Instant getOccurredAt() { return occurredAt; }

    public String getTruckId()                    { return truckId; }
    public String getZoneId()                     { return zoneId; }
    public String getZoneName()                   { return zoneName; }
    public Temperature getCurrentReading()        { return currentReading; }
    public TemperatureRange getThreshold()    { return threshold; }
    public NotificationLevel getLevel()           { return level; }

    @Override
    public String toString() {
        return "TemperatureAlertEvent[truck=" + truckId + ", zone=" + zoneName
                + ", reading=" + currentReading + ", threshold=" + threshold + "]";
    }
}