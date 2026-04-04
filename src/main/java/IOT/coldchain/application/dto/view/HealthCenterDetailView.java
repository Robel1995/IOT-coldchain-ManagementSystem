package IOT.coldchain.application.dto.view;

import java.util.List;

/**
 * READ MODEL — HealthCenterDetailView
 *
 * A flat projection of a HealthCenter aggregate for GetHealthCenterByIdQuery.
 *
 * @param centerId       unique health center identifier
 * @param centerName     name of the facility (e.g., "Black Lion Hospital")
 * @param email          contact email address for notifications
 * @param phoneNumber    contact phone number for SMS/Telegram notifications
 * @param latitude       GPS latitude of the facility
 * @param longitude      GPS longitude of the facility
 * @param pendingOrders  list of shipment orders currently expected by this center
 */
public record HealthCenterDetailView(
        String centerId,
        String centerName,
        String email,
        String phoneNumber,
        double latitude,
        double longitude,
        List<ShipmentOrderView> pendingOrders
) {}