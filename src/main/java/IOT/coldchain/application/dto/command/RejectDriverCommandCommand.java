package IOT.coldchain.application.dto.command;

/**
 * COMMAND — RejectDriverCommandCommand
 *
 * Represents a driver's intent to reject a pending command with a stated reason.
 *
 * Command lifecycle: ISSUED → REJECTED
 *
 * Handled by: IssueDriverCommandUseCase
 * Domain method called: truck.rejectDriverCommand(commandId, reason)
 * Business rules enforced by domain:
 *   - The command must exist in this truck's command list.
 *   - The command must not already have been responded to.
 *   - The rejection reason must not be blank.
 *
 * @param truckId    the truck whose driver rejected the command
 * @param commandId  the ID of the DriverCommand to reject
 * @param reason     driver's stated reason for rejection
 */
public record RejectDriverCommandCommand(
        String truckId,
        String commandId,
        String reason
) {}
