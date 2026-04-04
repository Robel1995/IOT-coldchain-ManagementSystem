package IOT.coldchain.application.dto.query;

/**
 * QUERY — GetAllDriversQuery
 *
 * Represents the intent to retrieve a paginated summary list of all
 * registered TruckDrivers in the system.
 *
 * Used by the EPSS fleet management dashboard for driver roster overview.
 *
 * Handled by: GetDriverQueryUseCase
 * Returns: List<DriverSummaryView>
 *
 * @param page  zero-based page number for pagination (0 = first page)
 * @param size  number of results per page
 */
public record GetAllDriversQuery(
        int page,
        int size
) {}
