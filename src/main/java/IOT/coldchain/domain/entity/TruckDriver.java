package IOT.coldchain.domain.entity;

import IOT.coldchain.domain.entity.base.AggregateRoot;
import IOT.coldchain.domain.valueobject.ContactInfo;
import IOT.coldchain.domain.valueobject.DriversLicense;

import java.util.Objects;

/**
 * ENTITY — TruckDriver (Independent Aggregate Root)
 *
 * Represents the person who drives the refrigerated truck.
 * This is a SEPARATE aggregate from RefrigeratedTruck — they are
 * linked by ID reference only (truck.assignedDriverId = driver.driverId).
 *
 * Why separate aggregate?
 *  A driver can be reassigned between trucks, can have their license updated,
 *  and exists independently of any truck. Embedding the driver inside the truck
 *  aggregate would make those operations impossible without going through the truck.
 *
 * Receives notifications:
 *  - ALERT: dashboard
 *  - NOTIFY: SMS + dashboard
 *  - EMERGENCY: urgent SMS
 *  - SPOILED: SMS + all channels
 */
public class TruckDriver extends AggregateRoot {

    private final String driverId;
    private final String fullName;
    private ContactInfo contactInfo;
    private DriversLicense license;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR — package-private: use TruckDriverFactory.createNew()
    // -------------------------------------------------------------------------

    TruckDriver(String driverId, String fullName, ContactInfo contactInfo, DriversLicense license) {
        if (driverId == null || driverId.isBlank())
            throw new IllegalArgumentException("driverId cannot be blank.");
        if (fullName == null || fullName.isBlank())
            throw new IllegalArgumentException("Driver fullName cannot be blank.");
        Objects.requireNonNull(contactInfo, "ContactInfo cannot be null.");
        Objects.requireNonNull(license, "DriversLicense cannot be null.");

        if (license.isExpired()) {
            throw new IllegalStateException(
                    "Business Rule Violation: Cannot register driver '"
                            + fullName + "' with an expired license: " + license
            );
        }

        this.driverId = driverId.trim();
        this.fullName = fullName.trim();
        this.contactInfo = contactInfo;
        this.license = license;
    }

    // -------------------------------------------------------------------------
    // DOMAIN BEHAVIOUR
    // -------------------------------------------------------------------------

    /**
     * Updates the driver's contact information.
     * ContactInfo is a Value Object — we replace the old one.
     */
    public void updateContactInfo(ContactInfo newContactInfo) {
        Objects.requireNonNull(newContactInfo, "New ContactInfo cannot be null.");
        this.contactInfo = newContactInfo;
    }

    /**
     * Renews the driver's license.
     * Business rule: the new license must not be expired.
     */
    public void renewLicense(DriversLicense newLicense) {
        Objects.requireNonNull(newLicense, "New DriversLicense cannot be null.");
        if (newLicense.isExpired()) {
            throw new IllegalArgumentException(
                    "Business Rule Violation: Cannot renew license with an already-expired license: "
                            + newLicense
            );
        }
        this.license = newLicense;
    }

    // -------------------------------------------------------------------------
    // RECONSTITUTION — for Infrastructure adapter ONLY
    // -------------------------------------------------------------------------

    public static TruckDriver reconstitute(
            String driverId, String fullName, ContactInfo contactInfo, DriversLicense license
    ) {
        // Bypass the expiry check — existing drivers may have licenses that expired
        // after registration. The system should warn, not throw.
        TruckDriver driver = new TruckDriver(driverId, fullName, contactInfo, license);
        return driver;
    }

    // -------------------------------------------------------------------------
    // ENTITY CONTRACT
    // -------------------------------------------------------------------------

    @Override
    public String getAggregateId() {
        return getDriverId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TruckDriver)) return false;
        return Objects.equals(driverId, ((TruckDriver) o).driverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId);
    }

    @Override
    public String toString() {
        return "TruckDriver[id=" + driverId + ", name=" + fullName + "]";
    }

    // -------------------------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------------------------

    public String getDriverId()         { return driverId; }
    public String getFullName()         { return fullName; }
    public ContactInfo getContactInfo() { return contactInfo; }
    public DriversLicense getLicense()  { return license; }
}