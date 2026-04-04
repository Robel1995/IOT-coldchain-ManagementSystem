package IOT.coldchain.application.handler;

import IOT.coldchain.application.dto.command.DeliverVaccineBoxCommand;
import IOT.coldchain.application.dto.command.LoadVaccineBoxCommand;
import IOT.coldchain.application.port.in.command.ManageCargoUseCase;
import IOT.coldchain.application.port1.out.TruckRepository;
import IOT.coldchain.domain.entity.RefrigeratedTruck;

/**
 * COMMAND HANDLER — ManageCargoHandler
 *
 * Handles cargo loading and delivery operations for truck zones.
 *
 * ─── Clean Architecture ───────────────────────────────────────────────────────
 * This handler implements the ManageCargoUseCase inbound port and depends
 * on the TruckRepository outbound port. It contains NO framework annotations.
 *
 * ─── Patient Safety Critical ──────────────────────────────────────────────────
 * The deliver operation is BLOCKED by the domain if the truck or zone is SPOILED.
 * The handler must never catch and suppress the domain's IllegalStateException —
 * it must propagate up to the API layer, which translates it to HTTP 422.
 *
 * ─── Error Handling ───────────────────────────────────────────────────────────
 * All exceptions from the domain are propagated unchanged to the caller.
 */
public class ManageCargoHandler implements ManageCargoUseCase {

    private final TruckRepository truckRepository;

    public ManageCargoHandler(TruckRepository truckRepository) {
        this.truckRepository = truckRepository;
    }

    @Override
    public void handle(LoadVaccineBoxCommand command) {
        RefrigeratedTruck truck = truckRepository.findById(command.truckId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Truck not found with ID: " + command.truckId()));

        truck.loadVaccineBox(command.zoneId(), command.serialNumber(), command.medicineName());

        truckRepository.save(truck);
    }

    @Override
    public void handle(DeliverVaccineBoxCommand command) {
        RefrigeratedTruck truck = truckRepository.findById(command.truckId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Truck not found with ID: " + command.truckId()));

        truck.deliverVaccineBox(command.zoneId(), command.serialNumber());

        truckRepository.save(truck);
    }
}
