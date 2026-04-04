package IOT.coldchain.application.dto.command;

/**
 * COMMAND — CompleteDriverCommandCommand
 *
 * Represents a driver's intent to mark a previously acknowledged command
 * as fully completed.
 *
 * Command lifecycle: ISSUED → ACKNOWLEDGED → COMPLETED
 *
 * Handled by: IssueDriverCommandUseCase
 * Domain method called: truck.completeDriverCommand(commandId)
 * Business rules enforced by domain:
 *   - The command must exist in this truck's command list.
 *   - The command must have been acknowledged first.
 *   - The command must not already have been responded to.
 *
 * @param truckId    the truck whose driver completed the command
 * @param commandId  the ID of the DriverCommand to complete
 */
public record CompleteDriverCommandCommand(
        String truckId,
        String commandId
) {}
