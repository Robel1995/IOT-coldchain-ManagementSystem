package IOT.coldchain.application.handler;

import IOT.coldchain.application.dto.command.UpdateZoneThresholdCommand;
import IOT.coldchain.application.port.in.command.UpdateZoneThresholdUseCase;
import IOT.coldchain.application.port1.out.TruckRepository;
import IOT.coldchain.domain.entity.RefrigeratedTruck;
import IOT.coldchain.domain.valueobject.TemperatureRange;

/**
 * COMMAND HANDLER — UpdateZoneThresholdHandler
 *
 * Handles temperature threshold updates for truck zones.
 *
 * ─── Clean Architecture ───────────────────────────────────────────────────────
 * This handler implements the UpdateZoneThresholdUseCase inbound port and depends
 * on the TruckRepository outbound port. It contains NO framework annotations.
 *
 * ─── Value Object Immutability ────────────────────────────────────────────────
 * TemperatureRange is immutable. The domain discards the old object and creates
 * a new one — it never mutates it in place. The handler builds the new VO and
 * passes it to the domain.
 *
 * ─── Error Handling ───────────────────────────────────────────────────────────
 * All exceptions from the domain are propagated unchanged to the caller.
 */
public class UpdateZoneThresholdHandler implements UpdateZoneThresholdUseCase {

    private final TruckRepository truckRepository;

    public UpdateZoneThresholdHandler(TruckRepository truckRepository) {
        this.truckRepository = truckRepository;
    }

    @Override
    public void handle(UpdateZoneThresholdCommand command) {
        RefrigeratedTruck truck = truckRepository.findById(command.truckId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Truck not found with ID: " + command.truckId()));

        TemperatureRange newThreshold = new TemperatureRange(command.newMinTemp(), command.newMaxTemp());

        truck.updateZoneThreshold(command.zoneId(), newThreshold);

        truckRepository.save(truck);
    }
}
