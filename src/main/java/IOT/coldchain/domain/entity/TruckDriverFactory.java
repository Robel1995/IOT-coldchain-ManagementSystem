
package IOT.coldchain.domain.entity;

import IOT.coldchain.domain.valueobject.ContactInfo;
import IOT.coldchain.domain.valueobject.DriversLicense;

import java.util.UUID;


/**
 * FACTORY — TruckDriverFactory
 *
 * Creates new TruckDriver aggregates.
 *
 * Responsibilities:
 *  1. Generate a unique driverId (UUID).
 *  2. Validate creation-time business rules (e.g., license not expired).
 *  3. Construct the entity via its package-private constructor.
 *
 * The Application layer (Command Handler) calls createNew() when an admin
 * registers a new driver. The JPA adapter calls TruckDriver.reconstitute()
 * when loading an existing driver from the database.
 */

public final class TruckDriverFactory {

    private TruckDriverFactory() {
        throw new UnsupportedOperationException("Factory class — do not instantiate.");
    }


/**
     * Creates a new TruckDriver.
     *
     * Business rule: the provided license must not be expired at registration time.
     * This is enforced inside the TruckDriver constructor.
     *
     * @param fullName    driver's full legal name
     * @param contactInfo ContactInfo Value Object (email + phone)
     * @param license     DriversLicense Value Object (must not be expired)
     * @return a new TruckDriver with a generated driverId
     */

    public static TruckDriver createNew(
            String fullName,
            ContactInfo contactInfo,
            DriversLicense license
    ) {
        String driverId = UUID.randomUUID().toString();
        return new TruckDriver(driverId, fullName, contactInfo, license);
    }
}