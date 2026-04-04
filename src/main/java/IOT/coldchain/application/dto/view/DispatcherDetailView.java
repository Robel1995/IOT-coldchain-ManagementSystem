package IOT.coldchain.application.dto.view;

import java.time.LocalDate;
import java.util.List;

/**
 * READ MODEL — DispatcherDetailView
 *
 * A flat projection of a VaccineDispatcher aggregate for GetDispatcherByIdQuery.
 *
 * @param dispatcherId    unique dispatcher identifier
 * @param fullName        dispatcher's full name
 * @param email           contact email address
 * @param phoneNumber     contact phone number
 * @param shipmentHistory list of all shipment orders this dispatcher has recorded
 */
public record DispatcherDetailView(
        String dispatcherId,
        String fullName,
        String email,
        String phoneNumber,
        List<ShipmentOrderView> shipmentHistory
) {}