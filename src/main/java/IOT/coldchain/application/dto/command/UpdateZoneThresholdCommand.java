package IOT.coldchain.application.dto.command;

/**
 * COMMAND — UpdateZoneThresholdCommand
 *
 * Represents the user's intent to replace the temperature threshold
 * for a specific compartment zone.
 *
 * Value Object immutability rule: The domain discards the old TemperatureRange
 * and creates a new one — it never mutates it in place. This command triggers
 * that replacement.
 *
 * Handled by: UpdateZoneThresholdUseCase
 * Domain method called: truck.updateZoneThreshold(zoneId, new TemperatureRange(newMinTemp, newMaxTemp))
 * Business rules enforced by domain:
 *   - Truck must not be SPOILED or SCRAPPED.
 *   - Zone must be in SAFE status (cannot update threshold during ALERT/WARNING/EMERGENCY/SPOILED).
 *   - New minTemp must be strictly less than newMaxTemp.
 *   - Both values must be above absolute zero (−273.15°C).
 *
 * @param truckId     the truck containing the zone
 * @param zoneId      the zone whose threshold is being updated
 * @param newMinTemp  new lower safe temperature bound in °C
 * @param newMaxTemp  new upper safe temperature bound in °C
 */
public record UpdateZoneThresholdCommand(
        String truckId,
        String zoneId,
        double newMinTemp,
        double newMaxTemp
) {}