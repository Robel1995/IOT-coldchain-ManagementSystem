package IOT.coldchain.application.port1.out;

import IOT.coldchain.domain.entity.AdminUser;

import java.util.List;
import java.util.Optional;

/**
 * OUTBOUND PORT — AdminRepository
 *
 * Defines the persistence contract for the AdminUser aggregate.
 * This interface is implemented by the Infrastructure layer (JPA adapter).
 *
 * ─── DIP Application ──────────────────────────────────────────────────────────
 * The Application layer depends ONLY on this interface, never on the concrete
 * JPA implementation. This allows the domain logic to remain pure and
 * framework-agnostic.
 *
 * ─── Aggregate Persistence ────────────────────────────────────────────────────
 * AdminUser is an aggregate root. All persistence operations go through
 * this repository. Contact info is stored as an embedded value object.
 *
 * ─── CQRS Note ────────────────────────────────────────────────────────────────
 * This repository is for WRITE operations only. Query handlers should use
 * direct JPA projections or read models for list queries.
 *
 * Implementations:
 *   @see IOT.coldchain.infrastructure.persistence.JpaAdminRepository
 */
public interface AdminRepository {

    /**
     * Persists a new or updated AdminUser aggregate.
     *
     * If the admin is new (no ID), the implementation must generate a UUID.
     * If the admin exists, the implementation must merge the changes.
     *
     * @param admin the aggregate to save
     * @return the saved aggregate (may have generated ID)
     */
    AdminUser save(AdminUser admin);

    /**
     * Retrieves an AdminUser by its unique identifier.
     *
     * The implementation must fully reconstitute the aggregate, including
     * contact info value object.
     *
     * @param adminId the unique admin identifier
     * @return Optional containing the admin if found, empty otherwise
     */
    Optional<AdminUser> findById(String adminId);

    /**
     * Retrieves all admins in the system.
     *
     * Used for system administration dashboards.
     *
     * @return list of all admins, empty list if none exist
     */
    List<AdminUser> findAll();

    /**
     * Permanently deletes an admin from the system.
     *
     * @param admin the admin to delete
     */
    void delete(AdminUser admin);

    /**
     * Checks if an admin exists with the given ID.
     *
     * @param adminId the ID to check
     * @return true if an admin exists, false otherwise
     */
    boolean existsById(String adminId);
}
