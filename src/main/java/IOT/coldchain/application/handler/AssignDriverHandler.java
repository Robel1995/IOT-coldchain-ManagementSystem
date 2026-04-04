package IOT.coldchain.application.handler;

import IOT.coldchain.application.dto.command.AssignDriverCommand;
import IOT.coldchain.application.dto.command.UnassignDriverCommand;
import IOT.coldchain.application.port.in.command.AssignDriverUseCase;
import IOT.coldchain.application.port1.out.DriverRepository;
import IOT.coldchain.application.port1.out.TruckRepository;
import IOT.coldchain.domain.entity.RefrigeratedTruck;
import IOT.coldchain.domain.entity.TruckDriver;

/**
 * COMMAND HANDLER — AssignDriverHandler
 *
 * Handles driver assignment and unassignment operations for trucks.
 *
 * ─── Clean Architecture ───────────────────────────────────────────────────────
 * This handler implements the AssignDriverUseCase inbound port and depends
 * on both TruckRepository and DriverRepository outbound ports. It contains
 * NO framework annotations.
 *
 * ─── Cross-Aggregate Coordination ─────────────────────────────────────────────
 * This handler coordinates between two aggregates:
 *   1. TruckDriver — loaded to validate license expiry
 *   2. RefrigeratedTruck — loaded to set the driver reference
 *
 * ─── Business Rule Enforcement ────────────────────────────────────────────────
 * The handler enforces Rule A5: Driver's license must not be expired at
 * time of assignment. This check is performed before calling the domain.
 *
 * ─── Error Handling ───────────────────────────────────────────────────────────
 * All exceptions from the domain are propagated unchanged to the caller.
 */
public class AssignDriverHandler implements AssignDriverUseCase {

    private final TruckRepository truckRepository;
    private final DriverRepository driverRepository;

    public AssignDriverHandler(TruckRepository truckRepository, DriverRepository driverRepository) {
        this.truckRepository = truckRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    public void handle(AssignDriverCommand command) {
        TruckDriver driver = driverRepository.findById(command.driverId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Driver not found with ID: " + command.driverId()));

        if (driver.getLicense().isExpired()) {
            throw new IllegalStateException(
                    "Cannot assign driver with expired license. Driver ID: " + command.driverId());
        }

        RefrigeratedTruck truck = truckRepository.findById(command.truckId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Truck not found with ID: " + command.truckId()));

        truck.assignDriver(command.driverId());

        truckRepository.save(truck);
    }

    @Override
    public void handle(UnassignDriverCommand command) {
        RefrigeratedTruck truck = truckRepository.findById(command.truckId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Truck not found with ID: " + command.truckId()));

        truck.unassignDriver();

        truckRepository.save(truck);
    }
}
