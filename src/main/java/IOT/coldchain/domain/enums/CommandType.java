package IOT.coldchain.domain.enums;

/**
 * ENUM — CommandType
 *
 * Defines the types of operational orders an Admin can issue to a TruckDriver.
 * The domain enforces that a STOP command can only be issued to an IN_TRANSIT truck.
 */
public enum CommandType {

    /** Immediately stop the truck at the nearest safe location. */
    STOP,

    /** Redirect the truck to a different destination. */
    REROUTE,

    /** Return the truck to the dispatch depot. */
    RETURN_TO_BASE,

    /** Instruct the driver to increase the cooling system power. */
    INCREASE_COOLING,

    /** Instruct the driver to reduce the cooling system power. */
    REDUCE_COOLING,

    /**
     * A free-text instruction from the admin.
     * Requires a non-null, non-blank customMessage in DriverCommand.
     */
    CUSTOM
}