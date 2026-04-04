package IOT.coldchain.application.dto.command;

/**
 * COMMAND — DeleteTruckCommand
 *
 * Represents the user's intent to permanently decommission and delete
 * a refrigerated truck from the system.
 *
 * The domain enforces that only SPOILED trucks may be deleted (Rule D1).
 * The aggregate transitions to SCRAPPED status and raises a TruckScrappedEvent
 * before the repository deletes the record. This preserves the audit trail.
 *
 * Handled by: DeleteTruckUseCase
 * Domain method called:
 *   - truck.assertCanBeDeleted()   → transitions to SCRAPPED, raises TruckScrappedEvent
 *   - truckRepository.delete(truck)
 * Business rules enforced by domain:
 *   - Truck must be in SPOILED status. SAFE trucks cannot be deleted (Rule D1).
 *   - After assertCanBeDeleted(), the TruckScrappedEvent must be dispatched
 *     by the Application layer before deletion.
 *
 * @param truckId  the ID of the truck to decommission and delete
 */
public record DeleteTruckCommand(
        String truckId
) {}