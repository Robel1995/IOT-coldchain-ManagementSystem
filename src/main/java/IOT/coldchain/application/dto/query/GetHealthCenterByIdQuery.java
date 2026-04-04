package IOT.coldchain.application.dto.query;

/**
 * QUERY — GetHealthCenterByIdQuery
 *
 * Represents the intent to retrieve the full profile and details of a single
 * HealthCenter aggregate, including contact information, GPS location, and
 * pending shipment orders.
 *
 *  Returns flat projection, not aggregate.
 *
 * Handled by: GetHealthCenterQueryUseCase
 * Returns: HealthCenterDetailView
 *
 * @param centerId  the ID of the HealthCenter to retrieve
 */
public record GetHealthCenterByIdQuery(
        String centerId
) {}
