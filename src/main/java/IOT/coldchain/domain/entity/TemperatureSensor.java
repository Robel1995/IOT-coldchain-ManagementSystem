package IOT.coldchain.domain.entity;

import IOT.coldchain.domain.valueobject.Temperature;
import IOT.coldchain.domain.valueobject.MacAddress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * ENTITY — TemperatureSensor (Child Entity of CompartmentZone)
 *
 * Represents a physical IoT temperature sensor installed inside a compartment zone.
 * Identity: macAddress — the hardware MAC address is globally unique per device.
 *
 * Why List<Temperature> and NOT just a raw double:
 *  The teacher pointed out the original design was wrong. A sensor must maintain
 *  a history of readings because:
 *    1. We need to track whether a breach is CONSECUTIVE for 30 minutes.
 *    2. Audit trails and incident reports require historical data.
 *    3. Each reading has a timestamp — a raw double loses that.
 *  Temperature (the Value Object) holds both the value and the recordedAt timestamp.
 *
 * equals/hashCode: identity-based on macAddress.
 *  Required so that CompartmentZone's Set<TemperatureSensor> can detect duplicate
 *  sensor registrations (same physical device installed twice by mistake).
 *
 * Fields are final where they should not change (macAddress, location).
 */
public class TemperatureSensor {

    // Maximum readings retained in memory per sensor (rolling window).
    // Older readings are dropped to prevent unbounded memory growth.
    private static final int MAX_READING_HISTORY = 500;

    private final String macAddress;  // IEEE 802 MAC, e.g. "AA:BB:CC:DD:EE:FF"
    private final String location;    // physical location in the zone, e.g. "TOP", "DOOR", "FLOOR"

    // Reading history: newest reading is at the end of the list.
    // Using ArrayList — we never need to search by MAC here, just append and iterate.
    private final List<Temperature> readingHistory;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR — package-private, only CompartmentZone can build this
    // -------------------------------------------------------------------------

    TemperatureSensor(String macAddress, String location) {

        MacAddress validatedMac = new MacAddress(macAddress);
        if (location == null || location.isBlank())
            throw new IllegalArgumentException("Sensor location cannot be null or blank.");

        this.macAddress = macAddress.trim().toUpperCase();
        this.location = location.trim();
        this.readingHistory = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // DOMAIN BEHAVIOUR
    // -------------------------------------------------------------------------

    /**
     * Records a new temperature reading from this sensor.
     * Appends to the rolling history (capped at MAX_READING_HISTORY).
     * Package-private — only CompartmentZone calls this.
     *
     * @param reading the new Temperature value object (with timestamp)
     */
    void recordReading(Temperature reading) {
        Objects.requireNonNull(reading, "Temperature reading cannot be null.");
        if (readingHistory.size() >= MAX_READING_HISTORY) {
            readingHistory.remove(0); // drop the oldest reading
        }
        readingHistory.add(reading);
    }

    /**
     * Returns the most recent temperature reading, or null if no readings exist.
     */
    public Temperature getLatestReading() {
        if (readingHistory.isEmpty()) return null;
        return readingHistory.get(readingHistory.size() - 1);
    }

    /**
     * Returns the full reading history as an unmodifiable list.
     * Ordered oldest → newest.
     */
    public List<Temperature> getReadingHistory() {
        return Collections.unmodifiableList(readingHistory);
    }

    // -------------------------------------------------------------------------
    // RECONSTITUTION — for the Infrastructure / JPA adapter ONLY
    // -------------------------------------------------------------------------

    public static TemperatureSensor reconstitute(
            String macAddress, String location, List<Temperature> readingHistory
    ) {
        TemperatureSensor sensor = new TemperatureSensor(macAddress, location);
        if (readingHistory != null) {
            sensor.readingHistory.addAll(readingHistory);
        }
        return sensor;
    }

    // -------------------------------------------------------------------------
    // ENTITY CONTRACT: equals and hashCode by identity (macAddress)
    // This is REQUIRED for Set<TemperatureSensor> in CompartmentZone to detect duplicates.
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TemperatureSensor)) return false;
        TemperatureSensor that = (TemperatureSensor) o;
        return Objects.equals(macAddress, that.macAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(macAddress);
    }

    @Override
    public String toString() {
        return "TemperatureSensor[mac=" + macAddress
                + ", location=" + location
                + ", readings=" + readingHistory.size() + "]";
    }
    // -------------------------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------------------------

    public String getMacAddress() { return macAddress; }
    public String getLocation()   { return location; }
}