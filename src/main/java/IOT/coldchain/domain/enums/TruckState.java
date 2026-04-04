package IOT.coldchain.domain.enums;

/**
 * ENUM — TruckStatus
 *
 * Represents the overall lifecycle/health status of a RefrigeratedTruck.
 * This is a DERIVED status: the truck is SPOILED if ANY of its zones is SPOILED.
 *
 * State transitions:
 *   REGISTERED → IN_TRANSIT → SAFE (normal operation) → SPOILED (if breach)
 *   SPOILED → SCRAPPED (only state that allows deletion)
 */
public enum TruckState {

    /**
     * Truck has been registered in the system but has not yet started a journey.
     */
    REGISTERED,

    /**
     * Truck is currently on the road delivering vaccines.
     * All active monitoring rules apply.
     */
    IN_TRANSIT,

    /**
     * All zones are within their safe temperature thresholds.
     * Normal operating state during transit.
     */
    SAFE,

    /**
     * At least one zone has been spoiled (30+ minutes of continuous breach).
     * No new cargo can be loaded. Delivery from this truck is BLOCKED.
     * Only trucks in this state can be scrapped/deleted.
     */
    SPOILED,

    /**
     * Truck has been permanently decommissioned.
     * Terminal state — no further operations possible.
     */
    SCRAPPED
}