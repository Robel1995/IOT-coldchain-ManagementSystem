package IOT.coldchain.application.dto.query;

/**
 * QUERY — GetTruckByIdQuery
 *
 * Represents the intent to retrieve the full real-time status of a single
 * refrigerated truck, including all its zones, sensors, and cargo.
 *
 *  Queries bypass the rich domain model entirely and return
 * a flat, read-optimised TruckDetailView DTO instead of the aggregate.
 * No state mutation occurs. No @Transactional annotation needed.
 *
 * Handled by: GetTruckQueryUseCase
 * Returns: TruckDetailView
 *
 * @param truckId  the ID of the truck to retrieve
 */
public record GetTruckByIdQuery(
        String truckId
) {}