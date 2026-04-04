package IOT.coldchain.application.dto.command;

import java.time.LocalDate;

/**
 * COMMAND — RecordShipmentCommand
 *
 * Represents the user's intent to record a shipment order against a
 * VaccineDispatcher's history. This tracks which dispatcher was responsible
 * for loading and dispatching each shipment.
 *
 * Handled by: RegisterDispatcherUseCase
 * Domain method called: dispatcher.recordShipment(order)
 *
 * @param dispatcherId   the dispatcher who loaded the shipment
 * @param orderId        unique identifier for the shipment order
 * @param destinationName    destination description (e.g., "Black Lion Hospital, Addis Ababa")
 * @param scheduledDeliveryDate  planned delivery date
 */
public record RecordShipmentCommand(
        String dispatcherId,
        String orderId,
        String destinationHealthCenterId,  // ID for database lookup
        String destinationName,
        LocalDate scheduledDeliveryDate
) {}
