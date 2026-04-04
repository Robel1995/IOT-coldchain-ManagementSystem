package IOT.coldchain.application.port.in.query;

import IOT.coldchain.application.dto.query.GetAllDispatchersQuery;
import IOT.coldchain.application.dto.query.GetDispatcherByIdQuery;
import IOT.coldchain.application.dto.view.DispatcherDetailView;
import IOT.coldchain.application.dto.view.DispatcherSummaryView;

import java.util.List;

/**
 * INBOUND PORT (USE CASE) — GetDispatcherQueryUseCase
 *
 * Defines the system's read capabilities for the VaccineDispatcher aggregate.
 *
 * ─── CQRS Read Side ────────────────────────────────────────────────────────────
 * All handlers on this interface are read-only. They return flat view objects
 * and never load or mutate domain aggregates. No @Transactional required.
 *
 * ─── ISP Application ──────────────────────────────────────────────────────────
 * Dispatcher queries are isolated from driver, truck, and health center queries
 * to maintain clean separation of concerns.
 *
 * Implementations:
 *   @see IOT.coldchain.application.handler.GetDispatcherQueryHandler
 */
public interface GetDispatcherQueryUseCase {

    /**
     * Retrieves the full profile of a single VaccineDispatcher.
     *
     * @param query contains the dispatcherId to retrieve
     * @return a DispatcherDetailView projection including shipment history
     * @throws IllegalArgumentException if no dispatcher exists with the given dispatcherId
     */
    DispatcherDetailView handle(GetDispatcherByIdQuery query);

    /**
     * Retrieves a paginated list of all registered VaccineDispatchers.
     *
     * @param query contains pagination parameters (page, size)
     * @return list of DispatcherSummaryView projections, empty list if none exist
     */
    List<DispatcherSummaryView> handle(GetAllDispatchersQuery query);
}
