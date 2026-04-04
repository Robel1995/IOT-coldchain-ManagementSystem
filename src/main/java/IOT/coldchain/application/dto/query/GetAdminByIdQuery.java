package IOT.coldchain.application.dto.query;

/**
 * QUERY — GetAdminByIdQuery
 *
 * Represents the intent to retrieve the full profile and details of a single
 * AdminUser aggregate, including contact information.
 *
 * Handled by: GetAdminQueryUseCase
 * Returns: AdminDetailView
 *
 * @param adminId  the ID of the AdminUser to retrieve
 */
public record GetAdminByIdQuery(
        String adminId
) {}
