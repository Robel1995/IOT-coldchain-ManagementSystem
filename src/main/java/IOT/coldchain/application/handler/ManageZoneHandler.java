package IOT.coldchain.application.handler;

import IOT.coldchain.application.dto.command.AddZoneToTruckCommand;
import IOT.coldchain.application.port1.in.command.ManageZoneUseCase;
import IOT.coldchain.application.port1.out.TruckRepository;
import IOT.coldchain.domain.entity.RefrigeratedTruck;
import IOT.coldchain.domain.valueobject.TemperatureRange;
import IOT.coldchain.domain.valueobject.ZoneName;

import java.util.UUID;

/**
 * COMMAND HANDLER — ManageZoneHandler
 *
 * Handles compartment zone addition operations for existing trucks.
 *
 * ─── Clean Architecture ───────────────────────────────────────────────────────
 * This handler implements the ManageZoneUseCase inbound port and depends
 * on the TruckRepository outbound port. It contains NO framework annotations.
 *
 * ─── Zone ID Generation ───────────────────────────────────────────────────────
 * The handler generates a new UUID for the zone ID before calling the domain.
 * The domain factory uses this ID to create the new zone.
 *
 * ─── Error Handling ───────────────────────────────────────────────────────────
 * All exceptions from the domain are propagated unchanged to the caller.
 */
public class ManageZoneHandler implements ManageZoneUseCase {

    private final TruckRepository truckRepository;

    public ManageZoneHandler(TruckRepository truckRepository) {
        this.truckRepository = truckRepository;
    }

    @Override
    public void handle(AddZoneToTruckCommand command) {
        RefrigeratedTruck truck = truckRepository.findById(command.truckId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Truck not found with ID: " + command.truckId()));

        String zoneId = UUID.randomUUID().toString();
        ZoneName zoneName = new ZoneName(command.zoneName());
        TemperatureRange threshold = new TemperatureRange(command.minTemp(), command.maxTemp());

        truck.addZone(zoneId, zoneName, threshold);

        truckRepository.save(truck);
    }
}
