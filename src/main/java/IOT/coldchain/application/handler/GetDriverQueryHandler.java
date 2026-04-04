package IOT.coldchain.application.handler;

import IOT.coldchain.application.dto.query.GetAllDriversQuery;
import IOT.coldchain.application.dto.query.GetDriverByIdQuery;
import IOT.coldchain.application.dto.query.GetDriverCommandHistoryQuery;
import IOT.coldchain.application.dto.view.DriverCommandView;
import IOT.coldchain.application.dto.view.DriverDetailView;
import IOT.coldchain.application.dto.view.DriverSummaryView;
import IOT.coldchain.application.port.in.query.GetDriverQueryUseCase;
import IOT.coldchain.application.port1.out.DriverRepository;
import IOT.coldchain.application.port1.out.TruckRepository;
import IOT.coldchain.domain.entity.TruckDriver;
import IOT.coldchain.domain.entity.DriverCommand;
import IOT.coldchain.domain.entity.RefrigeratedTruck;

import java.util.List;
import java.util.stream.Collectors;

/**
 * QUERY HANDLER — GetDriverQueryHandler
 *
 * Handles all read operations for TruckDriver aggregates and driver command history.
 *
 * ─── CQRS Read Side ───────────────────────────────────────────────────────────
 * This handler is read-only. It returns flat view objects and never mutates
 * domain aggregates. No transaction management is required for queries.
 *
 * ─── Clean Architecture ───────────────────────────────────────────────────────
 * This handler implements the GetDriverQueryUseCase inbound port and depends
 * on the DriverRepository and TruckRepository outbound ports. It contains NO
 * framework annotations.
 *
 * ─── View Mapping ─────────────────────────────────────────────────────────────
 * The handler maps domain aggregates to flat DTOs. This decouples the read model
 * from the domain model, allowing optimization for query performance.
 */
public class GetDriverQueryHandler implements GetDriverQueryUseCase {

    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;

    public GetDriverQueryHandler(DriverRepository driverRepository, TruckRepository truckRepository) {
        this.driverRepository = driverRepository;
        this.truckRepository = truckRepository;
    }

    @Override
    public DriverDetailView handle(GetDriverByIdQuery query) {
        TruckDriver driver = driverRepository.findById(query.driverId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Driver not found with ID: " + query.driverId()));

        return new DriverDetailView(
                driver.getDriverId(),
                driver.getFullName(),
                driver.getContactInfo().getEmail(),
                driver.getContactInfo().getPhoneNumber(),
                driver.getLicense().getLicenseNumber(),
                driver.getLicense().getExpiryDate(),
                driver.getLicense().isExpired()
        );
    }

    @Override
    public List<DriverSummaryView> handle(GetAllDriversQuery query) {
        List<TruckDriver> drivers = driverRepository.findAll();

        return drivers.stream()
                .skip((long) query.page() * query.size())
                .limit(query.size())
                .map(driver -> new DriverSummaryView(
                        driver.getDriverId(),
                        driver.getFullName(),
                        driver.getLicense().getExpiryDate(),
                        driver.getLicense().isExpired()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<DriverCommandView> handle(GetDriverCommandHistoryQuery query) {
        RefrigeratedTruck truck = truckRepository.findById(query.truckId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Truck not found with ID: " + query.truckId()));

        return truck.getDriverCommands().stream()
                .map(this::mapToDriverCommandView)
                .collect(Collectors.toList());
    }

    private DriverCommandView mapToDriverCommandView(DriverCommand command) {
        return new DriverCommandView(
                command.getCommandId(),
                command.getType(),
                command.getStatus(),
                command.getIssuedByAdminId(),
                command.getCustomMessage(),
                command.getIssuedAt(),
                command.getAcknowledgedAt(),
                command.getRejectionReason()
        );
    }
}
