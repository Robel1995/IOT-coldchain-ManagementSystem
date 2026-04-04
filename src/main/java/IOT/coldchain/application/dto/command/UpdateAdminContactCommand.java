package IOT.coldchain.application.dto.command;

/**
 * COMMAND — UpdateAdminContactCommand
 *
 * Represents the user's intent to update an AdminUser's contact information.
 *
 * Handled by: RegisterAdminUseCase
 * Domain method called: admin.updateContactInfo(newContactInfo)
 *
 * @param adminId      the admin whose contact info is being updated
 * @param email        new contact email address
 * @param phoneNumber  new contact phone number
 */
public record UpdateAdminContactCommand(
        String adminId,
        String email,
        String phoneNumber
) {}
