package IOT.coldchain.application.handler;

import IOT.coldchain.application.dto.command.RecordTelemetryCommand;
import IOT.coldchain.application.port.in.command.RecordTelemetryUseCase;
import IOT.coldchain.application.port1.out.EventPublisher;
import IOT.coldchain.application.port1.out.TruckRepository;
import IOT.coldchain.domain.entity.RefrigeratedTruck;
import IOT.coldchain.domain.event.DomainEvent;
import IOT.coldchain.domain.valueobject.Temperature;

import java.util.List;

/**
 * COMMAND HANDLER — RecordTelemetryHandler
 *
 * Handles incoming temperature readings from IoT sensors.
 *
 * ─── Highest Frequency Operation ──────────────────────────────────────────────
 * This is the highest-frequency command in the system — called every few seconds
 * per sensor during active transit. Performance is critical.
 *
 * ─── Clean Architecture ───────────────────────────────────────────────────────
 * This handler implements the RecordTelemetryUseCase inbound port and depends
 * on the TruckRepository and EventPublisher outbound ports. It contains NO
 * framework annotations.
 *
 * ─── State Machine & Event Cascade ────────────────────────────────────────────
 * This handler triggers the domain's full state machine:
 *   SAFE → ALERT → WARNING → EMERGENCY → SPOILED
 *
 * Each transition may raise a DomainEvent. The handler MUST:
 *   1. Save the aggregate FIRST.
 *   2. Collect events via truck.getDomainEvents().
 *   3. Publish events via EventPublisher.
 *   4. Clear events via truck.clearEvents().
 *
 * ─── Error Handling ───────────────────────────────────────────────────────────
 * All exceptions from the domain are propagated unchanged to the caller.
 */
public class RecordTelemetryHandler implements RecordTelemetryUseCase {

    private final TruckRepository truckRepository;
    private final EventPublisher eventPublisher;

    public RecordTelemetryHandler(TruckRepository truckRepository, EventPublisher eventPublisher) {
        this.truckRepository = truckRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void handle(RecordTelemetryCommand command) {
        RefrigeratedTruck truck = truckRepository.findById(command.truckId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Truck not found with ID: " + command.truckId()));

        Temperature reading = Temperature.now(command.currentTemp());

        truck.recordTelemetry(command.zoneId(), command.macAddress(), reading);

        truckRepository.save(truck);

        List<DomainEvent> events = truck.getDomainEvents();
        if (!events.isEmpty()) {
            eventPublisher.publishAll(events);
            truck.clearEvents();
        }
    }
}
