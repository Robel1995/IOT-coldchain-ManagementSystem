package IOT.coldchain.application.dto.command;

/**
 * COMMAND — ConfirmOrderDeliveredCommand
 *
 * Represents the user's intent to confirm that a pending shipment order
 * has been successfully delivered to a HealthCenter.
 *
 * This removes the order from the health center's pending list.
 *
 * Handled by: RegisterHealthCenterUseCase
 * Domain method called: healthCenter.confirmOrderDelivered(orderId)
 *
 * @param centerId  the health center receiving the delivery
 * @param orderId   the shipment order being confirmed as delivered
 */
public record ConfirmOrderDeliveredCommand(
        String centerId,
        String orderId
) {}
