package IOT.coldchain.domain.entity;

import java.util.Objects;

/**
 * ENTITY — VaccineBox (Child Entity of CompartmentZone)
 *
 * Represents a physical box of vaccines loaded into a specific compartment zone.
 * Identity: serialNumber — globally unique across all trucks and zones.
 *
 * Encapsulation rules:
 *  - Constructor is package-private: only the CompartmentZone (in the same package)
 *    can create a VaccineBox. External code MUST go through the aggregate root.
 *  - markAsRuined() is package-private: only CompartmentZone can ruin a box
 *    as part of the CASCADE EFFECT when a zone temperature breach is confirmed.
 *
 * equals/hashCode: identity-based on serialNumber.
 *  Why it matters: when the aggregate calls list.removeIf(item -> item.getSerialNumber().equals(sn)),
 *  having a proper equals makes the intent explicit and safe. Also required if
 *  VaccineBox is ever placed in a Set or used as a Map key.
 */
public class VaccineBox {

    private final String serialNumber;  // globally unique identifier for this box
    private final String medicineName;  // e.g., "Polio Vaccine OPV", "Yellow Fever 17D"
    private boolean isRuined;           // set to true by cascade when zone is spoiled

    // -------------------------------------------------------------------------
    // CONSTRUCTOR — package-private, only CompartmentZone can build this
    // -------------------------------------------------------------------------

    VaccineBox(String serialNumber, String medicineName) {
        if (serialNumber == null || serialNumber.isBlank())
            throw new IllegalArgumentException("VaccineBox serialNumber cannot be null or blank.");
        if (medicineName == null || medicineName.isBlank())
            throw new IllegalArgumentException("VaccineBox medicineName cannot be null or blank.");

        this.serialNumber = serialNumber.trim();
        this.medicineName = medicineName.trim();
        this.isRuined = false;
    }

    // -------------------------------------------------------------------------
    // DOMAIN BEHAVIOUR
    // -------------------------------------------------------------------------

    /**
     * Marks this box as ruined due to a confirmed temperature breach in its zone.
     * Package-private — only CompartmentZone may call this as part of cascade.
     */
    void markAsRuined() {
        this.isRuined = true;
    }

    // -------------------------------------------------------------------------
    // RECONSTITUTION — for the Infrastructure / JPA adapter ONLY
    // -------------------------------------------------------------------------

    /**
     * Rebuilds a VaccineBox from database state.
     * Called ONLY by the JPA adapter when loading an existing aggregate from DB.
     * Must never be called by application or domain logic for new creation.
     */
    public static VaccineBox reconstitute(String serialNumber, String medicineName, boolean isRuined) {
        VaccineBox box = new VaccineBox(serialNumber, medicineName);
        box.isRuined = isRuined;
        return box;
    }

    // -------------------------------------------------------------------------
    // ENTITY CONTRACT: equals and hashCode by identity (serialNumber)
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VaccineBox)) return false;
        VaccineBox that = (VaccineBox) o;
        return Objects.equals(serialNumber, that.serialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNumber);
    }

    @Override
    public String toString() {
        return "VaccineBox[sn=" + serialNumber
                + ", medicine=" + medicineName
                + ", ruined=" + isRuined + "]";
    }

    // -------------------------------------------------------------------------
    // GETTERS — no setters (mutation only through domain methods)
    // -------------------------------------------------------------------------

    public String getSerialNumber() { return serialNumber; }
    public String getMedicineName() { return medicineName; }
    public boolean isRuined()       { return isRuined; }
}