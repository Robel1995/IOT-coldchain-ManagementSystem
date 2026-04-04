package IOT.coldchain.application.port1.out;

import IOT.coldchain.domain.entity.TruckDriver;

import java.util.List;
import java.util.Optional;

/**
 * OUTBOUND PORT — DriverRepository
 *
 * Defines the persistence contract for the TruckDriver aggregate.
 * This interface is implemented by the Infrastructure layer (JPA adapter).
 *
 * ─── DIP Application ──────────────────────────────────────────────────────────
 * The Application layer depends ONLY on this interface, never on the concrete
 * JPA implementation. This allows the domain logic to remain pure and
 * framework-agnostic.
 *
 * ─── Aggregate Persistence ────────────────────────────────────────────────────
 * TruckDriver is an aggregate root. All persistence operations go through
 * this repository. The driver's license and contact info are value objects
 * embedded within the aggregate.
 *
 * ─── CQRS Note ────────────────────────────────────────────────────────────────
 * This repository is for WRITE operations only. Query handlers should use
 * direct JPA projections or read models for list queries.
 *
 * Implementations:
 *   @see IOT.coldchain.infrastructure.persistence.JpaDriverRepository
 */
public interface DriverRepository {

    /**
     * Persists a new or updated TruckDriver aggregate.
     *
     * If the driver is new (no ID), the implementation must generate a UUID.
     * If the driver exists, the implementation must merge the changes.
     *
     * @param driver the aggregate to save
     * @return the saved aggregate (may have generated ID)
     */
    TruckDriver save(TruckDriver driver);

    /**
     * Retrieves a TruckDriver by its unique identifier.
     *
     * The implementation must fully reconstitute the aggregate, including
     * contact info and license value objects.
     *
     * @param driverId the unique driver identifier
     * @return Optional containing the driver if found, empty otherwise
     */
    Optional<TruckDriver> findById(String driverId);

    /**
     * Retrieves all drivers in the system.
     *
     * Used for administrative dashboards. Consider pagination for large fleets.
     *
     * @return list of all drivers, empty list if none exist
     */
    List<TruckDriver> findAll();

    /**
     * Permanently deletes a driver from the system.
     *
     * Note: The domain may enforce business rules about when a driver can be
     * deleted (e.g., not assigned to any active trucks).
     *
     * @param driver the driver to delete
     */
    void delete(TruckDriver driver);

    /**
     * Checks if a driver exists with the given ID.
     *
     * @param driverId the ID to check
     * @return true if a driver exists, false otherwise
     */
    boolean existsById(String driverId);
}
