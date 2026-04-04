package IOT.coldchain.application.dto.query;

/**
 * QUERY — GetAllTrucksQuery
 *
 * Represents the intent to retrieve a paginated summary list of all
 * registered trucks in the system, regardless of status.
 *
 * Used by the EPSS fleet overview dashboard.
 *Returns projection list, not aggregates.
 *
 * Handled by: GetTruckQueryUseCase
 * Returns: List<TruckSummaryView>
 *
 * @param page  zero-based page number for pagination (0 = first page)
 * @param size  number of results per page
 */
public record GetAllTrucksQuery(
        int page,
        int size
) {}