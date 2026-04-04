package IOT.coldchain.application.dto.command;

/**
 * COMMAND — AcknowledgeDriverCommandCommand
 *
 * Represents a driver's intent to acknowledge (accept) a pending command.
 *
 * Handled by: IssueDriverCommandUseCase
 * Domain method called: truck.acknowledgeDriverCommand(commandId)
 * Business rules enforced by domain:
 *   - The command must exist in this truck's command list.
 *   - The command must not already have been responded to.
 *
 * @param truckId    the truck that received the command
 * @param commandId  the ID of the DriverCommand to acknowledge
 */
public record AcknowledgeDriverCommandCommand(
        String truckId,
        String commandId
) {}