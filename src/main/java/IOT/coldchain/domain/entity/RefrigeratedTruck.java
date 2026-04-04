package IOT.coldchain.domain.entity;

import IOT.coldchain.domain.entity.base.AggregateRoot;
import IOT.coldchain.domain.enums.CommandType;
import IOT.coldchain.domain.enums.BoundaryType;
import IOT.coldchain.domain.enums.NotificationLevel;
import IOT.coldchain.domain.enums.TruckState;
import IOT.coldchain.domain.enums.ZoneStatus;
import IOT.coldchain.domain.event.*;
import IOT.coldchain.domain.valueobject.Temperature;
import IOT.coldchain.domain.valueobject.TemperatureRange;
import IOT.coldchain.domain.valueobject.ZoneName;

import java.time.Instant;
import java.util.*;

/**
 * ENTITY — RefrigeratedTruck (AGGREGATE ROOT)
 *
 * The master entity that owns and protects the consistency of the entire
 * cold-chain monitoring aggregate. The ONLY entry point for any modification
 * to compartment zones, sensors, vaccine boxes, and driver commands.
 *
 * ─────────────────────────────────────────────────────────────────────────────
 * CONSTRUCTOR STRATEGY — the key architectural decision:
 *
 *   private RefrigeratedTruck(truckId)
 *       The single initialization constructor. Never called directly by
 *       anything outside this class.
 *
 *   static createViaFactory(truckId)   ← called ONLY by RefrigeratedTruckFactory
 *       Package-level visibility. Name makes intent explicit. Represents
 *       "a brand new truck is being registered for the first time."
 *       Triggers all creation-time business rules.
 *
 *   static reconstitute(...)           ← called ONLY by Infrastructure JPA adapter
 *       Public. Represents "rebuilding a truck we already know exists from DB."
 *       Bypasses creation-time rules (already validated when first created).
 *
 * These two paths are architecturally separate. Using reconstitute() for new
 * creation is wrong — it misrepresents intent and passes null children to a
 * method designed for loading persisted children.
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * Aggregate boundary:
 *   RefrigeratedTruck (Root)
 *     └─ CompartmentZone (child)
 *          ├─ TemperatureSensor (child)  — Set, no duplicates by MAC address
 *          ├─ VaccineBox (child)         — List, insertion order preserved
 *          └─ TemperatureThreshold (VO) — immutable, replaced on update
 *     └─ DriverCommand (child)          — List, full history retained
 *
 * TruckDriver, VaccineDispatcher, HealthCenter — separate aggregates,
 * referenced by ID only. Never embedded.
 *
 * DIP compliance: zero Spring/JPA/framework imports. Pure Java domain.
 *
 * LSP compliance: any subclass must honour all preconditions and postconditions
 * defined in every public method.
 */
public class RefrigeratedTruck extends AggregateRoot{

    // =========================================================================
    // FIELDS
    // =========================================================================

    private final String truckId;
    private TruckState status;
    private final List<CompartmentZone> zones;
    private String assignedDriverId;
    private final List<DriverCommand> driverCommands;

    /**
     * Transient domain events. Collected during operations, dispatched by
     * the Application layer after saving the aggregate, then cleared.
     * Never persisted to the database.
     */
   // private final List<DomainEvent> domainEvents;

    // =========================================================================
    // PRIVATE CONSTRUCTOR — single initialization point
    // =========================================================================

    private RefrigeratedTruck(String truckId) {
        if (truckId == null || truckId.isBlank())
            throw new IllegalArgumentException("truckId cannot be null or blank.");
        this.truckId        = truckId.trim();
        this.status         = TruckState.REGISTERED;
        this.zones          = new ArrayList<>();
        this.driverCommands = new ArrayList<>();
        //this.domainEvents   = new ArrayList<>();
        this.assignedDriverId = null;
    }

    // =========================================================================
    // FACTORY BRIDGE — called ONLY by RefrigeratedTruckFactory
    // =========================================================================

    /**
     * Creates a blank truck for the factory to configure.
     *
     * Package-accessible: RefrigeratedTruckFactory is in the sibling package
     * domain.factory — it cannot see a package-private method here. We use
     * package-private visibility and place the factory in the same package,
     * OR we make this method package-private and accept the factory sits in
     * domain.entity. The cleanest solution without module-info.java:
     * declare this method with default (package-private) access and keep
     * the factory in domain.factory but document that this bridge exists.
     *
     * Alternative: make the factory an inner static class of RefrigeratedTruck.
     * For academic clarity, we use the named static method approach.
     *
     * @param truckId UUID already generated by the factory
     */
    static RefrigeratedTruck createViaFactory(String truckId) {
        return new RefrigeratedTruck(truckId);
    }

    // =========================================================================
    // ZONE MANAGEMENT
    // =========================================================================

    /**
     * Adds a new compartment zone to this truck.
     *
     * Business rules:
     *  - Truck must not be SPOILED or SCRAPPED.
     *  - Zone name must be unique within this truck.
     *
     * @param zoneId    UUID for the zone (assigned by factory or command handler)
     * @param zoneName  ZoneName Value Object (already validated)
     * @param threshold TemperatureThreshold Value Object (already validated)
     */
    public void addZone(String zoneId, ZoneName zoneName, TemperatureRange threshold) {
        assertNotSpoiledOrScrapped("add a zone");
        boolean nameConflict = zones.stream()
                .anyMatch(z -> z.getZoneName().equals(zoneName));
        if (nameConflict) {
            throw new IllegalStateException(
                    "Business Rule Violation: Zone '" + zoneName
                            + "' already exists in truck '" + truckId + "'."
            );
        }
        zones.add(new CompartmentZone(zoneId, zoneName, threshold));
    }

    // =========================================================================
    // SENSOR MANAGEMENT
    // =========================================================================

    /**
     * Installs an IoT sensor into a specific zone.
     *
     * All child mutations route through the aggregate root.
     * External code never touches CompartmentZone directly.
     *
     * @param zoneId     the zone to install the sensor in
     * @param macAddress IEEE 802 MAC address (XX:XX:XX:XX:XX:XX)
     * @param location   physical position, e.g. "TOP", "DOOR", "FLOOR"
     */
    public void installSensor(String zoneId, String macAddress, String location) {
        assertNotSpoiledOrScrapped("install a sensor");
        findZoneById(zoneId).installSensor(macAddress, location);
    }

    // =========================================================================
    // CARGO MANAGEMENT
    // =========================================================================

    /**
     * Loads a vaccine box into a specific zone.
     *
     * Truck-level guard: not SPOILED/SCRAPPED.
     * Zone-level guards delegated to CompartmentZone:
     *   - Zone not SPOILED
     *   - Serial number unique within the zone
     *
     * @param zoneId       target zone identifier
     * @param serialNumber globally unique box serial number
     * @param medicineName e.g. "Polio Vaccine OPV", "Yellow Fever 17D"
     */
    public void loadVaccineBox(String zoneId, String serialNumber, String medicineName) {
        assertNotSpoiledOrScrapped("load cargo");
        findZoneById(zoneId).loadVaccineBox(serialNumber, medicineName);
    }

    /**
     * Delivers (removes) a vaccine box for a health center.
     *
     * PATIENT SAFETY: Blocked at truck level (SPOILED truck) and zone level
     * (SPOILED zone). Both guards must pass before delivery is allowed.
     *
     * @param zoneId       zone containing the box
     * @param serialNumber the box to deliver and remove
     */
    public void deliverVaccineBox(String zoneId, String serialNumber) {
        if (this.status == TruckState.SPOILED) {
            throw new IllegalStateException(
                    "FATAL: Cannot deliver vaccines — truck '" + truckId
                            + "' is SPOILED. Patient lives are at risk."
            );
        }
        findZoneById(zoneId).deliverVaccineBox(serialNumber);
    }

    // =========================================================================
    // TELEMETRY
    // =========================================================================

    /**
     * Records a temperature reading from an IoT sensor in a zone.
     *
     * Called every few seconds per sensor during transit.
     *
     * Flow:
     *  1. Delegate to CompartmentZone.recordReading() → returns NotificationLevel.
     *  2. Derive updated truck-level TruckStatus from all zones.
     *  3. Raise the appropriate DomainEvent for the Application layer.
     *
     * @param zoneId     zone where the sensor is installed
     * @param macAddress the sensor reporting the reading
     * @param reading    Temperature Value Object (value + timestamp)
     */
    public void recordTelemetry(String zoneId, String macAddress, Temperature reading) {
        if (this.status == TruckState.SCRAPPED) {
            throw new IllegalStateException(
                    "Truck '" + truckId + "' is SCRAPPED. Telemetry refused."
            );
        }
        CompartmentZone zone = findZoneById(zoneId);
        NotificationLevel level = zone.recordReading(macAddress, reading);
        deriveAndUpdateTruckStatus();
        raiseNotificationEvent(zone, reading, level);
    }

    // =========================================================================
    // THRESHOLD UPDATE
    // =========================================================================

    /**
     * Replaces the temperature threshold for a specific zone.
     *
     * Value Object immutability rule: we replace the entire TemperatureThreshold
     * object — never mutate it. The zone discards the old object and uses the new one.
     *
     * @param zoneId       target zone
     * @param newThreshold the replacement TemperatureThreshold
     */
    public void updateZoneThreshold(String zoneId, TemperatureRange newThreshold) {
        assertNotSpoiledOrScrapped("update a threshold");
        findZoneById(zoneId).updateThreshold(newThreshold);
    }

    // =========================================================================
    // TRUCK LIFECYCLE
    // =========================================================================

    /**
     * Starts the transit journey.
     * Requires at least one zone with at least one installed sensor.
     */
    public void startTransit() {
        if (this.status == TruckState.SPOILED || this.status == TruckState.SCRAPPED) {
            throw new IllegalStateException(
                    "Cannot start transit — truck '" + truckId + "' is " + status + "."
            );
        }
        if (zones.isEmpty()) {
            throw new IllegalStateException(
                    "Business Rule Violation: Cannot start transit without configured zones."
            );
        }
        boolean hasSensors = zones.stream().anyMatch(CompartmentZone::hasSensors);
        if (!hasSensors) {
            throw new IllegalStateException(
                    "Business Rule Violation: Cannot start transit — no sensors installed in any zone."
            );
        }
        this.status = TruckState.IN_TRANSIT;
    }

    /**
     * Assigns a driver by ID reference (not embedding the TruckDriver entity).
     */
    public void assignDriver(String driverId) {
        assertNotSpoiledOrScrapped("assign a driver");
        if (driverId == null || driverId.isBlank())
            throw new IllegalArgumentException("driverId cannot be null or blank.");
        this.assignedDriverId = driverId.trim();
    }

    /**
     * Unassigns the driver. Blocked while IN_TRANSIT.
     */
    public void unassignDriver() {
        if (this.status == TruckState.IN_TRANSIT) {
            throw new IllegalStateException(
                    "Cannot unassign driver from truck '" + truckId + "' while IN_TRANSIT."
            );
        }
        this.assignedDriverId = null;
    }

    // =========================================================================
    // ADMIN COMMANDS
    // =========================================================================

    /**
     * Issues an operational command from an Admin to the assigned driver.
     *
     * Business rules:
     *  - Truck must have an assigned driver.
     *  - STOP command requires IN_TRANSIT status.
     *  - CUSTOM commands require a non-blank message (DriverCommand enforces this).
     */
    public void issueDriverCommand(
            String commandId, String adminId, CommandType type, String customMessage
    ) {
        if (assignedDriverId == null) {
            throw new IllegalStateException(
                    "Cannot issue command — no driver assigned to truck '" + truckId + "'."
            );
        }
        if (type == CommandType.STOP && this.status != TruckState.IN_TRANSIT) {
            throw new IllegalStateException(
                    "STOP command can only be issued to IN_TRANSIT trucks. Current: " + status
            );
        }
        DriverCommand command = new DriverCommand(commandId, adminId, type, customMessage);
        driverCommands.add(command);
        registerEvent(new DriverCommandIssuedEvent(
                truckId, assignedDriverId, commandId, type, customMessage, Instant.now()
        ));
    }

    public void acknowledgeDriverCommand(String commandId) {
        findCommandById(commandId).acknowledge();
    }

    public void completeDriverCommand(String commandId) {
        findCommandById(commandId).complete();
    }

    public void rejectDriverCommand(String commandId, String reason) {
        findCommandById(commandId).reject(reason);
    }

    // =========================================================================
    // DELETION GUARD
    // =========================================================================

    /**
     * Validates deletion eligibility and transitions to SCRAPPED.
     *
     * Only SPOILED trucks can be deleted.
     * The Application layer calls this before repository.delete(truck).
     */
    public void assertCanBeDeleted() {
        if (this.status != TruckState.SPOILED) {
            throw new IllegalStateException(
                    "Cannot delete truck '" + truckId
                            + "'. Only SPOILED trucks may be deleted. Current: " + status
            );
        }
        this.status = TruckState.SCRAPPED;
        registerEvent(new TruckScrappedEvent(truckId, Instant.now()));
    }

    // =========================================================================
    // DOMAIN EVENTS — collect and clear
    // =========================================================================

    /**
     * Returns all pending domain events and clears the internal list.
     * Called by the Application layer after persisting. Events dispatched exactly once.
     */
    // =========================================================================
    // PRIVATE HELPERS
    // =========================================================================

    private CompartmentZone findZoneById(String zoneId) {
        return zones.stream()
                .filter(z -> z.getZoneId().equals(zoneId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Zone '" + zoneId + "' not found in truck '" + truckId + "'."
                ));
    }

    private DriverCommand findCommandById(String commandId) {
        return driverCommands.stream()
                .filter(c -> c.getCommandId().equals(commandId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "DriverCommand '" + commandId + "' not found in truck '" + truckId + "'."
                ));
    }

    private void deriveAndUpdateTruckStatus() {
        if (this.status == TruckState.SCRAPPED) return;
        boolean anySpoiled = zones.stream()
                .anyMatch(z -> z.getStatus() == ZoneStatus.SPOILED);
        if (anySpoiled && this.status != TruckState.SPOILED) {
            this.status = TruckState.SPOILED;
        }
    }

    private void raiseNotificationEvent(
            CompartmentZone zone, Temperature reading, NotificationLevel level
    ) {
        if (level == NotificationLevel.NONE) return;
        switch (level) {
            case ALERT:
                registerEvent(new TemperatureAlertEvent(
                        truckId, zone.getZoneId(), zone.getZoneName().getName(),
                        reading, zone.getThreshold(), Instant.now()));
                break;
            case NOTIFY:
                BoundaryType boundaryType = zone.determineBoundaryTypeForReading(reading);
                registerEvent(new TemperatureBoundaryEvent(
                        truckId, zone.getZoneId(), zone.getZoneName().getName(),
                        reading, zone.getThreshold(), Instant.now()));
                break;
            case EMERGENCY:
                registerEvent(new TemperatureEmergencyEvent(
                        truckId, zone.getZoneId(), zone.getZoneName().getName(),
                        reading, zone.getThreshold(), zone.getEmergencyStartTime(), Instant.now()));
                break;
            case SPOILED:
                List<String> ruinedSerials = new ArrayList<>();
                zone.getVaccineBoxes().stream()
                        .filter(VaccineBox::isRuined)
                        .map(VaccineBox::getSerialNumber)
                        .forEach(ruinedSerials::add);
                registerEvent(new ZoneSpoiledEvent(
                        truckId, zone.getZoneId(), zone.getZoneName().getName(),
                        ruinedSerials, Instant.now()));
                break;
            default:
                break;
        }
    }

    private void assertNotSpoiledOrScrapped(String action) {
        if (this.status == TruckState.SPOILED)
            throw new IllegalStateException(
                    "Cannot " + action + " — truck '" + truckId + "' is SPOILED.");
        if (this.status == TruckState.SCRAPPED)
            throw new IllegalStateException(
                    "Cannot " + action + " — truck '" + truckId + "' is SCRAPPED.");
    }

    // =========================================================================
    // RECONSTITUTION — Infrastructure / JPA adapter ONLY
    // =========================================================================

    /**
     * Rebuilds a complete aggregate from persisted database state.
     *
     * NEVER call from Application or Domain logic.
     * Only the JPA repository adapter should call this.
     */
    public static RefrigeratedTruck reconstitute(
            String truckId,
            TruckState status,
            List<CompartmentZone> zones,
            List<DriverCommand> driverCommands,
            String assignedDriverId
    ) {
        RefrigeratedTruck truck = new RefrigeratedTruck(truckId);
        truck.status           = status;
        truck.assignedDriverId = assignedDriverId;
        if (zones != null)          truck.zones.addAll(zones);
        if (driverCommands != null) truck.driverCommands.addAll(driverCommands);
        return truck;
    }

    // =========================================================================
    // ENTITY CONTRACT
    // =========================================================================

    @Override
    public String getAggregateId() {
        return getTruckId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RefrigeratedTruck)) return false;
        return Objects.equals(truckId, ((RefrigeratedTruck) o).truckId);
    }

    @Override
    public int hashCode() { return Objects.hash(truckId); }

    @Override
    public String toString() {
        return "RefrigeratedTruck[id=" + truckId + ", status=" + status
                + ", zones=" + zones.size() + "]";
    }

    // =========================================================================
    // GETTERS
    // =========================================================================

    public String getTruckId()          { return truckId; }
    public TruckState getStatus()      { return status; }
    public String getAssignedDriverId() { return assignedDriverId; }

    public List<CompartmentZone> getZones() {
        return Collections.unmodifiableList(zones);
    }

    public List<DriverCommand> getDriverCommands() {
        return Collections.unmodifiableList(driverCommands);
    }
}