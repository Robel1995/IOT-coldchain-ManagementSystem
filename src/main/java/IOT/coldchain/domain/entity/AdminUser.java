package IOT.coldchain.domain.entity;

import IOT.coldchain.domain.entity.base.AggregateRoot;
import IOT.coldchain.domain.valueobject.ContactInfo;

import java.util.Objects;

/**
 * ENTITY — AdminUser (Independent Aggregate Root)
 *
 * Represents an EPSS system administrator who monitors all trucks on the
 * dashboard and can issue commands to drivers.
 *
 * Receives ALL notification levels on ALL channels — the admin is the
 * highest-level stakeholder in the cold-chain monitoring system.
 *
 * Notification routing:
 *  - ALERT:     dashboard + email
 *  - NOTIFY:    dashboard + SMS
 *  - EMERGENCY: dashboard + SMS + Telegram
 *  - SPOILED:   dashboard + SMS + Telegram + email
 *
 * Referenced in DriverCommand.issuedByAdminId — commands are attributed to admins.
 */
public class AdminUser extends AggregateRoot{

    private final String adminId;
    private final String fullName;
    private ContactInfo contactInfo;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR — package-private: use AdminUserFactory.createNew()
    // -------------------------------------------------------------------------

    AdminUser(String adminId, String fullName, ContactInfo contactInfo) {
        if (adminId == null || adminId.isBlank())
            throw new IllegalArgumentException("adminId cannot be blank.");
        if (fullName == null || fullName.isBlank())
            throw new IllegalArgumentException("Admin fullName cannot be blank.");
        Objects.requireNonNull(contactInfo, "ContactInfo cannot be null.");

        this.adminId = adminId.trim();
        this.fullName = fullName.trim();
        this.contactInfo = contactInfo;
    }

    // -------------------------------------------------------------------------
    // DOMAIN BEHAVIOUR
    // -------------------------------------------------------------------------

    /**
     * Updates contact info. Value Object replacement.
     */
    public void updateContactInfo(ContactInfo newContactInfo) {
        Objects.requireNonNull(newContactInfo, "New ContactInfo cannot be null.");
        this.contactInfo = newContactInfo;
    }

    // -------------------------------------------------------------------------
    // RECONSTITUTION — for Infrastructure adapter ONLY
    // -------------------------------------------------------------------------

    public static AdminUser reconstitute(
            String adminId, String fullName, ContactInfo contactInfo
    ) {
        return new AdminUser(adminId, fullName, contactInfo);
    }

    // -------------------------------------------------------------------------
    // ENTITY CONTRACT
    // -------------------------------------------------------------------------



    @Override
    public String toString() {
        return "AdminUser[id=" + adminId + ", name=" + fullName + "]";
    }

    // -------------------------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------------------------

    public String getAdminId()          { return adminId; }
    public String getFullName()         { return fullName; }
    public ContactInfo getContactInfo() { return contactInfo; }

    @Override
    public String getAggregateId() {
        return getAdminId();
    }
}