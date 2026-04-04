package IOT.coldchain.application.dto.command;

/**
 * COMMAND — RegisterDispatcherCommand
 *
 * Represents the user's intent to register a new VaccineDispatcher into the
 * EPSS cold-chain monitoring system.
 *
 * VaccineDispatchers are EPSS staff members responsible for loading vaccine
 * boxes into trucks and recording shipment orders.
 *
 * Handled by: RegisterDispatcherUseCase
 * Domain method called: VaccineDispatcherFactory.createNew(fullName, contactInfo)
 *
 * @param fullName     dispatcher's full legal name
 * @param email        contact email address
 * @param phoneNumber  contact phone number
 */
public record RegisterDispatcherCommand(
        String fullName,
        String email,
        String phoneNumber
) {}
