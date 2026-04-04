package IOT.coldchain.application.dto.query;

/**
 * QUERY — GetZoneDetailQuery
 *
 * Represents the intent to retrieve the full detail of a single compartment
 * zone within a truck, including its current status, temperature threshold,
 * all installed sensors with their reading history, and all loaded vaccine boxes.
 *
 * Handled by: GetTruckQueryUseCase
 * Returns: ZoneDetailView
 *
 * @param truckId  the ID of the truck containing the zone
 * @param zoneId   the ID of the zone to retrieve
 */
public record GetZoneDetailQuery(
        String truckId,
        String zoneId
) {}