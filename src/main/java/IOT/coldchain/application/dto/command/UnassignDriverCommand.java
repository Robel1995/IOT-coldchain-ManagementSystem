package IOT.coldchain.application.dto.command;

/**
 * COMMAND — UnassignDriverCommand
 *
 * Represents the user's intent to remove a driver from a truck.
 *
 * Handled by: AssignDriverUseCase
 * Domain method called: truck.unassignDriver()
 * Business rules enforced by domain:
 *   - Cannot unassign a driver while truck is IN_TRANSIT (Rule D3).
 *
 * @param truckId  the truck to unassign the driver from
 */
public record UnassignDriverCommand(
        String truckId
) {}