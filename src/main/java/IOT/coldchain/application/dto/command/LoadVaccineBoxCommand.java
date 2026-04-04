package IOT.coldchain.application.dto.command;

/**
 * COMMAND — LoadVaccineBoxCommand
 *
 * Represents the user's intent to load a vaccine box into a specific
 * compartment zone of a refrigerated truck.
 *
 * This action is performed by a VaccineDispatcher before dispatch.
 *
 * Handled by: ManageCargoUseCase
 * Domain method called: truck.loadVaccineBox(zoneId, serialNumber, medicineName)
 * Business rules enforced by domain:
 *   - Truck must not be SPOILED or SCRAPPED.
 *   - Target zone must not be SPOILED.
 *   - Serial number must be unique within the zone.
 *   - Both serialNumber and medicineName must not be blank.
 *
 * @param truckId       the truck to load cargo into
 * @param zoneId        the specific zone to place the box into
 * @param serialNumber  globally unique box identifier (e.g., "SN-2024-001")
 * @param medicineName  name of the medicine in this box (e.g., "Polio Vaccine OPV")
 */
public record LoadVaccineBoxCommand(
        String truckId,
        String zoneId,
        String serialNumber,
        String medicineName
) {}