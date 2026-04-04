package IOT.coldchain.application.port.in.query;

import IOT.coldchain.application.dto.query.GetAllDriversQuery;
import IOT.coldchain.application.dto.query.GetDriverByIdQuery;
import IOT.coldchain.application.dto.query.GetDriverCommandHistoryQuery;
import IOT.coldchain.application.dto.view.DriverCommandView;
import IOT.coldchain.application.dto.view.DriverDetailView;
import IOT.coldchain.application.dto.view.DriverSummaryView;

import java.util.List;

/**
 * INBOUND PORT (USE CASE) — GetDriverQueryUseCase
 *
 * Defines the system's read capabilities for the TruckDriver aggregate
 * and the DriverCommand child entities within a truck.
 *
 * ─── CQRS Read Side ────────────────────────────────────────────────────────────
 * All handlers on this interface are read-only. They return flat view objects
 * and never load or mutate domain aggregates. No @Transactional required.
 *
 * ─── ISP Application ──────────────────────────────────────────────────────────
 * GetDriverCommandHistoryQuery is included here (not in GetTruckQueryUseCase)
 * because command history is primarily a driver-centric view — "what commands
 * has this truck's driver received?" — and the primary actor querying it is an
 * admin managing the driver, not the truck.
 *
 * Implementations:
 *   @see IOT.coldchain.application.handler.GetDriverQueryHandler
 */
public interface GetDriverQueryUseCase {

    /**
     * Retrieves the full profile of a single TruckDriver.
     *
     * @param query contains the driverId to retrieve
     * @return a DriverDetailView projection including license expiry status
     * @throws IllegalArgumentException if no driver exists with the given driverId
     */
    DriverDetailView handle(GetDriverByIdQuery query);

    /**
     * Retrieves a paginated list of all registered TruckDrivers.
     *
     * @param query contains pagination parameters (page, size)
     * @return list of DriverSummaryView projections, empty list if none exist
     */
    List<DriverSummaryView> handle(GetAllDriversQuery query);

    /**
     * Retrieves the full admin-command history for a specific truck.
     *
     * Returns all DriverCommand records (ISSUED, ACKNOWLEDGED, COMPLETED, REJECTED)
     * in chronological order (most recent first).
     *
     * @param query contains the truckId whose command history is requested
     * @return list of DriverCommandView projections, empty list if no commands issued
     * @throws IllegalArgumentException if no truck exists with the given truckId
     */
    List<DriverCommandView> handle(GetDriverCommandHistoryQuery query);
}