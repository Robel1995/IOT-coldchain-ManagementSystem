package IOT.coldchain.application.port1.in.command;

import IOT.coldchain.application.dto.command.AddZoneToTruckCommand;

/**
 * INBOUND PORT (USE CASE) — ManageZoneUseCase
 *
 * Defines the system's capability to add new compartment zones to an
 * existing refrigerated truck after initial registration.
 *
 * ─── ISP Application ──────────────────────────────────────────────────────────
 * Zone management is separated from truck registration because the actor and
 * timing differ: registration is a one-time creation act, while zone addition
 * is an ongoing operational modification. Splitting them allows each controller
 * action to depend only on the interface it needs.
 *
 * ─── CQRS Role ────────────────────────────────────────────────────────────────
 * WRITE side. Mutates aggregate state.
 * Returns void — the caller already knows the truckId; no new ID is generated.
 *
 * Implementations:
 *   @see IOT.coldchain.application.handler.ManageZoneHandler
 */
public interface ManageZoneUseCase {

    /**
     * Adds a new compartment zone to an existing truck.
     *
     * Handler responsibilities:
     *   1. Load truck via TruckRepository.findById(command.truckId()).
     *   2. Generate a new zoneId UUID.
     *   3. Build ZoneName and TemperatureRange value objects from command fields.
     *   4. Call truck.addZone(zoneId, zoneName, threshold).
     *   5. Persist via TruckRepository.save(truck).
     *
     * @param command encapsulates truckId, zone name, and temperature bounds
     * @throws IllegalArgumentException if the truck is not found, or temperature values are invalid
     * @throws IllegalStateException    if the truck is SPOILED/SCRAPPED, or zone name is duplicate
     */
    void handle(AddZoneToTruckCommand command);
}