package IOT.coldchain.application.port.in.command;

import IOT.coldchain.application.dto.command.DeliverVaccineBoxCommand;
import IOT.coldchain.application.dto.command.LoadVaccineBoxCommand;

/**
 * INBOUND PORT (USE CASE) — ManageCargoUseCase
 *
 * Defines the system's capabilities for loading vaccine boxes into a truck zone
 * before dispatch and delivering (removing) them at a health center.
 *
 * ─── ISP Application ──────────────────────────────────────────────────────────
 * Load and deliver are grouped in a single interface because they share the same
 * actor (the VaccineDispatcher or health center staff) and the same aggregate
 * (RefrigeratedTruck). They are separated from sensor and telemetry operations.
 *
 * ─── Patient Safety Note ──────────────────────────────────────────────────────
 * The deliver operation is BLOCKED by the domain if the truck or zone is SPOILED.
 * The handler must never catch and suppress the domain's IllegalStateException —
 * it must propagate up to the API layer, which translates it to HTTP 422.
 *
 * ─── CQRS Role ────────────────────────────────────────────────────────────────
 * WRITE side. Both methods mutate state. Return void.
 *
 * Implementations:
 *   @see IOT.coldchain.application.handler.ManageCargoHandler
 */
public interface ManageCargoUseCase {

    /**
     * Loads a vaccine box into a specific zone of a truck.
     *
     * Handler responsibilities:
     *   1. Load truck via TruckRepository.findById(command.truckId()).
     *   2. Call truck.loadVaccineBox(command.zoneId(), command.serialNumber(),
     *              command.medicineName()).
     *   3. Persist via TruckRepository.save(truck).
     *
     * @param command encapsulates truckId, zoneId, serialNumber, and medicineName
     * @throws IllegalArgumentException if truck/zone not found, or inputs are blank
     * @throws IllegalStateException    if truck or zone is SPOILED/SCRAPPED
     */
    void handle(LoadVaccineBoxCommand command);

    /**
     * Delivers (removes) a vaccine box from a truck zone.
     *
     * PATIENT SAFETY CRITICAL — the domain enforces that SPOILED trucks and zones
     * cannot deliver. This guard must never be bypassed.
     *
     * Handler responsibilities:
     *   1. Load truck via TruckRepository.findById(command.truckId()).
     *   2. Call truck.deliverVaccineBox(command.zoneId(), command.serialNumber()).
     *   3. Persist via TruckRepository.save(truck).
     *
     * @param command encapsulates truckId, zoneId, and serialNumber of the box to deliver
     * @throws IllegalArgumentException if truck/zone/box not found
     * @throws IllegalStateException    if truck or zone is SPOILED, or box is ruined
     */
    void handle(DeliverVaccineBoxCommand command);
}