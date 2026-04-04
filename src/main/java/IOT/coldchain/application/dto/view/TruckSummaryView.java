package IOT.coldchain.application.dto.view;

import IOT.coldchain.domain.enums.TruckState;

/**
 * READ MODEL — TruckSummaryView
 *
 * A flat, read-optimised projection of a RefrigeratedTruck aggregate for use
 * in list-based query responses (e.g., GetAllTrucksQuery, GetAllSpoiledTrucksQuery).
 *
 * CQRS principle: This object is never fed back into the domain. It is a
 * one-way, read-only snapshot of state, optimised for fast serialisation.
 * The handler may construct this directly from a JPA projection or by mapping
 * from the aggregate — the query side does not need the full aggregate graph.
 *
 * @param truckId          unique truck identifier
 * @param status           current TruckState (REGISTERED, IN_TRANSIT, SAFE, SPOILED, SCRAPPED)
 * @param totalZones       number of compartment zones configured on this truck
 * @param totalCargo       total number of vaccine boxes currently loaded across all zones
 * @param assignedDriverId ID of the assigned driver, or null if none assigned
 */
public record TruckSummaryView(
        String truckId,
        TruckState status,
        int totalZones,
        int totalCargo,
        String assignedDriverId
) {}