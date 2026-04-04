package IOT.coldchain.application.dto.command;

/**
 * COMMAND — StartTransitCommand
 *
 * Represents the user's intent to begin a truck's transit journey.
 *
 * This transitions the truck from REGISTERED to IN_TRANSIT status.
 * The domain enforces that the truck has at least one zone with at
 * least one installed sensor before transit may begin.
 *
 * Handled by: ManageTruckLifecycleUseCase
 * Domain method called: truck.startTransit()
 * Business rules enforced by domain:
 *   - Truck must not be SPOILED or SCRAPPED.
 *   - Truck must have at least one configured compartment zone.
 *   - At least one zone must have an installed sensor.
 *
 * @param truckId  the truck to start transit for
 */
public record StartTransitCommand(
        String truckId
) {}