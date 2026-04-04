package IOT.coldchain.application.dto.view;

/**
 * READ MODEL — DispatcherSummaryView
 *
 * A lightweight projection of a VaccineDispatcher for list-based queries
 * (GetAllDispatchersQuery).
 *
 * @param dispatcherId      unique dispatcher identifier
 * @param fullName          dispatcher's full name
 * @param email             contact email address
 * @param totalShipments    total number of shipment orders this dispatcher has recorded
 */
public record DispatcherSummaryView(
        String dispatcherId,
        String fullName,
        String email,
        int totalShipments
) {}