package IOT.coldchain.application.port1.in.command;

import IOT.coldchain.application.dto.command.RegisterMultiZoneTruckCommand;
import IOT.coldchain.application.dto.command.RegisterTruckCommand;

/**
 * INBOUND PORT (USE CASE) — RegisterTruckUseCase
 *
 * Defines the system's capability to register a new refrigerated truck
 * into the EPSS cold-chain monitoring system.
 *
 * ─── ISP Application ──────────────────────────────────────────────────────────
 * This interface is intentionally narrow: it covers ONLY truck registration.
 * It does not include sensor installation, cargo loading, or telemetry — those
 * belong to their own use case interfaces (ManageSensorUseCase, ManageCargoUseCase,
 * RecordTelemetryUseCase). No handler is forced to implement methods it does not use.
 *
 * ─── DIP Application ──────────────────────────────────────────────────────────
 * The API Controller (Presentation layer) depends on this interface, never on
 * the concrete handler class. The handler is injected at runtime by Spring's IoC.
 *
 * ─── CQRS Role ────────────────────────────────────────────────────────────────
 * WRITE side. Both methods mutate state. Both return the new truckId so the
 * API layer can respond with a 201 Created + Location header.
 *
 * Implementations (Application layer):
 *   @see IOT.coldchain.application.handler.RegisterTruckHandler
 */
public interface RegisterTruckUseCase {

    /**
     * Registers a new truck with a single initial compartment zone.
     *
     * Handler responsibilities:
     *   1. Call RefrigeratedTruckFactory.createNew(command.initialZoneName(),
     *          command.minTemp(), command.maxTemp()).
     *   2. Persist via TruckRepository.save(truck).
     *   3. Return the generated truckId.
     *
     * @param command encapsulates zone name and temperature bounds for the first zone
     * @return the generated UUID truckId of the newly registered truck
     * @throws IllegalArgumentException if temperature values violate physics or business rules
     */
    String handle(RegisterTruckCommand command);

    /**
     * Registers a new truck with multiple compartment zones defined upfront.
     *
     * Handler responsibilities:
     *   1. Map command.zoneDefinitions() to a List<RefrigeratedTruckFactory.ZoneDefinition>.
     *   2. Call RefrigeratedTruckFactory.createNewMultiZone(definitions).
     *   3. Persist via TruckRepository.save(truck).
     *   4. Return the generated truckId.
     *
     * @param command encapsulates a list of zone definitions (name + temperature range each)
     * @return the generated UUID truckId of the newly registered truck
     * @throws IllegalArgumentException if any zone definition violates validation rules
     */
    String handle(RegisterMultiZoneTruckCommand command);
}