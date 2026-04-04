package IOT.coldchain.application.dto.query;

/**
 * QUERY — GetDispatcherByIdQuery
 *
 * Represents the intent to retrieve the full profile and details of a single
 * VaccineDispatcher aggregate, including contact information and shipment history.
 *
 *  Returns flat projection, not aggregate.
 *
 * Handled by: GetDispatcherQueryUseCase
 * Returns: DispatcherDetailView
 *
 * @param dispatcherId  the ID of the VaccineDispatcher to retrieve
 */
public record GetDispatcherByIdQuery(
        String dispatcherId
) {}
