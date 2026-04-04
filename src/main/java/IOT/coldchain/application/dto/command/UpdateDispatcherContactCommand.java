package IOT.coldchain.application.dto.command;

/**
 * COMMAND — UpdateDispatcherContactCommand
 *
 * Represents the user's intent to update a VaccineDispatcher's contact information.
 *
 * Handled by: RegisterDispatcherUseCase
 * Domain method called: dispatcher.updateContactInfo(newContactInfo)
 *
 * @param dispatcherId  the dispatcher whose contact info is being updated
 * @param email         new contact email address
 * @param phoneNumber   new contact phone number
 */
public record UpdateDispatcherContactCommand(
        String dispatcherId,
        String email,
        String phoneNumber
) {}
