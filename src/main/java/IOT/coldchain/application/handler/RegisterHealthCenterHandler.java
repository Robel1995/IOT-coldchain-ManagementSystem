package IOT.coldchain.application.handler;

import IOT.coldchain.application.dto.command.AddPendingOrderCommand;
import IOT.coldchain.application.dto.command.ConfirmOrderDeliveredCommand;
import IOT.coldchain.application.dto.command.RegisterHealthCenterCommand;
import IOT.coldchain.application.dto.command.UpdateHealthCenterContactCommand;
import IOT.coldchain.application.port.in.command.RegisterHealthCenterUseCase;
import IOT.coldchain.application.port1.out.HealthCenterRepository;
import IOT.coldchain.domain.entity.HealthCenter;
import IOT.coldchain.domain.entity.HealthCenterFactory;
import IOT.coldchain.domain.valueobject.ContactInfo;
import IOT.coldchain.domain.valueobject.GpsCoordinate;
import IOT.coldchain.domain.valueobject.ShipmentOrder;

import java.time.LocalDate;

import static java.time.LocalDate.*;

/**
 * COMMAND HANDLER — RegisterHealthCenterHandler
 *
 * Handles all operations related to HealthCenter aggregate management:
 * registration, contact updates, and pending order management.
 *
 * ─── Clean Architecture ───────────────────────────────────────────────────────
 * This handler implements the RegisterHealthCenterUseCase inbound port and depends
 * on the HealthCenterRepository outbound port. It contains NO framework annotations.
 *
 * ─── Error Handling ───────────────────────────────────────────────────────────
 * All exceptions from the domain are propagated unchanged to the caller.
 */
public class RegisterHealthCenterHandler implements RegisterHealthCenterUseCase {

    private final HealthCenterRepository healthCenterRepository;

    public RegisterHealthCenterHandler(HealthCenterRepository healthCenterRepository) {
        this.healthCenterRepository = healthCenterRepository;
    }

    @Override
    public String handle(RegisterHealthCenterCommand command) {
        GpsCoordinate location = new GpsCoordinate(command.latitude(), command.longitude());
        ContactInfo contactInfo = new ContactInfo(command.email(), command.phoneNumber());

        HealthCenter center = HealthCenterFactory.createNew(command.centerName(), location, contactInfo);

        HealthCenter savedCenter = healthCenterRepository.save(center);
        return savedCenter.getCenterId();
    }

    @Override
    public void handle(UpdateHealthCenterContactCommand command) {
        HealthCenter center = healthCenterRepository.findById(command.centerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Health center not found with ID: " + command.centerId()));

        ContactInfo newContactInfo = new ContactInfo(command.email(), command.phoneNumber());
        center.updateContactInfo(newContactInfo);

        healthCenterRepository.save(center);
    }

    @Override
    public void handle(AddPendingOrderCommand command) {
        HealthCenter center = healthCenterRepository.findById(command.centerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Health center not found with ID: " + command.centerId()));

        String destinationId = command.destination(); // or extract from format "ID|Name"
        String destinationName = command.destination(); // or extract from format "ID|Name"

        ShipmentOrder order = new ShipmentOrder(command.orderId(), destinationId, destinationName, command.scheduledDate());
        center.addPendingOrder(order);

        healthCenterRepository.save(center);
    }

    @Override
    public void handle(ConfirmOrderDeliveredCommand command) {
        HealthCenter center = healthCenterRepository.findById(command.centerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Health center not found with ID: " + command.centerId()));

        center.confirmOrderDelivered(command.orderId());

        healthCenterRepository.save(center);
    }
}
