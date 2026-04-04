package IOT.coldchain.application.port.in.command;

import IOT.coldchain.application.dto.command.AcknowledgeDriverCommandCommand;
import IOT.coldchain.application.dto.command.CompleteDriverCommandCommand;
import IOT.coldchain.application.dto.command.IssueDriverCommandCommand;
import IOT.coldchain.application.dto.command.RejectDriverCommandCommand;

/**
 * INBOUND PORT (USE CASE) — IssueDriverCommandUseCase
 *
 * Defines the system's capabilities for the full lifecycle of admin-to-driver
 * commands: issuing, acknowledging, completing, and rejecting.
 *
 * ─── Command Lifecycle ─────────────────────────────────────────────────────────
 * ISSUED → ACKNOWLEDGED → COMPLETED   (normal path)
 * ISSUED → REJECTED                   (driver declines the order)
 *
 * ─── Domain Event ──────────────────────────────────────────────────────────────
 * The issue() operation raises a DriverCommandIssuedEvent automatically inside
 * the aggregate. The handler for issueCommand must:
 *   1. Save the truck.
 *   2. Dispatch the event via EventPublisher.
 *   3. Clear events from the aggregate.
 *
 * ─── ISP Application ──────────────────────────────────────────────────────────
 * All four operations concern the same DriverCommand child entity inside the
 * RefrigeratedTruck aggregate. They are grouped here because they form a coherent
 * unit of responsibility — the command lifecycle — performed by admin and driver actors.
 *
 * ─── CQRS Role ────────────────────────────────────────────────────────────────
 * WRITE side. All four methods mutate DriverCommand state. Return void.
 *
 * Implementations:
 *   @see IOT.coldchain.application.handler.IssueDriverCommandHandler
 */
public interface IssueDriverCommandUseCase {

    /**
     * Issues an operational command from an admin to the truck's assigned driver.
     *
     * Handler responsibilities:
     *   1. Generate a new commandId UUID.
     *   2. Load truck via TruckRepository.findById(command.truckId()).
     *   3. Call truck.issueDriverCommand(commandId, command.adminId(),
     *              command.commandType(), command.customMessage()).
     *   4. Persist via TruckRepository.save(truck).
     *   5. Dispatch DriverCommandIssuedEvent via EventPublisher.
     *   6. Clear truck events.
     *
     * @param command encapsulates truckId, adminId, commandType, and optional customMessage
     * @throws IllegalArgumentException if truck not found, or CUSTOM command has blank message
     * @throws IllegalStateException    if truck has no assigned driver, or STOP on non-IN_TRANSIT truck
     */
    void handle(IssueDriverCommandCommand command);

    /**
     * Records that the driver has accepted and will execute a pending command.
     *
     * Handler responsibilities:
     *   1. Load truck via TruckRepository.findById(command.truckId()).
     *   2. Call truck.acknowledgeDriverCommand(command.commandId()).
     *   3. Persist via TruckRepository.save(truck).
     *
     * @param command encapsulates truckId and commandId
     * @throws IllegalArgumentException if truck or command not found
     * @throws IllegalStateException    if command has already been responded to
     */
    void handle(AcknowledgeDriverCommandCommand command);

    /**
     * Records that the driver has fully executed a previously acknowledged command.
     *
     * Handler responsibilities:
     *   1. Load truck via TruckRepository.findById(command.truckId()).
     *   2. Call truck.completeDriverCommand(command.commandId()).
     *   3. Persist via TruckRepository.save(truck).
     *
     * @param command encapsulates truckId and commandId
     * @throws IllegalArgumentException if truck or command not found
     * @throws IllegalStateException    if command was not first acknowledged
     */
    void handle(CompleteDriverCommandCommand command);

    /**
     * Records that the driver has rejected a pending command with a stated reason.
     *
     * Handler responsibilities:
     *   1. Load truck via TruckRepository.findById(command.truckId()).
     *   2. Call truck.rejectDriverCommand(command.commandId(), command.reason()).
     *   3. Persist via TruckRepository.save(truck).
     *
     * @param command encapsulates truckId, commandId, and rejection reason
     * @throws IllegalArgumentException if truck or command not found, or reason is blank
     * @throws IllegalStateException    if command has already been responded to
     */
    void handle(RejectDriverCommandCommand command);
}