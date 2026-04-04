package IOT.coldchain.application.port.in.command;

import IOT.coldchain.application.dto.command.InstallSensorCommand;

/**
 * INBOUND PORT (USE CASE) — ManageSensorUseCase
 *
 * Defines the system's capability to install an IoT temperature sensor
 * into a specific compartment zone of a refrigerated truck.
 *
 * ─── ISP Application ──────────────────────────────────────────────────────────
 * Sensor management is isolated from cargo and telemetry operations because
 * it is performed by a technician at a different time and with a different
 * intent than loading cargo or recording sensor data. Each interface covers
 * exactly one actor's responsibilities — no irrelevant methods forced on callers.
 *
 * ─── CQRS Role ────────────────────────────────────────────────────────────────
 * WRITE side. Mutates aggregate state (adds sensor to zone's LinkedHashSet).
 * Returns void — no new ID is generated; the MAC address is the sensor's identity.
 *
 * Implementations:
 *   @see IOT.coldchain.application.handler.ManageSensorHandler
 */
public interface ManageSensorUseCase {

    /**
     * Installs a physical IoT temperature sensor into a truck zone.
     *
     * Handler responsibilities:
     *   1. Load truck via TruckRepository.findById(command.truckId()).
     *   2. Call truck.installSensor(command.zoneId(), command.macAddress(), command.location()).
     *   3. Persist via TruckRepository.save(truck).
     *
     * @param command encapsulates truckId, zoneId, macAddress, and physical location
     * @throws IllegalArgumentException if truck/zone not found, or MAC address format is invalid
     * @throws IllegalStateException    if truck is SPOILED/SCRAPPED, or sensor MAC is duplicate in zone
     */
    void handle(InstallSensorCommand command);
}