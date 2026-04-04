package IOT.coldchain.application.port1.out;

import IOT.coldchain.domain.entity.RefrigeratedTruck;

import java.util.List;
import java.util.Optional;

/**
 * OUTBOUND PORT — TruckRepository
 *
 * Defines the persistence contract for the RefrigeratedTruck aggregate.
 * This interface is implemented by the Infrastructure layer (JPA adapter).
 *
 * ─── DIP Application ──────────────────────────────────────────────────────────
 * The Application layer depends ONLY on this interface, never on the concrete
 * JPA implementation. This allows the domain logic to remain pure and
 * framework-agnostic. The Infrastructure layer adapts to the domain, not vice versa.
 *
 * ─── Aggregate Persistence ────────────────────────────────────────────────────
 * RefrigeratedTruck is an aggregate root. All persistence operations go through
 * this repository. Child entities (zones, sensors, commands) are persisted
 * via aggregate cascading — they have no separate repositories.
 *
 * ─── CQRS Note ────────────────────────────────────────────────────────────────
 * This repository is for WRITE operations only. Query handlers should use
 * direct JPA projections or read models, not load full aggregates.
 *
 * Implementations:
 *   @see IOT.coldchain.infrastructure.persistence.JpaTruckRepository
 */
public interface TruckRepository {

    /**
     * Persists a new or updated RefrigeratedTruck aggregate.
     *
     * If the truck is new (no ID), the implementation must generate a UUID.
     * If the truck exists, the implementation must merge the changes.
     *
     * @param truck the aggregate to save
     * @return the saved aggregate (may have generated ID)
     */
    RefrigeratedTruck save(RefrigeratedTruck truck);

    /**
     * Retrieves a RefrigeratedTruck by its unique identifier.
     *
     * The implementation must fully reconstitute the aggregate, including all
     * zones, sensors, vaccine boxes, and driver commands.
     *
     * @param truckId the unique truck identifier
     * @return Optional containing the truck if found, empty otherwise
     */
    Optional<RefrigeratedTruck> findById(String truckId);

    /**
     * Retrieves all trucks in the system.
     *
     * Used for administrative dashboards. Consider pagination for large fleets.
     *
     * @return list of all trucks, empty list if none exist
     */
    List<RefrigeratedTruck> findAll();

    /**
     * Retrieves all trucks currently in SPOILED status.
     *
     * Used by the EPSS dashboard to display compromised shipments.
     *
     * @return list of spoiled trucks, empty list if none
     */
    List<RefrigeratedTruck> findAllSpoiled();

    /**
     * Permanently deletes a truck from the system.
     *
     * This should only be called after the truck has been transitioned to
     * SCRAPPED status and all domain events have been dispatched.
     *
     * @param truck the truck to delete
     */
    void delete(RefrigeratedTruck truck);

    /**
     * Checks if a truck exists with the given ID.
     *
     * @param truckId the ID to check
     * @return true if a truck exists, false otherwise
     */
    boolean existsById(String truckId);
}
