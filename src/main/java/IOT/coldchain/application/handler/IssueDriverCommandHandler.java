package IOT.coldchain.application.handler;

import IOT.coldchain.application.dto.command.AcknowledgeDriverCommandCommand;
import IOT.coldchain.application.dto.command.CompleteDriverCommandCommand;
import IOT.coldchain.application.dto.command.IssueDriverCommandCommand;
import IOT.coldchain.application.dto.command.RejectDriverCommandCommand;
import IOT.coldchain.application.port.in.command.IssueDriverCommandUseCase;
import IOT.coldchain.application.port1.out.EventPublisher;
import IOT.coldchain.application.port1.out.TruckRepository;
import IOT.coldchain.domain.entity.RefrigeratedTruck;
import IOT.coldchain.domain.event.DomainEvent;

import java.util.List;
import java.util.UUID;

/**
 * COMMAND HANDLER — IssueDriverCommandHandler
 *
 * Handles the full lifecycle of admin-to-driver commands:
 * issuing, acknowledging, completing, and rejecting.
 *
 * ─── Command Lifecycle ────────────────────────────────────────────────────────
 * ISSUED → ACKNOWLEDGED → COMPLETED   (normal path)
 * ISSUED → REJECTED                   (driver declines)
 *
 * ─── Clean Architecture ───────────────────────────────────────────────────────
 * This handler implements the IssueDriverCommandUseCase inbound port and depends
 * on the TruckRepository and EventPublisher outbound ports. It contains NO
 * framework annotations.
 *
 * ─── Event Dispatch for Issue ─────────────────────────────────────────────────
 * The issue() operation raises a DriverCommandIssuedEvent. The handler MUST:
 *   1. Save the truck FIRST.
 *   2. Collect events via truck.getDomainEvents().
 *   3. Publish events via EventPublisher.
 *   4. Clear events via truck.clearEvents().
 *
 * ─── Error Handling ───────────────────────────────────────────────────────────
 * All exceptions from the domain are propagated unchanged to the caller.
 */
public class IssueDriverCommandHandler implements IssueDriverCommandUseCase {

    private final TruckRepository truckRepository;
    private final EventPublisher eventPublisher;

    public IssueDriverCommandHandler(TruckRepository truckRepository, EventPublisher eventPublisher) {
        this.truckRepository = truckRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void handle(IssueDriverCommandCommand command) {
        RefrigeratedTruck truck = truckRepository.findById(command.truckId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Truck not found with ID: " + command.truckId()));

        String commandId = UUID.randomUUID().toString();

        truck.issueDriverCommand(commandId, command.adminId(), command.commandType(), command.customMessage());

        truckRepository.save(truck);

        List<DomainEvent> events = truck.getDomainEvents();
        eventPublisher.publishAll(events);

        truck.clearEvents();
    }

    @Override
    public void handle(AcknowledgeDriverCommandCommand command) {
        RefrigeratedTruck truck = truckRepository.findById(command.truckId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Truck not found with ID: " + command.truckId()));

        truck.acknowledgeDriverCommand(command.commandId());

        truckRepository.save(truck);
    }

    @Override
    public void handle(CompleteDriverCommandCommand command) {
        RefrigeratedTruck truck = truckRepository.findById(command.truckId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Truck not found with ID: " + command.truckId()));

        truck.completeDriverCommand(command.commandId());

        truckRepository.save(truck);
    }

    @Override
    public void handle(RejectDriverCommandCommand command) {
        RefrigeratedTruck truck = truckRepository.findById(command.truckId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Truck not found with ID: " + command.truckId()));

        truck.rejectDriverCommand(command.commandId(), command.reason());

        truckRepository.save(truck);
    }
}
