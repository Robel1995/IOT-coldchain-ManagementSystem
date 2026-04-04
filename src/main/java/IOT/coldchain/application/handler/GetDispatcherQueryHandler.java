package IOT.coldchain.application.handler;

import IOT.coldchain.application.dto.query.GetAllDispatchersQuery;
import IOT.coldchain.application.dto.query.GetDispatcherByIdQuery;
import IOT.coldchain.application.dto.view.DispatcherDetailView;
import IOT.coldchain.application.dto.view.DispatcherSummaryView;
import IOT.coldchain.application.dto.view.ShipmentOrderView;
import IOT.coldchain.application.port.in.query.GetDispatcherQueryUseCase;
import IOT.coldchain.application.port1.out.DispatcherRepository;
import IOT.coldchain.domain.entity.VaccineDispatcher;
import IOT.coldchain.domain.valueobject.ShipmentOrder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * QUERY HANDLER — GetDispatcherQueryHandler
 *
 * Handles all read operations for VaccineDispatcher aggregates.
 *
 * ─── CQRS Read Side ───────────────────────────────────────────────────────────
 * This handler is read-only. It returns flat view objects and never mutates
 * domain aggregates.
 *
 * ─── Clean Architecture ───────────────────────────────────────────────────────
 * This handler implements the GetDispatcherQueryUseCase inbound port and depends
 * on the DispatcherRepository outbound port. It contains NO framework annotations.
 */
public class GetDispatcherQueryHandler implements GetDispatcherQueryUseCase {

    private final DispatcherRepository dispatcherRepository;

    public GetDispatcherQueryHandler(DispatcherRepository dispatcherRepository) {
        this.dispatcherRepository = dispatcherRepository;
    }

    @Override
    public DispatcherDetailView handle(GetDispatcherByIdQuery query) {
        VaccineDispatcher dispatcher = dispatcherRepository.findById(query.dispatcherId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Dispatcher not found with ID: " + query.dispatcherId()));

        List<ShipmentOrderView> shipmentHistory = dispatcher.getShipmentHistory().stream()
                .map(this::mapToShipmentOrderView)
                .collect(Collectors.toList());

        return new DispatcherDetailView(
                dispatcher.getDispatcherId(),
                dispatcher.getFullName(),
                dispatcher.getContactInfo().getEmail(),
                dispatcher.getContactInfo().getPhoneNumber(),
                shipmentHistory
        );
    }

    @Override
    public List<DispatcherSummaryView> handle(GetAllDispatchersQuery query) {
        List<VaccineDispatcher> dispatchers = dispatcherRepository.findAll();

        return dispatchers.stream()
                .skip((long) query.page() * query.size())
                .limit(query.size())
                .map(dispatcher -> new DispatcherSummaryView(
                        dispatcher.getDispatcherId(),
                        dispatcher.getFullName(),
                        dispatcher.getContactInfo().getEmail(),
                        dispatcher.getShipmentHistory().size()
                ))
                .collect(Collectors.toList());
    }

    private ShipmentOrderView mapToShipmentOrderView(ShipmentOrder order) {
        return new ShipmentOrderView(
                order.getOrderId(),
                order.getDestinationName(),
                order.getScheduledDeliveryDate()
        );
    }
}
