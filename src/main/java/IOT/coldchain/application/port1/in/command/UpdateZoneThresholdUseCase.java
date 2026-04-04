package IOT.coldchain.application.port.in.command;

import IOT.coldchain.application.dto.command.UpdateZoneThresholdCommand;

/**
 * INBOUND PORT (USE CASE) — UpdateZoneThresholdUseCase
 *
 * Defines the system's capability to replace the temperature threshold
 * (safe operating range) for a specific compartment zone.
 *
 * ─── Value Object Immutability ─────────────────────────────────────────────────
 * TemperatureRange is immutable. The domain discards the old object and creates
 * a new one — it never mutates it in place. The handler must not attempt to
 * modify the threshold directly; it must call truck.updateZoneThreshold(...),
 * which creates a new TemperatureRange internally.
 *
 * ─── ISP Application ──────────────────────────────────────────────────────────
 * Threshold updates are performed by admins in response to changing storage
 * requirements. They are temporally distinct from registration and telemetry
 * and deserve their own interface.
 *
 * ─── CQRS Role ────────────────────────────────────────────────────────────────
 * WRITE side. Replaces a value object inside a child entity. Returns void.
 *
 * Implementations:
 *   @see IOT.coldchain.application.handler.UpdateZoneThresholdHandler
 */
public interface UpdateZoneThresholdUseCase {

    /**
     * Replaces the temperature threshold for a zone with a new valid range.
     *
     * Handler responsibilities:
     *   1. Load truck via TruckRepository.findById(command.truckId()).
     *   2. Build new TemperatureRange(command.newMinTemp(), command.newMaxTemp()).
     *   3. Call truck.updateZoneThreshold(command.zoneId(), newThreshold).
     *   4. Persist via TruckRepository.save(truck).
     *
     * @param command encapsulates truckId, zoneId, newMinTemp, and newMaxTemp
     * @throws IllegalArgumentException if temperature values violate physics or business rules
     * @throws IllegalStateException    if truck/zone is SPOILED/SCRAPPED/ALERT/WARNING/EMERGENCY
     */
    void handle(UpdateZoneThresholdCommand command);
}