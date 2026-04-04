package IOT.coldchain.application.port.in.query;

import IOT.coldchain.application.dto.query.GetAllHealthCentersQuery;
import IOT.coldchain.application.dto.query.GetHealthCenterByIdQuery;
import IOT.coldchain.application.dto.view.HealthCenterDetailView;
import IOT.coldchain.application.dto.view.HealthCenterSummaryView;

import java.util.List;

/**
 * INBOUND PORT (USE CASE) — GetHealthCenterQueryUseCase
 *
 * Defines the system's read capabilities for the HealthCenter aggregate.
 *
 * ─── CQRS Read Side ────────────────────────────────────────────────────────────
 * All handlers on this interface are read-only. They return flat view objects
 * and never load or mutate domain aggregates. No @Transactional required.
 *
 * ─── ISP Application ──────────────────────────────────────────────────────────
 * Health center queries are isolated from driver, truck, and dispatcher queries
 * to maintain clean separation of concerns.
 *
 * Implementations:
 *   @see IOT.coldchain.application.handler.GetHealthCenterQueryHandler
 */
public interface GetHealthCenterQueryUseCase {

    /**
     * Retrieves the full profile of a single HealthCenter.
     *
     * @param query contains the centerId to retrieve
     * @return a HealthCenterDetailView projection including pending orders
     * @throws IllegalArgumentException if no health center exists with the given centerId
     */
    HealthCenterDetailView handle(GetHealthCenterByIdQuery query);

    /**
     * Retrieves a paginated list of all registered HealthCenters.
     *
     * @param query contains pagination parameters (page, size)
     * @return list of HealthCenterSummaryView projections, empty list if none exist
     */
    List<HealthCenterSummaryView> handle(GetAllHealthCentersQuery query);
}
