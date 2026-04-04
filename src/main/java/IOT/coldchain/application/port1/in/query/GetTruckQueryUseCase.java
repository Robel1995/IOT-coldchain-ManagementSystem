package IOT.coldchain.application.port1.in.query;

import IOT.coldchain.application.dto.query.*;
import IOT.coldchain.application.dto.view.*;

import java.util.List;

/**
 * INBOUND PORT (USE CASE) — GetTruckQueryUseCase
 *
 * Defines the system's read capabilities for the RefrigeratedTruck aggregate
 * and its children (zones, sensors, cargo).
 *
 * ─── CQRS Read Side ────────────────────────────────────────────────────────────
 * Query handlers BYPASS the rich domain model entirely. They do NOT:
 *   - Load a RefrigeratedTruck aggregate via TruckRepository.
 *   - Call any domain methods.
 *   - Use @Transactional.
 *
 * Instead they use the ReadModelRepository outbound port (or a direct JPA
 * projection query) to return flat, serialisation-ready view objects.
 * This makes reads fast, lightweight, and completely independent of the
 * write-side aggregate graph.
 *
 * ─── ISP Application ──────────────────────────────────────────────────────────
 * All truck-related queries are grouped here. Each query method has a single,
 * precisely-typed input record and a precisely-typed return type. No method
 * in this interface is irrelevant to a class implementing it.
 *
 * Implementations:
 *   @see IOT.coldchain.application.handler.GetTruckQueryHandler
 */
public interface GetTruckQueryUseCase {

    /**
     * Retrieves the full real-time detail of a single truck including all zones,
     * sensors (with latest reading), and loaded vaccine boxes.
     *
     * @param query contains the truckId to retrieve
     * @return a TruckDetailView projection, never null
     * @throws IllegalArgumentException if no truck exists with the given truckId
     */
    TruckDetailView handle(GetTruckByIdQuery query);

    /**
     * Retrieves a paginated summary list of all trucks regardless of status.
     *
     * @param query contains pagination parameters (page, size)
     * @return list of TruckSummaryView projections, empty list if none exist
     */
    List<TruckSummaryView> handle(GetAllTrucksQuery query);

    /**
     * Retrieves a list of all trucks currently in SPOILED status.
     * Used by the EPSS emergency dashboard.
     *
     * @param query marker query (no fields)
     * @return list of TruckSummaryView projections for spoiled trucks, empty if none
     */
    List<TruckSummaryView> handle(GetAllSpoiledTrucksQuery query);

    /**
     * Retrieves the full detail of a single compartment zone within a truck.
     *
     * @param query contains truckId and zoneId
     * @return a ZoneDetailView projection including sensors and cargo
     * @throws IllegalArgumentException if truck or zone not found
     */
    ZoneDetailView handle(GetZoneDetailQuery query);

    /**
     * Retrieves the complete chronological temperature reading history
     * for a specific sensor.
     *
     * @param query contains truckId, zoneId, and macAddress
     * @return list of TemperatureReadingView in chronological order (oldest first)
     * @throws IllegalArgumentException if truck, zone, or sensor not found
     */
    List<TemperatureReadingView> handle(GetSensorReadingHistoryQuery query);
}