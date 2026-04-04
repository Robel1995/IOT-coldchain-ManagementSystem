package IOT.coldchain.application.handler;

import IOT.coldchain.application.dto.command.RecordShipmentCommand;
import IOT.coldchain.application.dto.command.RegisterDispatcherCommand;
import IOT.coldchain.application.dto.command.UpdateDispatcherContactCommand;
import IOT.coldchain.application.port.in.command.RegisterDispatcherUseCase;
import IOT.coldchain.application.port1.out.DispatcherRepository;
import IOT.coldchain.domain.entity.VaccineDispatcher;
import IOT.coldchain.domain.entity.VaccineDispatcherFactory;
import IOT.coldchain.domain.valueobject.ContactInfo;
import IOT.coldchain.domain.valueobject.ShipmentOrder;

/**
 * COMMAND HANDLER — RegisterDispatcherHandler
 *
 * Handles all operations related to VaccineDispatcher aggregate management:
 * registration, contact updates, and recording shipment orders.
 *
 * ─── Clean Architecture ───────────────────────────────────────────────────────
 * This handler implements the RegisterDispatcherUseCase inbound port and depends
 * on the DispatcherRepository outbound port. It contains NO framework annotations.
 *
 * ─── Error Handling ───────────────────────────────────────────────────────────
 * All exceptions from the domain are propagated unchanged to the caller.
 */
public class RegisterDispatcherHandler implements RegisterDispatcherUseCase {

    private final DispatcherRepository dispatcherRepository;

    public RegisterDispatcherHandler(DispatcherRepository dispatcherRepository) {
        this.dispatcherRepository = dispatcherRepository;
    }

    @Override
    public String handle(RegisterDispatcherCommand command) {
        ContactInfo contactInfo = new ContactInfo(command.email(), command.phoneNumber());

        VaccineDispatcher dispatcher = VaccineDispatcherFactory.createNew(command.fullName(), contactInfo);

        VaccineDispatcher savedDispatcher = dispatcherRepository.save(dispatcher);
        return savedDispatcher.getDispatcherId();
    }

    @Override
    public void handle(UpdateDispatcherContactCommand command) {
        VaccineDispatcher dispatcher = dispatcherRepository.findById(command.dispatcherId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Dispatcher not found with ID: " + command.dispatcherId()));

        ContactInfo newContactInfo = new ContactInfo(command.email(), command.phoneNumber());
        dispatcher.updateContactInfo(newContactInfo);

        dispatcherRepository.save(dispatcher);
    }

    @Override
    public void handle(RecordShipmentCommand command) {
        VaccineDispatcher dispatcher = dispatcherRepository.findById(command.dispatcherId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Dispatcher not found with ID: " + command.dispatcherId()));

        ShipmentOrder order = new ShipmentOrder(command.orderId(), command.destinationHealthCenterId(),command.destinationName(),
                command.scheduledDeliveryDate());
        dispatcher.recordShipment(order);

        dispatcherRepository.save(dispatcher);
    }
}
