package IOT.coldchain.domain.entity;

import IOT.coldchain.domain.valueobject.ContactInfo;

import java.util.UUID;

/**
 * FACTORY — AdminUserFactory
 *
 * Creates new AdminUser aggregates (EPSS system administrators).
 * The Application layer calls createNew() when an admin account is set up.
 * The JPA adapter calls AdminUser.reconstitute() when loading from DB.
 */
public final class AdminUserFactory {

    private AdminUserFactory() {
        throw new UnsupportedOperationException("Factory class — do not instantiate.");
    }

    /**
     * @param fullName    admin's full name
     * @param contactInfo ContactInfo Value Object (email + phone)
     * @return a new AdminUser with a generated adminId
     */
    public static AdminUser createNew(String fullName, ContactInfo contactInfo) {
        String adminId = UUID.randomUUID().toString();
        return new AdminUser(adminId, fullName, contactInfo);
    }
}