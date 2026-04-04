package IOT.coldchain.application.port1.out;

import IOT.coldchain.domain.entity.HealthCenter;

import java.util.List;
import java.util.Optional;

/**
 * OUTBOUND PORT — HealthCenterRepository
 *
 * Defines the persistence contract for the HealthCenter aggregate.
 * This interface is implemented by the Infrastructure layer (JPA adapter).
 *
 * ─── DIP Application ──────────────────────────────────────────────────────────
 * The Application layer depends ONLY on this interface, never on the concrete
 * JPA implementation. This allows the domain logic to remain pure and
 * framework-agnostic.
 *
 * ─── Aggregate Persistence ────────────────────────────────────────────────────
 * HealthCenter is an aggregate root. All persistence operations go through
 * this repository. The GPS coordinates and pending orders are stored as value
 * objects within the aggregate.
 *
 * ─── CQRS Note ────────────────────────────────────────────────────────────────
 * This repository is for WRITE operations only. Query handlers should use
 * direct JPA projections or read models for list queries.
 *
 * Implementations:
 *   @see IOT.coldchain.infrastructure.persistence.JpaHealthCenterRepository
 */
public interface HealthCenterRepository {

    /**
     * Persists a new or updated HealthCenter aggregate.
     *
     * If the center is new (no ID), the implementation must generate a UUID.
     * If the center exists, the implementation must merge the changes.
     *
     * @param center the aggregate to save
     * @return the saved aggregate (may have generated ID)
     */
    HealthCenter save(HealthCenter center);

    /**
     * Retrieves a HealthCenter by its unique identifier.
     *
     * The implementation must fully reconstitute the aggregate, including
     * GPS coordinates, contact info, and pending orders.
     *
     * @param centerId the unique center identifier
     * @return Optional containing the center if found, empty otherwise
     */
    Optional<HealthCenter> findById(String centerId);

    /**
     * Retrieves all health centers in the system.
     *
     * Used for administrative dashboards. Consider pagination for large networks.
     *
     * @return list of all health centers, empty list if none exist
     */
    List<HealthCenter> findAll();

    /**
     * Finds health centers that have a specific order ID in their pending list.
     *
     * Used by the notification system to find which centers to alert when
     * a shipment is compromised.
     *
     * @param orderId the order ID to search for
     * @return list of health centers with this pending order
     */
    List<HealthCenter> findByPendingOrderId(String orderId);

    /**
     * Permanently deletes a health center from the system.
     *
     * @param center the health center to delete
     */
    void delete(HealthCenter center);

    /**
     * Checks if a health center exists with the given ID.
     *
     * @param centerId the ID to check
     * @return true if a health center exists, false otherwise
     */
    boolean existsById(String centerId);
}
