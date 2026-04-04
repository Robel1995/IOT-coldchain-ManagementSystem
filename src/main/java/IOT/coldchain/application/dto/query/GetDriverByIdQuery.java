package IOT.coldchain.application.dto.query;

/**
 * QUERY — GetDriverByIdQuery
 *
 * Represents the intent to retrieve the profile and details of a single
 * TruckDriver aggregate, including contact information and license status.
 *
 * Handled by: GetDriverQueryUseCase
 * Returns: DriverDetailView
 *
 * @param driverId  the ID of the TruckDriver to retrieve
 */
public record GetDriverByIdQuery(
        String driverId
) {}