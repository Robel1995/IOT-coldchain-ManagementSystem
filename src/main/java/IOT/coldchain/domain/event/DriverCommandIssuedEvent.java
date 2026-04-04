package IOT.coldchain.domain.event;

import IOT.coldchain.domain.enums.CommandType;

import java.time.Instant;

/**
 * DOMAIN EVENT — DriverCommandIssuedEvent
 *
 * Raised when an Admin issues a command to a TruckDriver via the aggregate root.
 * The Application layer routes this to the driver's notification channel
 * (dashboard + SMS depending on urgency).
 *
 * A STOP or EMERGENCY command gets immediate SMS; routine commands go to dashboard.
 */
public final class DriverCommandIssuedEvent implements DomainEvent {

    private final String truckId;
    private final String driverId;
    private final String commandId;
    private final CommandType commandType;
    private final String customMessage;   // null unless commandType == CUSTOM
    private final Instant occurredAt;

    public DriverCommandIssuedEvent(
            String truckId,
            String driverId,
            String commandId,
            CommandType commandType,
            String customMessage,
            Instant occurredAt
    ) {
        this.truckId = truckId;
        this.driverId = driverId;
        this.commandId = commandId;
        this.commandType = commandType;
        this.customMessage = customMessage;
        this.occurredAt = occurredAt;
    }

    @Override public String getAggregateId() { return truckId; }
    @Override public Instant getOccurredAt() { return occurredAt; }

    public String getTruckId()        { return truckId; }
    public String getDriverId()       { return driverId; }
    public String getCommandId()      { return commandId; }
    public CommandType getCommandType() { return commandType; }
    public String getCustomMessage()  { return customMessage; }

    @Override
    public String toString() {
        return "DriverCommandIssuedEvent[truck=" + truckId
                + ", driver=" + driverId + ", type=" + commandType + "]";
    }
}