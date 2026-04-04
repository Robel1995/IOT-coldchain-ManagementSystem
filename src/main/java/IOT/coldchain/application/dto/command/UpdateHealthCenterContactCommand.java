package IOT.coldchain.application.dto.command;

/**
 * COMMAND — UpdateHealthCenterContactCommand
 *
 * Represents the user's intent to update a HealthCenter's contact information.
 *
 * Handled by: RegisterHealthCenterUseCase
 * Domain method called: healthCenter.updateContactInfo(newContactInfo)
 *
 * @param centerId     the health center whose contact info is being updated
 * @param email        new contact email address
 * @param phoneNumber  new contact phone number
 */
public record UpdateHealthCenterContactCommand(
        String centerId,
        String email,
        String phoneNumber
) {}
