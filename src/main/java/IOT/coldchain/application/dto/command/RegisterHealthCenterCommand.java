package IOT.coldchain.application.dto.command;

/**
 * COMMAND — RegisterHealthCenterCommand
 *
 * Represents the user's intent to register a new HealthCenter into the
 * EPSS cold-chain monitoring system.
 *
 * HealthCenters are vaccination facilities that receive vaccine shipments
 * from the cold-chain logistics network.
 *
 * Handled by: RegisterHealthCenterUseCase
 * Domain method called: HealthCenterFactory.createNew(centerName, location, contactInfo)
 *
 * @param centerName   name of the health facility (e.g., "Black Lion Hospital")
 * @param latitude     GPS latitude coordinate
 * @param longitude    GPS longitude coordinate
 * @param email        contact email address for notifications
 * @param phoneNumber  contact phone number for SMS/Telegram notifications
 */
public record RegisterHealthCenterCommand(
        String centerName,
        double latitude,
        double longitude,
        String email,
        String phoneNumber
) {}
