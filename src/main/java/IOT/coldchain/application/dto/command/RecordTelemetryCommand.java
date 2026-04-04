package IOT.coldchain.application.dto.command;

/**
 * COMMAND — RecordTelemetryCommand
 *
 * Represents a temperature reading pushed by an IoT sensor in a truck zone.
 *
 * This is the highest-frequency command in the system — called every few seconds
 * per sensor during active transit. It triggers the domain's full state machine:
 * SAFE → ALERT → WARNING → EMERGENCY → SPOILED, and raises the corresponding
 * DomainEvent for the Application layer to dispatch.
 *
 * The handler must:
 *   1. Load the truck aggregate from the repository.
 *   2. Call truck.recordTelemetry(zoneId, macAddress, Temperature.now(currentTemp)).
 *   3. Save the aggregate (all zone/sensor state changes are persisted).
 *   4. Dispatch all collected domain events (alert, boundary, emergency, spoiled).
 *   5. Clear the domain events from the aggregate.
 *
 * Handled by: RecordTelemetryUseCase
 * Domain method called: truck.recordTelemetry(zoneId, macAddress, Temperature.now(currentTemp))
 * Business rules enforced by domain:
 *   - Truck must not be SCRAPPED.
 *   - Temperature cannot be below absolute zero (−273.15°C) — Temperature VO validates.
 *   - Zone state machine transitions are automatic based on the reading.
 *
 * @param truckId      the truck the sensor belongs to
 * @param zoneId       the zone where the sensor is installed
 * @param macAddress   the MAC address of the reporting sensor
 * @param currentTemp  the temperature reading in °C
 */
public record RecordTelemetryCommand(
        String truckId,
        String zoneId,
        String macAddress,
        double currentTemp
) {}