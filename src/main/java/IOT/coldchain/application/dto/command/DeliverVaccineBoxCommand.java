package IOT.coldchain.application.dto.command;

/**
 * COMMAND — DeliverVaccineBoxCommand
 *
 * Represents the user's intent to deliver (remove) a specific vaccine box
 * from a truck zone and hand it off to a health center.
 *
 * PATIENT SAFETY CRITICAL: This operation is blocked by the domain if the
 * truck OR the target zone is in SPOILED status. The domain will throw an
 * IllegalStateException. The Application layer must never bypass this guard.
 *
 * Handled by: ManageCargoUseCase
 * Domain method called: truck.deliverVaccineBox(zoneId, serialNumber)
 * Business rules enforced by domain:
 *   - Truck must not be SPOILED (truck-level patient safety guard).
 *   - Zone must not be SPOILED (zone-level guard).
 *   - VaccineBox.isRuined must be false.
 *   - The box with this serialNumber must exist in the specified zone.
 *
 * @param truckId       the truck delivering the cargo
 * @param zoneId        the zone that holds the box
 * @param serialNumber  the serial number of the vaccine box to deliver
 */
public record DeliverVaccineBoxCommand(
        String truckId,
        String zoneId,
        String serialNumber
) {}