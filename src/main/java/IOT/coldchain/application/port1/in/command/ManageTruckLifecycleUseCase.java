package IOT.coldchain.application.port.in.command;

import IOT.coldchain.application.dto.command.DeleteTruckCommand;
import IOT.coldchain.application.dto.command.StartTransitCommand;

/**
 * INBOUND PORT (USE CASE) — ManageTruckLifecycleUseCase
 *
 * Defines the system's capabilities for managing the lifecycle state
 * transitions of a refrigerated truck: starting transit and deleting a
 * decommissioned (SPOILED) truck.
 *
 * ─── ISP Application ──────────────────────────────────────────────────────────
 * Lifecycle transitions are grouped together because they both concern the
 * truck's overall state — not its zones, cargo, or sensors. They are performed
 * by an admin or dispatcher actor, distinct from IoT sensor operations.
 *
 * ─── Deletion Design ──────────────────────────────────────────────────────────
 * The delete handler MUST:
 *   1. Call truck.assertCanBeDeleted() — this transitions to SCRAPPED and raises
 *      TruckScrappedEvent (audit trail).
 *   2. Dispatch the TruckScrappedEvent BEFORE calling repository.delete().
 *   3. Call truckRepository.delete(truck) to permanently remove from DB.
 * The event must be dispatched even though the record is being deleted — it
 * is the permanent audit record of the truck's existence and reason for removal.
 *
 * ─── CQRS Role ────────────────────────────────────────────────────────────────
 * WRITE side. Both methods mutate state. Return void.
 *
 * Implementations:
 *   @see IOT.coldchain.application.handler.ManageTruckLifecycleHandler
 */
public interface ManageTruckLifecycleUseCase {

    /**
     * Starts the transit journey for a registered truck.
     *
     * Handler responsibilities:
     *   1. Load truck via TruckRepository.findById(command.truckId()).
     *   2. Call truck.startTransit().
     *   3. Persist via TruckRepository.save(truck).
     *
     * @param command encapsulates the truckId to start transit for
     * @throws IllegalArgumentException if truck is not found
     * @throws IllegalStateException    if truck is SPOILED/SCRAPPED, has no zones,
     *                                  or no zones have sensors installed
     */
    void handle(StartTransitCommand command);

    /**
     * Permanently decommissions and deletes a SPOILED truck.
     *
     * Handler responsibilities:
     *   1. Load truck via TruckRepository.findById(command.truckId()).
     *   2. Call truck.assertCanBeDeleted() — transitions to SCRAPPED,
     *      registers TruckScrappedEvent.
     *   3. Dispatch all domain events via EventPublisher.publish(events).
     *   4. Call TruckRepository.delete(truck) to remove the record.
     *
     * @param command encapsulates the truckId to decommission and delete
     * @throws IllegalArgumentException if truck is not found
     * @throws IllegalStateException    if truck is not in SPOILED status
     */
    void handle(DeleteTruckCommand command);
}