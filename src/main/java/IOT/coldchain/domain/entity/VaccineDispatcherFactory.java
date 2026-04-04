package IOT.coldchain.domain.entity;

import IOT.coldchain.domain.valueobject.ContactInfo;

import java.util.UUID;

/**
 * FACTORY — VaccineDispatcherFactory
 *
 * Creates new VaccineDispatcher aggregates (the EPSS staff who load vaccine trucks).
 * The Application layer calls createNew() when registering a new dispatcher.
 * The JPA adapter calls VaccineDispatcher.reconstitute() when loading from DB.
 */
public final class VaccineDispatcherFactory {

    private VaccineDispatcherFactory() {
        throw new UnsupportedOperationException("Factory class — do not instantiate.");
    }

    /**
     * @param fullName    dispatcher's full legal name
     * @param contactInfo ContactInfo Value Object (email + phone, already validated)
     * @return a new VaccineDispatcher with a generated dispatcherId
     */
    public static VaccineDispatcher createNew(String fullName, ContactInfo contactInfo) {
        String dispatcherId = UUID.randomUUID().toString();
        return new VaccineDispatcher(dispatcherId, fullName, contactInfo);
    }
}