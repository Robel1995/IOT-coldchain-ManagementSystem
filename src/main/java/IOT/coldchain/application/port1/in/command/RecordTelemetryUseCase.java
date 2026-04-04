package IOT.coldchain.application.port.in.command;

import IOT.coldchain.application.dto.command.RecordTelemetryCommand;

/**
 * INBOUND PORT (USE CASE) — RecordTelemetryUseCase
 *
 * Defines the system's capability to process an incoming temperature reading
 * from an IoT sensor in a truck zone.
 *
 * ─── ISP Application ──────────────────────────────────────────────────────────
 * Telemetry is separated from all other operations because it is the highest-
 * frequency operation (called every few seconds per sensor), is triggered by
 * an automated IoT device rather than a human user, and involves the most
 * complex domain logic (state machine + domain event cascade). Isolating it
 * allows the controller endpoint and handler to remain laser-focused.
 *
 * ─── Event Dispatch Responsibility ────────────────────────────────────────────
 * This is the ONLY use case that triggers domain events. The handler MUST:
 *   1. Save the aggregate FIRST.
 *   2. Collect events via truck.getDomainEvents().
 *   3. Dispatch each event to the EventPublisher outbound port.
 *   4. Call truck.clearEvents() to prevent re-dispatching on retry.
 *
 * ─── CQRS Role ────────────────────────────────────────────────────────────────
 * WRITE side. Mutates zone status, sensor reading history, and potentially
 * triggers the SPOILED cascade. Returns void.
 *
 * Implementations:
 *   @see IOT.coldchain.application.handler.RecordTelemetryHandler
 */
public interface RecordTelemetryUseCase {

    /**
     * Records a temperature reading from an IoT sensor and drives the zone
     * state machine forward (SAFE → ALERT → WARNING → EMERGENCY → SPOILED).
     *
     * Handler responsibilities:
     *   1. Load truck via TruckRepository.findById(command.truckId()).
     *   2. Build Temperature.now(command.currentTemp()) value object.
     *   3. Call truck.recordTelemetry(command.zoneId(), command.macAddress(), reading).
     *   4. Persist via TruckRepository.save(truck).
     *   5. Dispatch all pending domain events via EventPublisher.publish(events).
     *   6. Call truck.clearEvents().
     *
     * @param command encapsulates truckId, zoneId, macAddress, and temperature value
     * @throws IllegalArgumentException if temperature is below absolute zero (−273.15°C),
     *                                  or truck/zone not found
     * @throws IllegalStateException    if truck is SCRAPPED
     */
    void handle(RecordTelemetryCommand command);
}