package IOT.coldchain.application.port.in.command;

import IOT.coldchain.application.dto.command.AssignDriverCommand;
import IOT.coldchain.application.dto.command.UnassignDriverCommand;

/**
 * INBOUND PORT (USE CASE) — AssignDriverUseCase
 *
 * Defines the system's capabilities for assigning and unassigning a
 * TruckDriver to/from a refrigerated truck.
 *
 * ─── Cross-Aggregate Coordination ──────────────────────────────────────────────
 * The assign handler coordinates two separate aggregates:
 *   1. TruckDriver — loaded via DriverRepository to validate license expiry.
 *   2. RefrigeratedTruck — loaded via TruckRepository to set the driver reference.
 * The truck stores only the driverId string (not the Driver aggregate itself).
 * This is the correct DDD cross-aggregate reference pattern.
 *
 * ─── ISP Application ──────────────────────────────────────────────────────────
 * Driver assignment is a distinct operational concern from cargo, sensor, and
 * lifecycle management. It is performed by a dispatcher or admin actor before
 * a truck begins transit.
 *
 * ─── CQRS Role ────────────────────────────────────────────────────────────────
 * WRITE side. Both methods mutate truck state. Return void.
 *
 * Implementations:
 *   @see IOT.coldchain.application.handler.AssignDriverHandler
 */
public interface AssignDriverUseCase {

    /**
     * Assigns a TruckDriver to a truck.
     *
     * Handler responsibilities:
     *   1. Load driver via DriverRepository.findById(command.driverId()).
     *   2. Validate driver.getLicense().isExpired() → throw if expired (Rule A5).
     *   3. Load truck via TruckRepository.findById(command.truckId()).
     *   4. Call truck.assignDriver(command.driverId()).
     *   5. Persist via TruckRepository.save(truck).
     *
     * @param command encapsulates truckId and driverId
     * @throws IllegalArgumentException if truck or driver not found
     * @throws IllegalStateException    if truck is SPOILED/SCRAPPED, or driver license is expired
     */
    void handle(AssignDriverCommand command);

    /**
     * Removes the assigned driver from a truck.
     *
     * Handler responsibilities:
     *   1. Load truck via TruckRepository.findById(command.truckId()).
     *   2. Call truck.unassignDriver().
     *   3. Persist via TruckRepository.save(truck).
     *
     * @param command encapsulates the truckId
     * @throws IllegalArgumentException if truck not found
     * @throws IllegalStateException    if truck is IN_TRANSIT (Rule D3)
     */
    void handle(UnassignDriverCommand command);
}