package IOT.coldchain.domain.entity;

import IOT.coldchain.domain.entity.base.AggregateRoot;
import IOT.coldchain.domain.valueobject.ContactInfo;
import IOT.coldchain.domain.valueobject.ShipmentOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * ENTITY — VaccineDispatcher (Independent Aggregate Root)
 *
 * Represents the EPSS staff member who physically loads vaccine boxes into
 * the refrigerated truck before dispatch. This is the entity your original
 * design was missing — the person who initiates a shipment journey.
 *
 * Why a separate aggregate?
 *  A dispatcher exists independently of any truck or shipment. They may
 *  load multiple trucks on the same day, have their contact info updated,
 *  and must be notified when a shipment they loaded goes into alert/spoiled.
 *
 * Linked to RefrigeratedTruck via truckId reference when a shipment is initiated.
 *
 * Receives notifications:
 *  - ALERT:     email only
 *  - NOTIFY:    SMS
 *  - EMERGENCY: SMS + Telegram
 *  - SPOILED:   all channels
 */
public class VaccineDispatcher extends AggregateRoot{

    private final String dispatcherId;
    private final String fullName;
    private ContactInfo contactInfo;

    /**
     * History of shipment orders this dispatcher has created and loaded.
     * Each ShipmentOrder is a Value Object — immutable record of a dispatch.
     */
    private final List<ShipmentOrder> shipmentHistory;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR — package-private: use VaccineDispatcherFactory.createNew()
    // -------------------------------------------------------------------------

    VaccineDispatcher(String dispatcherId, String fullName, ContactInfo contactInfo) {
        if (dispatcherId == null || dispatcherId.isBlank())
            throw new IllegalArgumentException("dispatcherId cannot be blank.");
        if (fullName == null || fullName.isBlank())
            throw new IllegalArgumentException("Dispatcher fullName cannot be blank.");
        Objects.requireNonNull(contactInfo, "ContactInfo cannot be null.");

        this.dispatcherId = dispatcherId.trim();
        this.fullName = fullName.trim();
        this.contactInfo = contactInfo;
        this.shipmentHistory = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // DOMAIN BEHAVIOUR
    // -------------------------------------------------------------------------

    /**
     * Records a new shipment order created by this dispatcher.
     * A ShipmentOrder Value Object is immutable — we just add it to history.
     *
     * @param order the shipment order to record
     */
    public void recordShipment(ShipmentOrder order) {
        Objects.requireNonNull(order, "ShipmentOrder cannot be null.");
        shipmentHistory.add(order);
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

    public static VaccineDispatcher reconstitute(
            String dispatcherId, String fullName,
            ContactInfo contactInfo, List<ShipmentOrder> shipmentHistory
    ) {
        VaccineDispatcher dispatcher = new VaccineDispatcher(dispatcherId, fullName, contactInfo);
        if (shipmentHistory != null) dispatcher.shipmentHistory.addAll(shipmentHistory);
        return dispatcher;
    }

    // -------------------------------------------------------------------------
    // ENTITY CONTRACT
    // -------------------------------------------------------------------------

    @Override
    public String getAggregateId() {
        return getDispatcherId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VaccineDispatcher)) return false;
        return Objects.equals(dispatcherId, ((VaccineDispatcher) o).dispatcherId);
    }

    @Override
    public int hashCode() { return Objects.hash(dispatcherId); }

    @Override
    public String toString() {
        return "VaccineDispatcher[id=" + dispatcherId + ", name=" + fullName + "]";
    }

    // -------------------------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------------------------

    public String getDispatcherId()          { return dispatcherId; }
    public String getFullName()              { return fullName; }
    public ContactInfo getContactInfo()      { return contactInfo; }

    public List<ShipmentOrder> getShipmentHistory() {
        return Collections.unmodifiableList(shipmentHistory);
    }
}