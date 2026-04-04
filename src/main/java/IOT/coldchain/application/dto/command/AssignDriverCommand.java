package IOT.coldchain.application.dto.command;

/**
 * COMMAND — AssignDriverCommand
 *
 * Represents the user's intent to assign a registered TruckDriver to
 * a refrigerated truck.
 *
 * The handler must load both the TruckDriver aggregate (to validate their
 * license hasn't expired — Business Rule A5) and the RefrigeratedTruck
 * aggregate (to call truck.assignDriver(driverId)).
 *
 * Handled by: AssignDriverUseCase
 * Domain methods called:
 *   - driverRepository.findById(driverId)    → load the driver
 *   - driver.getLicense().isExpired()         → validate license
 *   - truck.assignDriver(driverId)            → set the reference
 * Business rules enforced:
 *   - Driver must exist in the system.
 *   - Driver's license must not be expired at time of assignment (Rule A5).
 *   - Truck must not be SPOILED or SCRAPPED.
 *
 * @param truckId   the truck to assign the driver to
 * @param driverId  the ID of the TruckDriver aggregate to assign
 */
public record AssignDriverCommand(
        String truckId,
        String driverId
) {}