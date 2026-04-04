package IOT.coldchain.domain.entity;

import IOT.coldchain.domain.valueobject.ContactInfo;
import IOT.coldchain.domain.valueobject.GpsCoordinate;

import java.util.UUID;

/**
 * FACTORY — HealthCenterFactory
 *
 * Creates new HealthCenter aggregates (hospitals/clinics receiving EPSS vaccines).
 * The Application layer calls createNew() when registering a new health center.
 * The JPA adapter calls HealthCenter.reconstitute() when loading from DB.
 */
public final class HealthCenterFactory {

    private HealthCenterFactory() {
        throw new UnsupportedOperationException("Factory class — do not instantiate.");
    }

    /**
     * @param centerName  the official name of the health facility
     * @param location    GpsCoordinate Value Object
     * @param contactInfo ContactInfo Value Object
     * @return a new HealthCenter with a generated centerId
     */
    public static HealthCenter createNew(
            String centerName,
            GpsCoordinate location,
            ContactInfo contactInfo
    ) {
        String centerId = UUID.randomUUID().toString();
        return new HealthCenter(centerId, centerName, location, contactInfo);
    }
}