package IOT.coldchain.application.dto.query;

/**
 * QUERY — GetAllDispatchersQuery
 *
 * Represents the intent to retrieve a paginated summary list of all
 * registered VaccineDispatchers in the system.
 *
 * Used by the EPSS staff management dashboard.
 *
 * Handled by: GetDispatcherQueryUseCase
 * Returns: List<DispatcherSummaryView>
 *
 * @param page  zero-based page number for pagination (0 = first page)
 * @param size  number of results per page
 */
public record GetAllDispatchersQuery(
        int page,
        int size
) {}
