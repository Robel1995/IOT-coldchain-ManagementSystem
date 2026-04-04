package IOT.coldchain.application.port.in.command;

import IOT.coldchain.application.dto.command.RegisterDriverCommand;
import IOT.coldchain.application.dto.command.RenewDriverLicenseCommand;
import IOT.coldchain.application.dto.command.UpdateDriverContactCommand;

/**
 * INBOUND PORT (USE CASE) — RegisterDriverUseCase
 *
 * Defines the system's capabilities for managing the TruckDriver aggregate:
 * registration, contact information updates, and license renewal.
 *
 * ─── ISP Application ──────────────────────────────────────────────────────────
 * All three operations concern the TruckDriver aggregate exclusively.
 * They are performed by an admin actor (HR/fleet manager) and grouped here
 * because they form a coherent unit of responsibility: driver profile management.
 * This interface has no dependency on RefrigeratedTruck, CompartmentZone,
 * or any other aggregate — it is purely about managing driver data.
 *
 * ─── CQRS Role ────────────────────────────────────────────────────────────────
 * WRITE side. All methods mutate state.
 * Registration returns the generated driverId (String).
 * Update operations return void.
 *
 * Implementations:
 *   @see IOT.coldchain.application.handler.RegisterDriverHandler
 */
public interface RegisterDriverUseCase {

    /**
     * Registers a new TruckDriver aggregate.
     *
     * Handler responsibilities:
     *   1. Build ContactInfo(command.email(), command.phoneNumber()) VO.
     *   2. Build DriversLicense(command.licenseNumber(), command.licenseExpiry()) VO.
     *   3. Call TruckDriverFactory.createNew(command.fullName(), contactInfo, license).
     *   4. Persist via DriverRepository.save(driver).
     *   5. Return the generated driverId.
     *
     * @param command encapsulates fullName, email, phoneNumber, licenseNumber, licenseExpiry
     * @return the generated UUID driverId of the newly registered driver
     * @throws IllegalArgumentException if ContactInfo or DriversLicense construction fails
     * @throws IllegalStateException    if the license is already expired at registration
     */
    String handle(RegisterDriverCommand command);

    /**
     * Updates a TruckDriver's contact information (email and phone number).
     *
     * Handler responsibilities:
     *   1. Load driver via DriverRepository.findById(command.driverId()).
     *   2. Build new ContactInfo(command.email(), command.phoneNumber()) VO.
     *   3. Call driver.updateContactInfo(newContactInfo).
     *   4. Persist via DriverRepository.save(driver).
     *
     * @param command encapsulates driverId, new email, and new phoneNumber
     * @throws IllegalArgumentException if driver not found, or contact format is invalid
     */
    void handle(UpdateDriverContactCommand command);

    /**
     * Renews a TruckDriver's license with a new, non-expired license.
     *
     * Handler responsibilities:
     *   1. Load driver via DriverRepository.findById(command.driverId()).
     *   2. Build new DriversLicense(command.licenseNumber(), command.licenseExpiry()) VO.
     *   3. Call driver.renewLicense(newLicense).
     *   4. Persist via DriverRepository.save(driver).
     *
     * @param command encapsulates driverId, new licenseNumber, and new licenseExpiry
     * @throws IllegalArgumentException if driver not found, or new license is already expired
     */
    void handle(RenewDriverLicenseCommand command);
}