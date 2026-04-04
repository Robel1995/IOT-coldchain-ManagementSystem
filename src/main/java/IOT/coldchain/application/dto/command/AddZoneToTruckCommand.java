package IOT.coldchain.application.dto.command;

/**
 * COMMAND — AddZoneToTruckCommand
 *
 * Represents the user's intent to add a new compartment zone to an existing truck.
 *
 * Used when a truck needs a new temperature-controlled section after registration
 * (e.g., expanding storage capacity mid-deployment).
 *
 * Handled by: ManageZoneUseCase
 * Domain method called: truck.addZone(...)
 * Business rules enforced by domain:
 *   - Truck must not be SPOILED or SCRAPPED.
 *   - Zone name must be unique within this truck.
 *   - Temperature values must satisfy TemperatureRange validation rules.
 *
 * @param truckId   the truck to add the zone to
 * @param zoneName  name for the new zone (e.g., "ZONE_B", "ULTRA_COLD")
 * @param minTemp   minimum safe temperature for the zone in °C
 * @param maxTemp   maximum safe temperature for the zone in °C
 */
public record AddZoneToTruckCommand(
        String truckId,
        String zoneName,
        double minTemp,
        double maxTemp
) {}