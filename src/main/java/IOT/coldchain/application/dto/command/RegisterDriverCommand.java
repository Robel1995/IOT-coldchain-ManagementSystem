package IOT.coldchain.application.dto.command;

import java.time.LocalDate;

/**
 * COMMAND — RegisterDriverCommand
 *
 * Represents the user's intent to register a new TruckDriver into the
 * EPSS cold-chain monitoring system.
 *
 * The handler must validate that the license is not already expired
 * at the time of registration (business rule).
 *
 * Handled by: RegisterDriverUseCase
 * Domain method called: TruckDriverFactory.createNew(fullName, contactInfo, license)
 *
 * @param fullName       driver's full legal name
 * @param email          contact email address
 * @param phoneNumber    contact phone number
 * @param licenseNumber  driver's license number
 * @param licenseExpiry  license expiry date
 */
public record RegisterDriverCommand(
        String fullName,
        String email,
        String phoneNumber,
        String licenseNumber,
        LocalDate licenseExpiry
) {}
