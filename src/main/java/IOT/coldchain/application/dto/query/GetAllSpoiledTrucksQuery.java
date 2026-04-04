package IOT.coldchain.application.dto.query;

/**
 * QUERY — GetAllSpoiledTrucksQuery
 *
 * Represents the intent to retrieve a summary list of all refrigerated trucks
 * currently in SPOILED status. Used by the EPSS dashboard to display
 * compromised shipments requiring attention.
 *
 * (TruckSummaryView list), not domain aggregates.
 *
 * Handled by: GetTruckQueryUseCase
 * Returns: List<TruckSummaryView>
 *
 * This record has no fields — it is a marker query used to distinguish
 * this operation from others in the use case interface (ISP compliance).
 */
public record GetAllSpoiledTrucksQuery() {}