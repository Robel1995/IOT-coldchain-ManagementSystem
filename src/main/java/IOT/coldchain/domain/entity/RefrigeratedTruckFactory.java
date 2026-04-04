package IOT.coldchain.domain.entity;

import IOT.coldchain.domain.valueobject.TemperatureRange;
import IOT.coldchain.domain.valueobject.ZoneName;

import java.util.Objects;
import java.util.UUID;

/**
 * FACTORY — RefrigeratedTruckFactory
 *
 * Responsible for creating new RefrigeratedTruck aggregates.
 *
 * Why a factory instead of calling the constructor directly?
 *  1. UUID generation is an infrastructure concern (creating new IDs).
 *     The factory encapsulates this so the domain doesn't import java.util.UUID
 *     in every service that needs a truck.
 *  2. A new truck must always start with at least one configured zone.
 *     The factory enforces this creation rule once, not in every caller.
 *  3. Complex construction logic (validation + child entity creation) is kept
 *     in one place — the factory — not scattered across use-case handlers.
 *
 * CRITICAL DESIGN DECISION — Factory vs reconstitute():
 *  ┌─────────────────────────────────────────────────────────────────┐
 *  │  createNew()     → called by Application layer (Command Handler)│
 *  │                    Creates a BRAND-NEW aggregate                │
 *  │                    Triggers creation-time business rules        │
 *  │                    Calls the PRIVATE constructor                │
 *  │                                                                 │
 *  │  reconstitute()  → called by Infrastructure layer (JPA adapter) │
 *  │                    Rebuilds an EXISTING aggregate from DB       │
 *  │                    Skips creation-time rules (already validated)│
 *  │                    A static method ON the aggregate class       │
 *  └─────────────────────────────────────────────────────────────────┘
 *
 *  These two paths are COMPLETELY SEPARATE. Using reconstitute() for new
 *  creation (as the original code did) is architecturally wrong because:
 *  - It bypasses the constructor's semantic meaning.
 *  - It passes null children to a method meant to load persisted children.
 *  - It makes the code lie: "reconstitute" means "rebuild", not "create".
 *
 * LSP note: If a subclass of RefrigeratedTruck is created (e.g., ElectricTruck),
 * a corresponding factory method should be added here — not a separate factory
 * class — so creation rules stay centralized.
 */
public final class RefrigeratedTruckFactory {

    // Static-only factory: no instances needed
    private RefrigeratedTruckFactory() {
        throw new UnsupportedOperationException("Factory class — do not instantiate.");
    }

    // =========================================================================
    // CREATION METHODS
    // =========================================================================

    /**
     * Creates a new RefrigeratedTruck with a single initial compartment zone.
     *
     * This is the primary creation method. Most trucks start with one zone
     * and add more zones via truck.addZone() after registration.
     *
     * @param zoneName  the name for the first zone (e.g., "ZONE_A")
     * @param minTemp   minimum safe temperature for the first zone (°C)
     * @param maxTemp   maximum safe temperature for the first zone (°C)
     * @return a new RefrigeratedTruck in REGISTERED status with one zone
     *
     * @throws IllegalArgumentException if temperature values violate physics or business rules
     */
    public static RefrigeratedTruck createNew(
            String zoneName,
            double minTemp,
            double maxTemp
    ) {
        // Generate unique IDs — UUID generation is the factory's responsibility
        String truckId = UUID.randomUUID().toString();
        String zoneId  = UUID.randomUUID().toString();

        // Build Value Objects — they self-validate on construction
        ZoneName zoneNameVO       = new ZoneName(zoneName);
        TemperatureRange threshold = new TemperatureRange(minTemp, maxTemp);

        // Call the PRIVATE constructor via package-visible access
        // (Factory is in domain.factory, Truck in domain.entity — see note below)
        RefrigeratedTruck truck = RefrigeratedTruck.createViaFactory(truckId);
        truck.addZone(zoneId, zoneNameVO, threshold);
        return truck;
    }

    /**
     * Creates a new RefrigeratedTruck with multiple zones configured upfront.
     *
     * Used when the full zone layout is known at registration time.
     * Each zone is defined by a ZoneDefinition record.
     *
     * @param zoneDefinitions list of zone specs — must not be empty
     * @return a configured RefrigeratedTruck with all zones added
     *
     * @throws IllegalArgumentException if zoneDefinitions is null or empty
     */
    public static RefrigeratedTruck createNewMultiZone(
            java.util.List<ZoneDefinition> zoneDefinitions
    ) {
        Objects.requireNonNull(zoneDefinitions, "zoneDefinitions cannot be null.");
        if (zoneDefinitions.isEmpty()) {
            throw new IllegalArgumentException(
                    "A refrigerated truck must have at least one compartment zone."
            );
        }

        String truckId = UUID.randomUUID().toString();
        RefrigeratedTruck truck = RefrigeratedTruck.createViaFactory(truckId);

        for (ZoneDefinition def : zoneDefinitions) {
            String zoneId = UUID.randomUUID().toString();
            ZoneName zoneNameVO       = new ZoneName(def.zoneName());
            TemperatureRange threshold = new TemperatureRange(def.minTemp(), def.maxTemp());
            truck.addZone(zoneId, zoneNameVO, threshold);
        }

        return truck;
    }

    // =========================================================================
    // ZONE DEFINITION — inner record used as a parameter object
    // =========================================================================

    /**
     * Parameter object for multi-zone creation.
     * A record is ideal here: immutable, auto-generates equals/hashCode/toString,
     * and clearly names each field.
     *
     * Java 16+ record syntax. If your project uses Java < 16, replace with a
     * simple final class with a constructor and getters.
     */
    public record ZoneDefinition(String zoneName, double minTemp, double maxTemp) {
        // Compact constructor (records support this) — validates on construction
        public ZoneDefinition {
            if (zoneName == null || zoneName.isBlank())
                throw new IllegalArgumentException("ZoneDefinition: zoneName cannot be blank.");
            // minTemp/maxTemp validation is deferred to TemperatureThreshold
        }
    }
}