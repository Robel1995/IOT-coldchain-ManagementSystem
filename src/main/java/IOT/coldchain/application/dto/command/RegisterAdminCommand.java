package IOT.coldchain.application.dto.command;

/**
 * COMMAND — RegisterAdminCommand
 *
 * Represents the user's intent to register a new AdminUser into the
 * EPSS cold-chain monitoring system.
 *
 * AdminUsers are system operators with privileges to issue commands to drivers,
 * manage truck lifecycles, and configure system settings.
 *
 * Handled by: RegisterAdminUseCase
 * Domain method called: AdminUserFactory.createNew(fullName, contactInfo)
 *
 * @param fullName     admin's full legal name
 * @param email        contact email address
 * @param phoneNumber  contact phone number
 */
public record RegisterAdminCommand(
        String fullName,
        String email,
        String phoneNumber
) {}
