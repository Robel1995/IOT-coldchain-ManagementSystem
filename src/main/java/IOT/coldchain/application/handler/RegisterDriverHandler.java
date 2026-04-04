package IOT.coldchain.application.handler;

import IOT.coldchain.application.dto.command.RegisterDriverCommand;
import IOT.coldchain.application.dto.command.RenewDriverLicenseCommand;
import IOT.coldchain.application.dto.command.UpdateDriverContactCommand;
import IOT.coldchain.application.port.in.command.RegisterDriverUseCase;
import IOT.coldchain.application.port1.out.DriverRepository;
import IOT.coldchain.domain.entity.TruckDriver;
import IOT.coldchain.domain.entity.TruckDriverFactory;
import IOT.coldchain.domain.valueobject.ContactInfo;
import IOT.coldchain.domain.valueobject.DriversLicense;

/**
 * COMMAND HANDLER — RegisterDriverHandler
 *
 * Handles all operations related to TruckDriver aggregate management:
 * registration, contact updates, and license renewal.
 *
 * ─── Clean Architecture ───────────────────────────────────────────────────────
 * This handler implements the RegisterDriverUseCase inbound port and depends
 * on the DriverRepository outbound port. It contains NO framework annotations
 * (no @Service, no @Transactional) — those belong in the infrastructure layer.
 *
 * ─── Error Handling ───────────────────────────────────────────────────────────
 * All exceptions from the domain (IllegalArgumentException, IllegalStateException)
 * are propagated unchanged to the caller (API layer). The API layer translates
 * these to appropriate HTTP status codes.
 *
 * ─── ID Generation ────────────────────────────────────────────────────────────
 * The domain factory generates UUIDs. The handler passes the saved aggregate's
 * ID back to the caller.
 */
public class RegisterDriverHandler implements RegisterDriverUseCase {

    private final DriverRepository driverRepository;

    public RegisterDriverHandler(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public String handle(RegisterDriverCommand command) {
        ContactInfo contactInfo = new ContactInfo(command.email(), command.phoneNumber());
        DriversLicense license = new DriversLicense(command.licenseNumber(), command.licenseExpiry());

        TruckDriver driver = TruckDriverFactory.createNew(command.fullName(), contactInfo, license);

        TruckDriver savedDriver = driverRepository.save(driver);
        return savedDriver.getDriverId();
    }

    @Override
    public void handle(UpdateDriverContactCommand command) {
        TruckDriver driver = driverRepository.findById(command.driverId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Driver not found with ID: " + command.driverId()));

        ContactInfo newContactInfo = new ContactInfo(command.email(), command.phoneNumber());
        driver.updateContactInfo(newContactInfo);

        driverRepository.save(driver);
    }

    @Override
    public void handle(RenewDriverLicenseCommand command) {
        TruckDriver driver = driverRepository.findById(command.driverId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Driver not found with ID: " + command.driverId()));

        DriversLicense newLicense = new DriversLicense(command.licenseNumber(), command.licenseExpiry());
        driver.renewLicense(newLicense);

        driverRepository.save(driver);
    }
}
