package IOT.coldchain.domain.entity;

import IOT.coldchain.domain.enums.CommandStatus;
import IOT.coldchain.domain.enums.CommandType;

import java.time.Instant;
import java.util.Objects;

/**
 * ENTITY — DriverCommand (Child Entity of RefrigeratedTruck)
 *
 * Represents an operational order issued by an Admin to the TruckDriver.
 * The command is attached to a specific truck (via the aggregate root).
 *
 * Identity: commandId — unique per command issued.
 *
 * Lifecycle: ISSUED → ACKNOWLEDGED → COMPLETED | REJECTED
 *
 * Business rules enforced here:
 *  - A CUSTOM command must have a non-blank customMessage.
 *  - A command can only be acknowledged/completed/rejected once.
 *  - Transition from COMPLETED or REJECTED is not allowed.
 */
public class DriverCommand {

    private final String commandId;
    private final String issuedByAdminId;
    private final CommandType type;
    private final String customMessage;   // only meaningful when type == CUSTOM
    private final Instant issuedAt;

    private CommandStatus status;
    private Instant acknowledgedAt;
    private Instant resolvedAt;
    private String rejectionReason;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR — package-private
    // -------------------------------------------------------------------------

    DriverCommand(String commandId, String issuedByAdminId, CommandType type, String customMessage) {
        if (commandId == null || commandId.isBlank())
            throw new IllegalArgumentException("commandId cannot be blank.");
        Objects.requireNonNull(issuedByAdminId, "issuedByAdminId cannot be null.");
        Objects.requireNonNull(type, "CommandType cannot be null.");

        if (type == CommandType.CUSTOM && (customMessage == null || customMessage.isBlank())) {
            throw new IllegalArgumentException(
                    "A CUSTOM command must have a non-blank customMessage."
            );
        }

        this.commandId = commandId.trim();
        this.issuedByAdminId = issuedByAdminId.trim();
        this.type = type;
        this.customMessage = (customMessage != null) ? customMessage.trim() : null;
        this.issuedAt = Instant.now();
        this.status = CommandStatus.ISSUED;
    }

    // -------------------------------------------------------------------------
    // DOMAIN BEHAVIOUR
    // -------------------------------------------------------------------------

    void acknowledge() {
        assertNotTerminal();
        if (this.status != CommandStatus.ISSUED)
            throw new IllegalStateException("Command can only be acknowledged from ISSUED state.");
        this.status = CommandStatus.ACKNOWLEDGED;
        this.acknowledgedAt = Instant.now();
    }

    void complete() {
        assertNotTerminal();
        if (this.status != CommandStatus.ACKNOWLEDGED)
            throw new IllegalStateException("Command must be ACKNOWLEDGED before COMPLETED.");
        this.status = CommandStatus.COMPLETED;
        this.resolvedAt = Instant.now();
    }

    void reject(String reason) {
        assertNotTerminal();
        if (reason == null || reason.isBlank())
            throw new IllegalArgumentException("Rejection reason cannot be blank.");
        this.status = CommandStatus.REJECTED;
        this.rejectionReason = reason.trim();
        this.resolvedAt = Instant.now();
    }

    private void assertNotTerminal() {
        if (status == CommandStatus.COMPLETED || status == CommandStatus.REJECTED) {
            throw new IllegalStateException(
                    "Command " + commandId + " is already in terminal state: " + status
            );
        }
    }

    // -------------------------------------------------------------------------
    // RECONSTITUTION — for Infrastructure adapter ONLY
    // -------------------------------------------------------------------------

    public static DriverCommand reconstitute(
            String commandId, String issuedByAdminId, CommandType type, String customMessage,
            Instant issuedAt, CommandStatus status, Instant acknowledgedAt,
            Instant resolvedAt, String rejectionReason
    ) {
        DriverCommand cmd = new DriverCommand(commandId, issuedByAdminId, type, customMessage);
        // Override fields set in constructor with persisted state
        cmd.status = status;
        cmd.acknowledgedAt = acknowledgedAt;
        cmd.resolvedAt = resolvedAt;
        cmd.rejectionReason = rejectionReason;
        return cmd;
    }

    // -------------------------------------------------------------------------
    // ENTITY CONTRACT
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DriverCommand)) return false;
        return Objects.equals(commandId, ((DriverCommand) o).commandId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandId);
    }

    @Override
    public String toString() {
        return "DriverCommand[id=" + commandId + ", type=" + type + ", status=" + status + "]";
    }

    // -------------------------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------------------------

    public String getCommandId()          { return commandId; }
    public String getIssuedByAdminId()    { return issuedByAdminId; }
    public CommandType getType()          { return type; }
    public String getCustomMessage()      { return customMessage; }
    public Instant getIssuedAt()          { return issuedAt; }
    public CommandStatus getStatus()      { return status; }
    public Instant getAcknowledgedAt()    { return acknowledgedAt; }
    public Instant getResolvedAt()        { return resolvedAt; }
    public String getRejectionReason()    { return rejectionReason; }
}