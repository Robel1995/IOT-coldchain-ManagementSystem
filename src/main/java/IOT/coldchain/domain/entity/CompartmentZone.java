package IOT.coldchain.domain.entity;

import IOT.coldchain.domain.valueobject.MacAddress;
import IOT.coldchain.domain.enums.BoundaryType;
import IOT.coldchain.domain.enums.NotificationLevel;
import IOT.coldchain.domain.enums.ZoneStatus;
import IOT.coldchain.domain.valueobject.Temperature;
import IOT.coldchain.domain.valueobject.TemperatureRange;
import IOT.coldchain.domain.valueobject.ZoneName;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * ENTITY — CompartmentZone (Child Entity of RefrigeratedTruck)
 *
 * Represents one temperature-controlled compartment inside the truck.
 * A multi-zone truck can carry vaccines that require different temperature ranges
 * simultaneously — e.g., Zone A at 2–8°C for polio, Zone B at -20 to -15°C for
 * yellow fever. Each zone has its own threshold, its own sensors, and its own cargo.
 *
 * Identity: zoneId — unique within the truck (UUID assigned by factory).
 *
 * CRITICAL DESIGN DECISIONS:
 *
 * 1. sensors is Set<TemperatureSensor> (NOT List):
 *    A physical IoT sensor has a unique MAC address. Installing the same device twice
 *    in one zone is a data error. Set enforces uniqueness.
 *    TemperatureSensor.equals() is based on macAddress — so the Set correctly deduplicates.
 *
 * 2. vaccineBoxes is List<VaccineBox>:
 *    Multiple boxes of the same medicine type can exist (different serial numbers).
 *    Order matters for audit purposes. List is correct here.
 *
 * 3. Emergency timer tracks CONSECUTIVE breach minutes:
 *    emergencyStartTime is reset to null whenever temperature returns to safe.
 *    This prevents false spoilage from brief glitches.
 *
 * 4. All mutable operations are package-private or protected:
 *    External code MUST go through RefrigeratedTruck (aggregate root).
 *
 * LSP compliance:
 *    If subclasses of CompartmentZone (e.g., UltraColdZone, AmbientZone) are created,
 *    they must honour the same contracts: recordReading() must return a NotificationLevel,
 *    never throw where the parent doesn't, and never weaken the preconditions.
 */
public class CompartmentZone {

    /**
     * Duration of continuous temperature breach before vaccines are declared spoiled.
     * Professional rule: the timer RESETS if temperature returns to safe range.
     */
    private static final Duration SPOILAGE_BREACH_DURATION = Duration.ofMinutes(30);

    private final String zoneId;
    private final ZoneName zoneName;

    private TemperatureRange threshold;   // mutable — can be updated by aggregate
    private ZoneStatus status;

    // SET prevents duplicate sensor installation (teacher's correction applied)
    // LinkedHashSet preserves insertion order for display consistency
    private final Set<TemperatureSensor> sensors;

    // LIST — multiple boxes of same medicine allowed (different serial numbers)
    private final List<VaccineBox> vaccineBoxes;

    // Emergency breach tracking — null means no active breach
    private Instant emergencyStartTime;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR — package-private: only RefrigeratedTruck (aggregate) builds zones
    // -------------------------------------------------------------------------

    CompartmentZone(String zoneId, ZoneName zoneName, TemperatureRange threshold) {
        Objects.requireNonNull(zoneId, "zoneId cannot be null.");
        Objects.requireNonNull(zoneName, "zoneName cannot be null.");
        Objects.requireNonNull(threshold, "threshold cannot be null.");

        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.threshold = threshold;
        this.status = ZoneStatus.SAFE;
        this.sensors = new LinkedHashSet<>();
        this.vaccineBoxes = new ArrayList<>();
        this.emergencyStartTime = null;
    }

    // =========================================================================
    // DOMAIN BEHAVIOUR — called by RefrigeratedTruck (aggregate root)
    // =========================================================================

    // -------------------------------------------------------------------------
    // Sensor management
    // -------------------------------------------------------------------------

    /**
     * Installs a new IoT temperature sensor into this zone.
     *
     * Business rule: duplicate MAC addresses are rejected.
     * The Set's add() returns false if an equal element already exists.
     *
     * @param macAddress IEEE 802 MAC address of the sensor
     * @param location   physical position in the zone, e.g. "TOP", "DOOR"
     */
    void installSensor(String macAddress, String location) {
        MacAddress validatedMac = new MacAddress(macAddress);
        TemperatureSensor newSensor = new TemperatureSensor(macAddress, location);
        boolean added = sensors.add(newSensor);
        if (!added) {
            throw new IllegalStateException(
                    "Business Rule Violation: Sensor with MAC address '" + macAddress
                            + "' is already installed in zone '" + zoneName + "'."
            );
        }
    }

    // -------------------------------------------------------------------------
    // Cargo management
    // -------------------------------------------------------------------------

    /**
     * Loads a box of vaccines into this zone.
     *
     * Business rules:
     *  - Zone must not be SPOILED.
     *  - Serial number must be unique within this zone.
     *
     * @param serialNumber unique box identifier
     * @param medicineName e.g. "Polio Vaccine OPV", "Yellow Fever 17D"
     */
    void loadVaccineBox(String serialNumber, String medicineName) {
        if (this.status == ZoneStatus.SPOILED) {
            throw new IllegalStateException(
                    "Business Rule Violation: Cannot load cargo into SPOILED zone '"
                            + zoneName + "'."
            );
        }
        // Enforce serial number uniqueness within this zone
        boolean duplicate = vaccineBoxes.stream()
                .anyMatch(box -> box.getSerialNumber().equals(serialNumber.trim()));
        if (duplicate) {
            throw new IllegalStateException(
                    "Business Rule Violation: VaccineBox with serial number '"
                            + serialNumber + "' already exists in zone '" + zoneName + "'."
            );
        }
        vaccineBoxes.add(new VaccineBox(serialNumber, medicineName));
    }

    /**
     * Removes a vaccine box from this zone for delivery to a health center.
     *
     * Business rules:
     *  - Zone must not be SPOILED (patient safety — never deliver ruined vaccines).
     *  - The box must exist in this zone.
     *
     * @param serialNumber the serial number of the box to deliver
     */
    void deliverVaccineBox(String serialNumber) {
        if (this.status == ZoneStatus.SPOILED) {
            throw new IllegalStateException(
                    "FATAL BUSINESS RULE: Cannot deliver vaccines from SPOILED zone '"
                            + zoneName + "'. Patient lives are at risk."
            );
        }
        boolean removed = vaccineBoxes.removeIf(
                box -> box.getSerialNumber().equals(serialNumber.trim())
        );
        if (!removed) {
            throw new IllegalArgumentException(
                    "VaccineBox with serial number '" + serialNumber
                            + "' was not found in zone '" + zoneName + "'."
            );
        }
    }

    // -------------------------------------------------------------------------
    // Temperature monitoring — the core business logic
    // -------------------------------------------------------------------------

    /**
     * Records a new temperature reading from a sensor in this zone and evaluates
     * the current threat level.
     *
     * This method implements the full 3-phase notification cascade:
     *   Phase 1 (ALERT):     approaching threshold (within 2°C)
     *   Phase 2 (NOTIFY):    exactly at boundary
     *   Phase 3 (EMERGENCY): beyond boundary → timer starts
     *   → SPOILED:           after 30 consecutive minutes of emergency
     *
     * The sensor reading is also stored in the sensor's history.
     *
     * @param macAddress the MAC address of the sensor sending the reading
     * @param reading    the Temperature value object (value + timestamp)
     * @return NotificationLevel indicating the urgency of the situation
     */
    /**
     * Records a new temperature reading and evaluates the threat level using a State Machine.
     * Events are "Edge-Triggered" (only fire when the state changes) to prevent IoT notification spam.
     */
    NotificationLevel recordReading(String macAddress, Temperature reading) {
        Objects.requireNonNull(reading, "Temperature reading cannot be null.");
        Objects.requireNonNull(macAddress, "MAC address cannot be null.");
        //Validate MAC format first
        MacAddress validatedMac = new MacAddress(macAddress);
        String normalizedMac = validatedMac.getValue();

        // 1. Save to sensor history
        sensors.stream()
                .filter(s -> s.getMacAddress().equals(macAddress.trim().toUpperCase()))
                .findFirst()
                .ifPresent(sensor -> sensor.recordReading(reading));

        if (this.status == ZoneStatus.SPOILED) {
            return NotificationLevel.NONE; // Already ruined, no need to spam more events
        }

        // 2. Capture the current state BEFORE we evaluate the new reading
        ZoneStatus previousStatus = this.status;
        NotificationLevel levelToTrigger = NotificationLevel.NONE;

        // ---- EVALUATE NEW THREAT LEVEL ----

        if (threshold.isViolatedBy(reading)) {
            // PHASE 3 — EMERGENCY
            if (this.emergencyStartTime == null) {
                this.emergencyStartTime = reading.getRecordedAt();
            }

            Duration breachDuration = Duration.between(emergencyStartTime, reading.getRecordedAt());

            if (!breachDuration.isNegative() && breachDuration.compareTo(SPOILAGE_BREACH_DURATION) >= 0) {
                this.status = ZoneStatus.SPOILED;
                vaccineBoxes.forEach(VaccineBox::markAsRuined);
                return NotificationLevel.SPOILED; // SPOILED always triggers
            }

            this.status = ZoneStatus.EMERGENCY;
            // State Transition logic: Only alert if we just entered EMERGENCY
            if (previousStatus != ZoneStatus.EMERGENCY) {
                levelToTrigger = NotificationLevel.EMERGENCY;
            }

        } else {
            // Temp is safe — reset emergency timer
            this.emergencyStartTime = null;

            if (threshold.isAtBoundary(reading)) {
                // PHASE 2 — WARNING (At boundery)
                this.status = ZoneStatus.WARNING;
                if (previousStatus != ZoneStatus.WARNING && previousStatus != ZoneStatus.EMERGENCY) {
                    levelToTrigger = NotificationLevel.NOTIFY;
                }
            } else if (threshold.isApproachingLimit(reading)) {
                // PHASE 1 — ALERT (approaching limit)
                this.status = ZoneStatus.ALERT;
                if (previousStatus == ZoneStatus.SAFE) {
                    levelToTrigger = NotificationLevel.ALERT;
                }
            } else {
                // ALL GOOD
                // Trigger recovery event when returning to safe
                if (previousStatus != ZoneStatus.SAFE) {
                    // We recovered from a danger state
                    // Return a special recovery indicator
                    levelToTrigger = NotificationLevel.NONE; // No urgent notification needed
                }
                this.status = ZoneStatus.SAFE;
            }
        }

        return levelToTrigger;
    }

    /**
     * Determines which boundary type would be triggered by a reading at boundary.
     * Used by the aggregate root to create proper boundary events.
     */
    BoundaryType determineBoundaryTypeForReading(Temperature reading) {
        double value = reading.getValue();
        if (Double.compare(value, threshold.getMaxTemp()) == 0) {
            return BoundaryType.UPPER;
        } else if (Double.compare(value, threshold.getMinTemp()) == 0) {
            return BoundaryType.LOWER;
        }
        throw new IllegalArgumentException("Reading is not at boundary");
    }
    // -------------------------------------------------------------------------
    // Threshold update
    // -------------------------------------------------------------------------

    /**
     * Replaces the current temperature threshold with a new one.
     *
     * Value Objects are immutable — we replace the whole object.
     * Blocked if zone is SPOILED (no point updating rules for ruined cargo).
     *
     * Business rule: if the new threshold equals the old one, reject as a no-op.
     *
     * @param newThreshold the replacement TemperatureThreshold value object
     */
    void updateThreshold(TemperatureRange newThreshold) {
        Objects.requireNonNull(newThreshold, "New threshold cannot be null.");
        if (this.status == ZoneStatus.SPOILED) {
            throw new IllegalStateException(
                    "Business Rule Violation: Cannot update threshold for SPOILED zone '"
                            + zoneName + "'."
            );
        }
        if (newThreshold.equals(this.threshold)) {
            throw new IllegalArgumentException(
                    "Business Rule Violation: New threshold " + newThreshold
                            + " is identical to the current threshold. No update needed."
            );
        }
        this.threshold = newThreshold;
    }

    // =========================================================================
    // RECONSTITUTION — for Infrastructure / JPA adapter ONLY
    // =========================================================================

    /**
     * Rebuilds a CompartmentZone from persisted database state.
     * Called ONLY by the JPA repository adapter — NEVER by domain or application logic.
     */
    public static CompartmentZone reconstitute(
            String zoneId,
            ZoneName zoneName,
            TemperatureRange threshold,
            ZoneStatus status,
            List<VaccineBox> vaccineBoxes,
            List<TemperatureSensor> sensors,
            Instant emergencyStartTime
    ) {
        CompartmentZone zone = new CompartmentZone(zoneId, zoneName, threshold);
        zone.status = status;
        zone.emergencyStartTime = emergencyStartTime;
        if (vaccineBoxes != null) zone.vaccineBoxes.addAll(vaccineBoxes);
        if (sensors != null) zone.sensors.addAll(sensors);
        return zone;
    }

    // =========================================================================
    // ENTITY CONTRACT
    // =========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompartmentZone)) return false;
        return Objects.equals(zoneId, ((CompartmentZone) o).zoneId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zoneId);
    }

    @Override
    public String toString() {
        return "CompartmentZone[id=" + zoneId
                + ", name=" + zoneName
                + ", status=" + status
                + ", threshold=" + threshold + "]";
    }

    // =========================================================================
    // GETTERS — no setters (mutation only via domain methods)
    // =========================================================================

    public String getZoneId()                         { return zoneId; }
    public ZoneName getZoneName()                     { return zoneName; }
    public TemperatureRange getThreshold()        { return threshold; }
    public ZoneStatus getStatus()                     { return status; }
    public Instant getEmergencyStartTime()            { return emergencyStartTime; }

    /** Returns sensors as an unmodifiable Set to protect aggregate internals. */
    public Set<TemperatureSensor> getSensors() {
        return Collections.unmodifiableSet(sensors);
    }

    /** Returns vaccine boxes as an unmodifiable List. */
    public List<VaccineBox> getVaccineBoxes() {
        return Collections.unmodifiableList(vaccineBoxes);
    }

    /** Convenience: does this zone have any sensors installed? */
    public boolean hasSensors() {
        return !sensors.isEmpty();
    }

    /** Convenience: is this zone currently in any danger state? */
    public boolean isInDanger() {
        return status == ZoneStatus.ALERT
                || status == ZoneStatus.WARNING
                || status == ZoneStatus.EMERGENCY
                || status == ZoneStatus.SPOILED;
    }
}