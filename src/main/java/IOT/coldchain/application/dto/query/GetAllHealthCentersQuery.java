package IOT.coldchain.application.dto.query;

/**
 * QUERY — GetAllHealthCentersQuery
 *
 * Represents the intent to retrieve a paginated summary list of all
 * registered HealthCenters in the system.
 *
 * Used by the EPSS facility management dashboard.
 *
 * Handled by: GetHealthCenterQueryUseCase
 * Returns: List<HealthCenterSummaryView>
 *
 * @param page  zero-based page number for pagination (0 = first page)
 * @param size  number of results per page
 */
public record GetAllHealthCentersQuery(
        int page,
        int size
) {}
