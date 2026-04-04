package IOT.coldchain.domain.entity;

import IOT.coldchain.domain.entity.base.AggregateRoot;
import IOT.coldchain.domain.valueobject.ContactInfo;
import IOT.coldchain.domain.valueobject.GpsCoordinate;
import IOT.coldchain.domain.valueobject.ShipmentOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * ENTITY — HealthCenter (Independent Aggregate Root)
 *
 * Represents a hospital or health facility waiting to receive vaccine shipments.
 * Must be notified when a shipment they are expecting is compromised so they can
 * proactively reorder from EPSS.
 *
 * Notification rules:
 *  - ALERT:     NOT notified (internal truck problem, not their concern yet)
 *  - NOTIFY:    email only — "your shipment is under monitoring"
 *  - EMERGENCY: Telegram — "your shipment is compromised, prepare to reorder"
 *  - SPOILED:   email + Telegram — "shipment CONFIRMED spoiled, initiate reorder"
 *
 * pendingOrders tracks what this health center is currently expecting.
 * When a shipment is confirmed spoiled, the Application layer will call
 * markOrderAsRequiringReorder() so the center knows to reorder.
 */
public class HealthCenter extends AggregateRoot {

    private final String centerId;
    private final String centerName;
    private final GpsCoordinate location;    // Value Object: GPS for map/routing display
    private ContactInfo contactInfo;

    /**
     * Orders currently expected from EPSS.
     * Added when a dispatcher registers a shipment destined for this center.
     * Managed as Value Objects (ShipmentOrder is immutable).
     */
    private final List<ShipmentOrder> pendingOrders;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR — package-private: use HealthCenterFactory.createNew()
    // -------------------------------------------------------------------------

    HealthCenter(String centerId, String centerName, GpsCoordinate location, ContactInfo contactInfo) {
        if (centerId == null || centerId.isBlank())
            throw new IllegalArgumentException("centerId cannot be blank.");
        if (centerName == null || centerName.isBlank())
            throw new IllegalArgumentException("centerName cannot be blank.");
        Objects.requireNonNull(location, "GPS location cannot be null.");
        Objects.requireNonNull(contactInfo, "ContactInfo cannot be null.");

        this.centerId = centerId.trim();
        this.centerName = centerName.trim();
        this.location = location;
        this.contactInfo = contactInfo;
        this.pendingOrders = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // DOMAIN BEHAVIOUR
    // -------------------------------------------------------------------------

    /**
     * Registers a new incoming shipment order for this health center.
     * Called when a VaccineDispatcher dispatches a truck toward this center.
     *
     * @param order the expected shipment
     */
    public void addPendingOrder(ShipmentOrder order) {
        Objects.requireNonNull(order, "ShipmentOrder cannot be null.");
        boolean duplicate = pendingOrders.stream()
                .anyMatch(o -> o.getOrderId().equals(order.getOrderId()));
        if (duplicate) {
            throw new IllegalStateException(
                    "ShipmentOrder '" + order.getOrderId()
                            + "' is already registered for health center '" + centerName + "'."
            );
        }
        pendingOrders.add(order);
    }

    /**
     * Removes a pending order once delivery is confirmed.
     * Called by the Application layer after a successful deliverVaccineBox() operation.
     *
     * @param orderId the ID of the order to remove
     */
    public void confirmOrderDelivered(String orderId) {
        boolean removed = pendingOrders.removeIf(o -> o.getOrderId().equals(orderId));
        if (!removed) {
            throw new IllegalArgumentException(
                    "ShipmentOrder '" + orderId + "' was not found in pending orders "
                            + "for health center '" + centerName + "'."
            );
        }
    }

    /**
     * Updates contact information.
     * Value Object replacement — not mutation.
     */
    public void updateContactInfo(ContactInfo newContactInfo) {
        Objects.requireNonNull(newContactInfo, "New ContactInfo cannot be null.");
        this.contactInfo = newContactInfo;
    }

    // -------------------------------------------------------------------------
    // RECONSTITUTION — for Infrastructure adapter ONLY
    // -------------------------------------------------------------------------

    public static HealthCenter reconstitute(
            String centerId, String centerName, GpsCoordinate location,
            ContactInfo contactInfo, List<ShipmentOrder> pendingOrders
    ) {
        HealthCenter center = new HealthCenter(centerId, centerName, location, contactInfo);
        if (pendingOrders != null) center.pendingOrders.addAll(pendingOrders);
        return center;
    }

    // -------------------------------------------------------------------------
    // ENTITY CONTRACT
    // -------------------------------------------------------------------------

    @Override
    public String getAggregateId() {
        return getCenterId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HealthCenter)) return false;
        return Objects.equals(centerId, ((HealthCenter) o).centerId);
    }

    @Override
    public int hashCode() { return Objects.hash(centerId); }

    @Override
    public String toString() {
        return "HealthCenter[id=" + centerId + ", name=" + centerName + "]";
    }

    // -------------------------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------------------------

    public String getCenterId()            { return centerId; }
    public String getCenterName()          { return centerName; }
    public GpsCoordinate getLocation()     { return location; }
    public ContactInfo getContactInfo()    { return contactInfo; }

    public List<ShipmentOrder> getPendingOrders() {
        return Collections.unmodifiableList(pendingOrders);
    }
}