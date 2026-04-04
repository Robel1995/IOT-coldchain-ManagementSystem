package IOT.coldchain.domain.event;

import IOT.coldchain.domain.enums.NotificationLevel;
import IOT.coldchain.domain.valueobject.Temperature;
import IOT.coldchain.domain.valueobject.TemperatureRange;

import java.time.Duration;
import java.time.Instant;

/**
 * DOMAIN EVENT — TemperatureEmergencyEvent (Phase 3: EMERGENCY)
 *
 * Raised when a zone's temperature has gone BEYOND its safe threshold.
 * An active breach is occurring. The emergency timer has started.
 * If the breach is not resolved within 30 consecutive minutes, the zone
 * will be declared SPOILED and a ZoneSpoiledEvent will be raised.
 *
 * The breachDuration field allows notification recipients to see how long
 * the breach has been ongoing — useful for triage decisions.
 *
 * Notification routing (Application layer handles this):
 *   - Admin:       Dashboard + SMS + Telegram (most urgent)
 *   - Dispatcher:  SMS + Telegram
 *   - Driver:      Urgent SMS
 *   - HealthCenter: Telegram — "prepare to reorder, shipment at risk"
 */
public final class TemperatureEmergencyEvent implements DomainEvent {

    private final String truckId;
    private final String zoneId;
    private final String zoneName;
    private final Temperature currentReading;
    private final TemperatureRange threshold;
    private final Instant emergencyStartTime;   // when the breach began
    private final Duration breachDuration;      // how long the breach has been ongoing
    private final NotificationLevel level = NotificationLevel.EMERGENCY;
    private final Instant occurredAt;

    public TemperatureEmergencyEvent(
            String truckId,
            String zoneId,
            String zoneName,
            Temperature currentReading,
            TemperatureRange threshold,
            Instant emergencyStartTime,
            Instant occurredAt
    ) {
        this.truckId = truckId;
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.currentReading = currentReading;
        this.threshold = threshold;
        this.emergencyStartTime = emergencyStartTime;
        this.occurredAt = occurredAt;
        this.breachDuration = (emergencyStartTime != null)
                ? Duration.between(emergencyStartTime, occurredAt)
                : Duration.ZERO;
    }

    /** Returns minutes remaining until the zone is declared SPOILED (30 min total). */
    public long minutesUntilSpoilage() {
        long elapsed = breachDuration.toMinutes();
        return Math.max(0, 30 - elapsed);
    }

    @Override public String getAggregateId() { return truckId; }
    @Override public Instant getOccurredAt() { return occurredAt; }

    public String getTruckId()                    { return truckId; }
    public String getZoneId()                     { return zoneId; }
    public String getZoneName()                   { return zoneName; }
    public Temperature getCurrentReading()        { return currentReading; }
    public TemperatureRange getThreshold()    { return threshold; }
    public Instant getEmergencyStartTime()        { return emergencyStartTime; }
    public Duration getBreachDuration()           { return breachDuration; }
    public NotificationLevel getLevel()           { return level; }

    @Override
    public String toString() {
        return "TemperatureEmergencyEvent[truck=" + truckId + ", zone=" + zoneName
                + ", reading=" + currentReading
                + ", breachDuration=" + breachDuration.toMinutes() + "min]";
    }
}