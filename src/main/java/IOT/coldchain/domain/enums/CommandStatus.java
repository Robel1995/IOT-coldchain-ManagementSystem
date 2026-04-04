package IOT.coldchain.domain.enums;

/**
 * ENUM — CommandStatus
 *
 * Lifecycle of a DriverCommand from issuance to resolution.
 */
public enum CommandStatus {

    /** Command has been issued by admin, not yet seen by the driver. */
    ISSUED,

    /** Driver has received and acknowledged the command. */
    ACKNOWLEDGED,

    /** Driver has completed the action required by the command. */
    COMPLETED,

    /** Driver has rejected the command with a stated reason. */
    REJECTED
}