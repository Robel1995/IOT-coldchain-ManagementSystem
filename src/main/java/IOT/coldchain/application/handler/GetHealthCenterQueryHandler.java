package IOT.coldchain.application.handler;

import IOT.coldchain.application.dto.query.GetAllHealthCentersQuery;
import IOT.coldchain.application.dto.query.GetHealthCenterByIdQuery;
import IOT.coldchain.application.dto.view.HealthCenterDetailView;
import IOT.coldchain.application.dto.view.HealthCenterSummaryView;
import IOT.coldchain.application.dto.view.ShipmentOrderView;
import IOT.coldchain.application.port.in.query.GetHealthCenterQueryUseCase;
import IOT.coldchain.application.port1.out.HealthCenterRepository;
import IOT.coldchain.domain.entity.HealthCenter;
import IOT.coldchain.domain.valueobject.ShipmentOrder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * QUERY HANDLER — GetHealthCenterQueryHandler
 *
 * Handles all read operations for HealthCenter aggregates.
 *
 * ─── CQRS Read Side ───────────────────────────────────────────────────────────
 * This handler is read-only. It returns flat view objects and never mutates
 * domain aggregates.
 *
 * ─── Clean Architecture ───────────────────────────────────────────────────────
 * This handler implements the GetHealthCenterQueryUseCase inbound port and depends
 * on the HealthCenterRepository outbound port. It contains NO framework annotations.
 */
public class GetHealthCenterQueryHandler implements GetHealthCenterQueryUseCase {

    private final HealthCenterRepository healthCenterRepository;

    public GetHealthCenterQueryHandler(HealthCenterRepository healthCenterRepository) {
        this.healthCenterRepository = healthCenterRepository;
    }

    @Override
    public HealthCenterDetailView handle(GetHealthCenterByIdQuery query) {
        HealthCenter center = healthCenterRepository.findById(query.centerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Health center not found with ID: " + query.centerId()));

        List<ShipmentOrderView> pendingOrders = center.getPendingOrders().stream()
                .map(this::mapToShipmentOrderView)
                .collect(Collectors.toList());

        return new HealthCenterDetailView(
                center.getCenterId(),
                center.getCenterName(),
                center.getContactInfo().getEmail(),
                center.getContactInfo().getPhoneNumber(),
                center.getLocation().getLatitude(),
                center.getLocation().getLongitude(),
                pendingOrders
        );
    }

    @Override
    public List<HealthCenterSummaryView> handle(GetAllHealthCentersQuery query) {
        List<HealthCenter> centers = healthCenterRepository.findAll();

        return centers.stream()
                .skip((long) query.page() * query.size())
                .limit(query.size())
                .map(center -> new HealthCenterSummaryView(
                        center.getCenterId(),
                        center.getCenterName(),
                        center.getContactInfo().getEmail(),
                        center.getPendingOrders().size()
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
