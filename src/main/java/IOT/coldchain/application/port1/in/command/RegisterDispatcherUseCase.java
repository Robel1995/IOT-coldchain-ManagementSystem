package IOT.coldchain.application.port.in.command;

import IOT.coldchain.application.dto.command.RecordShipmentCommand;
import IOT.coldchain.application.dto.command.RegisterDispatcherCommand;
import IOT.coldchain.application.dto.command.UpdateDispatcherContactCommand;

/**
 * INBOUND PORT (USE CASE) — RegisterDispatcherUseCase
 *
 * Defines the system's capabilities for managing the VaccineDispatcher aggregate:
 * registration, contact updates, and recording shipment orders.
 *
 * ─── ISP Application ──────────────────────────────────────────────────────────
 * VaccineDispatcher management is wholly separate from truck, driver, admin, and
 * health center management. The dispatcher is the EPSS staff member who physically
 * loads trucks. Their profile and shipment history are self-contained concerns.
 *
 * ─── CQRS Role ────────────────────────────────────────────────────────────────
 * WRITE side. Registration returns the generated dispatcherId. Others return void.
 *
 * Implementations:
 *   @see IOT.coldchain.application.handler.RegisterDispatcherHandler
 */
public interface RegisterDispatcherUseCase {

    /**
     * Registers a new VaccineDispatcher aggregate.
     *
     * Handler responsibilities:
     *   1. Build ContactInfo(command.email(), command.phoneNumber()) VO.
     *   2. Call VaccineDispatcherFactory.createNew(command.fullName(), contactInfo).
     *   3. Persist via DispatcherRepository.save(dispatcher).
     *   4. Return the generated dispatcherId.
     *
     * @param command encapsulates fullName, email, and phoneNumber
     * @return the generated UUID dispatcherId of the newly registered dispatcher
     * @throws IllegalArgumentException if ContactInfo construction fails (invalid format)
     */
    String handle(RegisterDispatcherCommand command);

    /**
     * Updates a VaccineDispatcher's contact information.
     *
     * Handler responsibilities:
     *   1. Load dispatcher via DispatcherRepository.findById(command.dispatcherId()).
     *   2. Build new ContactInfo(command.email(), command.phoneNumber()) VO.
     *   3. Call dispatcher.updateContactInfo(newContactInfo).
     *   4. Persist via DispatcherRepository.save(dispatcher).
     *
     * @param command encapsulates dispatcherId, new email, and new phoneNumber
     * @throws IllegalArgumentException if dispatcher not found, or contact format is invalid
     */
    void handle(UpdateDispatcherContactCommand command);

    /**
     * Records a shipment order against the dispatcher's history.
     *
     * Handler responsibilities:
     *   1. Load dispatcher via DispatcherRepository.findById(command.dispatcherId()).
     *   2. Build new ShipmentOrder(command.orderId(), command.destination(),
     *              command.scheduledDate()) VO.
     *   3. Call dispatcher.recordShipment(order).
     *   4. Persist via DispatcherRepository.save(dispatcher).
     *
     * @param command encapsulates dispatcherId, orderId, destination, and scheduledDate
     * @throws IllegalArgumentException if dispatcher not found, or ShipmentOrder fields are blank
     */
    void handle(RecordShipmentCommand command);
}