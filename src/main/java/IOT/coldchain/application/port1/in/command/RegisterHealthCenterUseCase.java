package IOT.coldchain.application.port.in.command;

import IOT.coldchain.application.dto.command.AddPendingOrderCommand;
import IOT.coldchain.application.dto.command.ConfirmOrderDeliveredCommand;
import IOT.coldchain.application.dto.command.RegisterHealthCenterCommand;
import IOT.coldchain.application.dto.command.UpdateHealthCenterContactCommand;

/**
 * INBOUND PORT (USE CASE) — RegisterHealthCenterUseCase
 *
 * Defines the system's capabilities for managing the HealthCenter aggregate:
 * registration, contact updates, and pending shipment order management.
 *
 * ─── ISP Application ──────────────────────────────────────────────────────────
 * HealthCenter is a distinct aggregate root from all others. Its management
 * interface is isolated so that the HealthCenter controller, handler, and
 * repository are fully decoupled from truck, driver, and dispatcher operations.
 *
 * ─── Notification Linkage ──────────────────────────────────────────────────────
 * Health centers receive emergency and spoiled notifications when their pending
 * orders are threatened. The EventPublisher (infrastructure) resolves which
 * health centers to notify by looking up centers with matching pending orders.
 * This use case manages the pending order list that makes that lookup possible.
 *
 * ─── CQRS Role ────────────────────────────────────────────────────────────────
 * WRITE side. Registration returns the generated centerId. Others return void.
 *
 * Implementations:
 *   @see IOT.coldchain.application.handler.RegisterHealthCenterHandler
 */
public interface RegisterHealthCenterUseCase {

    /**
     * Registers a new HealthCenter aggregate.
     *
     * Handler responsibilities:
     *   1. Build GpsCoordinate(command.latitude(), command.longitude()) VO.
     *   2. Build ContactInfo(command.email(), command.phoneNumber()) VO.
     *   3. Call HealthCenterFactory.createNew(command.centerName(), location, contactInfo).
     *   4. Persist via HealthCenterRepository.save(center).
     *   5. Return the generated centerId.
     *
     * @param command encapsulates centerName, latitude, longitude, email, phoneNumber
     * @return the generated UUID centerId of the newly registered health center
     * @throws IllegalArgumentException if GPS coordinates, or contact info format is invalid
     */
    String handle(RegisterHealthCenterCommand command);

    /**
     * Updates a HealthCenter's contact information.
     *
     * Handler responsibilities:
     *   1. Load center via HealthCenterRepository.findById(command.centerId()).
     *   2. Build new ContactInfo(command.email(), command.phoneNumber()) VO.
     *   3. Call center.updateContactInfo(newContactInfo).
     *   4. Persist via HealthCenterRepository.save(center).
     *
     * @param command encapsulates centerId, new email, and new phoneNumber
     * @throws IllegalArgumentException if center not found, or contact format is invalid
     */
    void handle(UpdateHealthCenterContactCommand command);

    /**
     * Registers an incoming shipment order as pending at a HealthCenter.
     *
     * Called when a dispatcher records a new shipment destined for this center,
     * so the system knows to notify the center if that shipment is compromised.
     *
     * Handler responsibilities:
     *   1. Load center via HealthCenterRepository.findById(command.centerId()).
     *   2. Build new ShipmentOrder(command.orderId(), command.destination(),
     *              command.scheduledDate()) VO.
     *   3. Call center.addPendingOrder(order).
     *   4. Persist via HealthCenterRepository.save(center).
     *
     * @param command encapsulates centerId, orderId, destination, and scheduledDate
     * @throws IllegalArgumentException if center not found, or ShipmentOrder fields are blank
     * @throws IllegalStateException    if the orderId is already registered as pending
     */
    void handle(AddPendingOrderCommand command);

    /**
     * Confirms that a pending shipment order has been successfully delivered.
     *
     * Handler responsibilities:
     *   1. Load center via HealthCenterRepository.findById(command.centerId()).
     *   2. Call center.confirmOrderDelivered(command.orderId()).
     *   3. Persist via HealthCenterRepository.save(center).
     *
     * @param command encapsulates centerId and orderId
     * @throws IllegalArgumentException if center not found, or orderId not in pending list
     */
    void handle(ConfirmOrderDeliveredCommand command);
}