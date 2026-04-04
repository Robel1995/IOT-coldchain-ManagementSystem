package IOT.coldchain.application.dto.view;

import IOT.coldchain.domain.enums.TruckState;

import java.util.List;

/**
 * READ MODEL — TruckDetailView
 *
 * A comprehensive, flat projection of a single RefrigeratedTruck aggregate.
 * Returned by GetTruckByIdQuery. Contains all zones, each with their sensors
 * and cargo boxes fully embedded as nested view objects.
 *
 * CQRS principle: Queries bypass the domain model and return this flat DTO
 * directly. The handler builds this view either from the JPA entity graph
 * or by mapping from the reconstituted aggregate.
 *
 * @param truckId          unique truck identifier
 * @param status           current lifecycle/health state of the truck
 * @param assignedDriverId ID of the assigned driver, or null if unassigned
 * @param zones            list of all compartment zones with full detail
 * @param pendingCommands  list of all unresolved (ISSUED) driver commands
 */
public record TruckDetailView(
        String truckId,
        TruckState status,
        String assignedDriverId,
        List<ZoneDetailView> zones,
        List<DriverCommandView> pendingCommands
) {}