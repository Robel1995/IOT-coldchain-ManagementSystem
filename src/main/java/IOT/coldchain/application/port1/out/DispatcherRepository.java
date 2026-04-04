package IOT.coldchain.application.port1.out;

import IOT.coldchain.domain.entity.VaccineDispatcher;

import java.util.List;
import java.util.Optional;

/**
 * OUTBOUND PORT — DispatcherRepository
 *
 * Defines the persistence contract for the VaccineDispatcher aggregate.
 * This interface is implemented by the Infrastructure layer (JPA adapter).
 *
 * ─── DIP Application ──────────────────────────────────────────────────────────
 * The Application layer depends ONLY on this interface, never on the concrete
 * JPA implementation. This allows the domain logic to remain pure and
 * framework-agnostic.
 *
 * ─── Aggregate Persistence ────────────────────────────────────────────────────
 * VaccineDispatcher is an aggregate root. All persistence operations go through
 * this repository. The shipment history is stored as a collection of value objects
 * within the aggregate.
 *
 * ─── CQRS Note ────────────────────────────────────────────────────────────────
 * This repository is for WRITE operations only. Query handlers should use
 * direct JPA projections or read models for list queries.
 *
 * Implementations:
 *   @see IOT.coldchain.infrastructure.persistence.JpaDispatcherRepository
 */
public interface DispatcherRepository {

    /**
     * Persists a new or updated VaccineDispatcher aggregate.
     *
     * If the dispatcher is new (no ID), the implementation must generate a UUID.
     * If the dispatcher exists, the implementation must merge the changes.
     *
     * @param dispatcher the aggregate to save
     * @return the saved aggregate (may have generated ID)
     */
    VaccineDispatcher save(VaccineDispatcher dispatcher);

    /**
     * Retrieves a VaccineDispatcher by its unique identifier.
     *
     * The implementation must fully reconstitute the aggregate, including
     * contact info and shipment history.
     *
     * @param dispatcherId the unique dispatcher identifier
     * @return Optional containing the dispatcher if found, empty otherwise
     */
    Optional<VaccineDispatcher> findById(String dispatcherId);

    /**
     * Retrieves all dispatchers in the system.
     *
     * Used for administrative dashboards. Consider pagination for large staffs.
     *
     * @return list of all dispatchers, empty list if none exist
     */
    List<VaccineDispatcher> findAll();

    /**
     * Permanently deletes a dispatcher from the system.
     *
     * @param dispatcher the dispatcher to delete
     */
    void delete(VaccineDispatcher dispatcher);

    /**
     * Checks if a dispatcher exists with the given ID.
     *
     * @param dispatcherId the ID to check
     * @return true if a dispatcher exists, false otherwise
     */
    boolean existsById(String dispatcherId);
}
