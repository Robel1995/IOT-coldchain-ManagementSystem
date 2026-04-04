package IOT.coldchain.domain.valueobject;

import java.time.LocalDate;
import java.util.Objects;

/**
 * VALUE OBJECT — ShipmentOrder
 *
 * An immutable record of what was dispatched, to where, and when.
 * Not an Entity — a shipment order is fully defined by its contents.
 * If the same order is re-issued, it becomes a new ShipmentOrder.
 *
 * Used by VaccineDispatcher (tracks what they loaded) and
 * HealthCenter (tracks what they are expecting to receive).
 */
public final class ShipmentOrder {

    private final String orderId;
    private final String destinationHealthCenterId;
    private final String destinationName;
    private final LocalDate scheduledDeliveryDate;

    public ShipmentOrder(
            String orderId,
            String destinationHealthCenterId,
            String destinationName,
            LocalDate scheduledDeliveryDate
    ) {
        if (orderId == null || orderId.isBlank())
            throw new IllegalArgumentException("orderId cannot be null or blank.");
        if (destinationHealthCenterId == null || destinationHealthCenterId.isBlank())
            throw new IllegalArgumentException("destinationHealthCenterId cannot be blank.");
        if (destinationName == null || destinationName.isBlank())
            throw new IllegalArgumentException("destinationName cannot be blank.");
        Objects.requireNonNull(scheduledDeliveryDate, "scheduledDeliveryDate cannot be null.");

        this.orderId = orderId.trim();
        this.destinationHealthCenterId = destinationHealthCenterId.trim();
        this.destinationName = destinationName.trim();
        this.scheduledDeliveryDate = scheduledDeliveryDate;
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(scheduledDeliveryDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShipmentOrder)) return false;
        ShipmentOrder that = (ShipmentOrder) o;
        return Objects.equals(orderId, that.orderId)
                && Objects.equals(destinationHealthCenterId, that.destinationHealthCenterId)
                && Objects.equals(scheduledDeliveryDate, that.scheduledDeliveryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, destinationHealthCenterId, scheduledDeliveryDate);
    }

    @Override
    public String toString() {
        return "ShipmentOrder[id=" + orderId
                + ", to=" + destinationName
                + ", on=" + scheduledDeliveryDate + "]";
    }

    public String getOrderId()                     { return orderId; }
    public String getDestinationHealthCenterId()   { return destinationHealthCenterId; }
    public String getDestinationName()             { return destinationName; }
    public LocalDate getScheduledDeliveryDate()    { return scheduledDeliveryDate; }
}