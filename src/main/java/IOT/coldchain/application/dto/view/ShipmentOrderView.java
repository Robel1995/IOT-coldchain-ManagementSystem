package IOT.coldchain.application.dto.view;

import java.time.LocalDate;

/**
 * READ MODEL — ShipmentOrderView
 *
 * A flat projection of a ShipmentOrder value object.
 * Embedded in DispatcherDetailView and HealthCenterDetailView.
 *
 * @param orderId        unique shipment order identifier
 * @param destination    destination description (e.g., "Black Lion Hospital, Addis Ababa")
 * @param scheduledDate  planned delivery date
 */
public record ShipmentOrderView(
        String orderId,
        String destination,
        LocalDate scheduledDate
) {}