package IOT.coldchain.application.dto.query;

/**
 * QUERY — GetSensorReadingHistoryQuery
 *
 * Represents the intent to retrieve the full temperature reading audit trail
 * for a specific sensor in a specific zone.
 *
 * Used for trend analysis, incident investigation, and compliance reporting.
 * Readings are returned in chronological order (oldest first).
 *
 * Handled by: GetTruckQueryUseCase
 * Returns: List<TemperatureReadingView>
 *
 * @param truckId     the truck the sensor belongs to
 * @param zoneId      the zone where the sensor is installed
 * @param macAddress  the MAC address of the sensor whose history is requested
 */
public record GetSensorReadingHistoryQuery(
        String truckId,
        String zoneId,
        String macAddress
) {}