package IOT.coldchain.application.dto.command;

import java.time.LocalDate;

/**
 * COMMAND — RenewDriverLicenseCommand
 *
 * Represents the user's intent to renew a TruckDriver's license with
 * a new license number and expiry date.
 *
 * The handler must validate that the new license is not already expired.
 *
 * Handled by: RegisterDriverUseCase
 * Domain method called: driver.renewLicense(newLicense)
 *
 * @param driverId       the driver whose license is being renewed
 * @param licenseNumber  new license number
 * @param licenseExpiry  new license expiry date
 */
public record RenewDriverLicenseCommand(
        String driverId,
        String licenseNumber,
        LocalDate licenseExpiry
) {}
