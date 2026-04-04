package IOT.coldchain.application.dto.command;

import java.time.LocalDate;

/**
 * COMMAND — AddPendingOrderCommand
 *
 * Represents the user's intent to register a new pending shipment order
 * at a HealthCenter. This allows the system to track which health centers
 * are expecting deliveries and notify them if shipments are compromised.
 *
 * Handled by: RegisterHealthCenterUseCase
 * Domain method called: healthCenter.addPendingOrder(order)
 *
 * @param centerId       the health center expecting the delivery
 * @param orderId        unique identifier for the shipment order
 * @param destination    destination description
 * @param scheduledDate  planned delivery date
 */
public record AddPendingOrderCommand(
        String centerId,
        String orderId,
        String destination,
        LocalDate scheduledDate
) {}
