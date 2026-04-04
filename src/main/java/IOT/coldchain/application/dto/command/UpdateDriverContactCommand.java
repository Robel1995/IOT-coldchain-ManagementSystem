package IOT.coldchain.application.dto.command;

/**
 * COMMAND — UpdateDriverContactCommand
 *
 * Represents the user's intent to update a TruckDriver's contact information.
 *
 * Handled by: RegisterDriverUseCase
 * Domain method called: driver.updateContactInfo(newContactInfo)
 *
 * @param driverId     the driver whose contact info is being updated
 * @param email        new contact email address
 * @param phoneNumber  new contact phone number
 */
public record UpdateDriverContactCommand(
        String driverId,
        String email,
        String phoneNumber
) {}
