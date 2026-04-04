package IOT.coldchain.application.handler;

import IOT.coldchain.application.dto.command.InstallSensorCommand;
import IOT.coldchain.application.port.in.command.ManageSensorUseCase;
import IOT.coldchain.application.port1.out.TruckRepository;
import IOT.coldchain.domain.entity.RefrigeratedTruck;

/**
 * COMMAND HANDLER — ManageSensorHandler
 *
 * Handles IoT sensor installation operations for truck zones.
 *
 * ─── Clean Architecture ───────────────────────────────────────────────────────
 * This handler implements the ManageSensorUseCase inbound port and depends
 * on the TruckRepository outbound port. It contains NO framework annotations.
 *
 * ─── MAC Address Format ───────────────────────────────────────────────────────
 * The domain validates MAC address format (IEEE 802). Invalid formats will
 * throw IllegalArgumentException.
 *
 * ─── Error Handling ───────────────────────────────────────────────────────────
 * All exceptions from the domain are propagated unchanged to the caller.
 */
public class ManageSensorHandler implements ManageSensorUseCase {

    private final TruckRepository truckRepository;

    public ManageSensorHandler(TruckRepository truckRepository) {
        this.truckRepository = truckRepository;
    }

    @Override
    public void handle(InstallSensorCommand command) {
        RefrigeratedTruck truck = truckRepository.findById(command.truckId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Truck not found with ID: " + command.truckId()));

        truck.installSensor(command.zoneId(), command.macAddress(), command.location());

        truckRepository.save(truck);
    }
}
