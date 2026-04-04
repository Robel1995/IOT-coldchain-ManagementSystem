package IOT.coldchain.application.handler;

import IOT.coldchain.application.dto.command.DeleteTruckCommand;
import IOT.coldchain.application.dto.command.StartTransitCommand;
import IOT.coldchain.application.port.in.command.ManageTruckLifecycleUseCase;
import IOT.coldchain.application.port1.out.EventPublisher;
import IOT.coldchain.application.port1.out.TruckRepository;
import IOT.coldchain.domain.entity.RefrigeratedTruck;
import IOT.coldchain.domain.event.DomainEvent;

import java.util.List;

/**
 * COMMAND HANDLER — ManageTruckLifecycleHandler
 *
 * Handles truck lifecycle state transitions: starting transit and deleting trucks.
 *
 * ─── Clean Architecture ───────────────────────────────────────────────────────
 * This handler implements the ManageTruckLifecycleUseCase inbound port and depends
 * on the TruckRepository and EventPublisher outbound ports. It contains NO
 * framework annotations.
 *
 * ─── Deletion Protocol ────────────────────────────────────────────────────────
 * The delete handler MUST:
 *   1. Call truck.assertCanBeDeleted() — transitions to SCRAPPED and raises event.
 *   2. Save the truck (to persist the SCRAPPED state).
 *   3. Dispatch the TruckScrappedEvent BEFORE deletion.
 *   4. Delete the truck from the repository.
 *
 * This preserves the audit trail even though the record is being removed.
 *
 * ─── Error Handling ───────────────────────────────────────────────────────────
 * All exceptions from the domain are propagated unchanged to the caller.
 */
public class ManageTruckLifecycleHandler implements ManageTruckLifecycleUseCase {

    private final TruckRepository truckRepository;
    private final EventPublisher eventPublisher;

    public ManageTruckLifecycleHandler(TruckRepository truckRepository, EventPublisher eventPublisher) {
        this.truckRepository = truckRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void handle(StartTransitCommand command) {
        RefrigeratedTruck truck = truckRepository.findById(command.truckId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Truck not found with ID: " + command.truckId()));

        truck.startTransit();

        truckRepository.save(truck);
    }

    @Override
    public void handle(DeleteTruckCommand command) {
        RefrigeratedTruck truck = truckRepository.findById(command.truckId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Truck not found with ID: " + command.truckId()));

        truck.assertCanBeDeleted();

        truckRepository.save(truck);

        List<DomainEvent> events = truck.getDomainEvents();
        eventPublisher.publishAll(events);

        truck.clearEvents();

        truckRepository.delete(truck);
    }
}
