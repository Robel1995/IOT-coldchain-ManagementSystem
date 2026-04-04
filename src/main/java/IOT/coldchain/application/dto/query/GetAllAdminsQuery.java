package IOT.coldchain.application.dto.query;

/**
 * QUERY — GetAllAdminsQuery
 *
 * Represents the intent to retrieve a paginated summary list of all
 * registered AdminUsers in the system.
 *
 * Used by the EPSS system administration dashboard.
 *
 * Handled by: GetAdminQueryUseCase
 * Returns: List<AdminSummaryView>
 *
 * @param page  zero-based page number for pagination (0 = first page)
 * @param size  number of results per page
 */
public record GetAllAdminsQuery(
        int page,
        int size
) {}
