package IOT.coldchain.application.dto.view;

import IOT.coldchain.domain.enums.CommandStatus;
import IOT.coldchain.domain.enums.CommandType;

import java.time.Instant;

/**
 * READ MODEL — DriverCommandView
 *
 * A flat projection of a DriverCommand entity.
 * Returned in TruckDetailView (for pending commands) and by
 * GetDriverCommandHistoryQuery (for the full history).
 *
 * @param commandId       unique command identifier
 * @param type            the type of order issued (STOP, REROUTE, etc.)
 * @param status          current lifecycle state of this command
 * @param issuedByAdminId ID of the AdminUser who issued the command
 * @param customMessage   free-text message, non-null only for CUSTOM type
 * @param issuedAt        when the admin issued the command
 * @param acknowledgedAt  when the driver responded, or null if still pending
 * @param rejectionReason driver's rejection reason, or null if not rejected
 */
public record DriverCommandView(
        String commandId,
        CommandType type,
        CommandStatus status,
        String issuedByAdminId,
        String customMessage,
        Instant issuedAt,
        Instant acknowledgedAt,
        String rejectionReason
) {}