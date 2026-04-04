package IOT.coldchain.application.handler;

import IOT.coldchain.application.dto.query.GetAllSpoiledTrucksQuery;
import IOT.coldchain.application.dto.query.GetAllTrucksQuery;
import IOT.coldchain.application.dto.query.GetSensorReadingHistoryQuery;
import IOT.coldchain.application.dto.query.GetTruckByIdQuery;
import IOT.coldchain.application.dto.query.GetZoneDetailQuery;
import IOT.coldchain.application.dto.view.DriverCommandView;
import IOT.coldchain.application.dto.view.SensorView;
import IOT.coldchain.application.dto.view.TemperatureReadingView;
import IOT.coldchain.application.dto.view.TruckDetailView;
import IOT.coldchain.application.dto.view.TruckSummaryView;
import IOT.coldchain.application.dto.view.VaccineBoxView;
import IOT.coldchain.application.dto.view.ZoneDetailView;
import IOT.coldchain.application.port1.in.query.GetTruckQueryUseCase;
import IOT.coldchain.application.port1.out.TruckRepository;
import IOT.coldchain.domain.entity.RefrigeratedTruck;
import IOT.coldchain.domain.entity.CompartmentZone;
import IOT.coldchain.domain.entity.DriverCommand;
import IOT.coldchain.domain.entity.TemperatureSensor;
import IOT.coldchain.domain.entity.VaccineBox;
import IOT.coldchain.domain.valueobject.Temperature;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * QUERY HANDLER — GetTruckQueryHandler
 *
 * Handles all read operations for RefrigeratedTruck aggregates and their children.
 *
 * ─── CQRS Read Side ───────────────────────────────────────────────────────────
 * This handler is read-only. It returns flat view objects and never mutates
 * domain aggregates.
 *
 * ─── Clean Architecture ───────────────────────────────────────────────────────
 * This handler implements the GetTruckQueryUseCase inbound port and depends
 * on the TruckRepository outbound port. It contains NO framework annotations.
 *
 * ─── View Mapping ─────────────────────────────────────────────────────────────
 * This handler contains comprehensive mapping logic to convert the rich domain
 * aggregate graph into flat, read-optimized DTOs.
 */
public class GetTruckQueryHandler implements GetTruckQueryUseCase {

    private final TruckRepository truckRepository;

    public GetTruckQueryHandler(TruckRepository truckRepository) {
        this.truckRepository = truckRepository;
    }

    @Override
    public TruckDetailView handle(GetTruckByIdQuery query) {
        RefrigeratedTruck truck = truckRepository.findById(query.truckId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Truck not found with ID: " + query.truckId()));

        List<ZoneDetailView> zones = truck.getZones().stream()
                .map(this::mapToZoneDetailView)
                .collect(Collectors.toList());

        List<DriverCommandView> pendingCommands = truck.getDriverCommands().stream()
                .filter(cmd -> cmd.getStatus().name().equals("ISSUED"))
                .map(this::mapToDriverCommandView)
                .collect(Collectors.toList());

        return new TruckDetailView(
                truck.getTruckId(),
                truck.getStatus(),
                truck.getAssignedDriverId(),
                zones,
                pendingCommands
        );
    }

    @Override
    public List<TruckSummaryView> handle(GetAllTrucksQuery query) {
        List<RefrigeratedTruck> trucks = truckRepository.findAll();

        return trucks.stream()
                .skip((long) query.page() * query.size())
                .limit(query.size())
                .map(this::mapToTruckSummaryView)
                .collect(Collectors.toList());
    }

    @Override
    public List<TruckSummaryView> handle(GetAllSpoiledTrucksQuery query) {
        List<RefrigeratedTruck> spoiledTrucks = truckRepository.findAllSpoiled();

        return spoiledTrucks.stream()
                .map(this::mapToTruckSummaryView)
                .collect(Collectors.toList());
    }

    @Override
    public ZoneDetailView handle(GetZoneDetailQuery query) {
        RefrigeratedTruck truck = truckRepository.findById(query.truckId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Truck not found with ID: " + query.truckId()));

        CompartmentZone zone = truck.getZones().stream()
                .filter(z -> z.getZoneId().equals(query.zoneId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Zone not found with ID: " + query.zoneId()));

        return mapToZoneDetailView(zone);
    }

    @Override
    public List<TemperatureReadingView> handle(GetSensorReadingHistoryQuery query) {
        RefrigeratedTruck truck = truckRepository.findById(query.truckId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Truck not found with ID: " + query.truckId()));

        CompartmentZone zone = truck.getZones().stream()
                .filter(z -> z.getZoneId().equals(query.zoneId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Zone not found with ID: " + query.zoneId()));

        TemperatureSensor sensor = zone.getSensors().stream()
                .filter(s -> s.getMacAddress().equals(query.macAddress()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sensor not found with MAC: " + query.macAddress()));

        return sensor.getReadingHistory().stream()
                .sorted(Comparator.comparing(Temperature::getRecordedAt))
                .map(this::mapToTemperatureReadingView)
                .collect(Collectors.toList());
    }

    private TruckSummaryView mapToTruckSummaryView(RefrigeratedTruck truck) {
        int totalCargo = truck.getZones().stream()
                .mapToInt(zone -> zone.getVaccineBoxes().size())
                .sum();

        return new TruckSummaryView(
                truck.getTruckId(),
                truck.getStatus(),
                truck.getZones().size(),
                totalCargo,
                truck.getAssignedDriverId()
        );
    }

    private ZoneDetailView mapToZoneDetailView(CompartmentZone zone) {
        List<SensorView> sensors = zone.getSensors().stream()
                .map(this::mapToSensorView)
                .collect(Collectors.toList());

        List<VaccineBoxView> vaccineBoxes = zone.getVaccineBoxes().stream()
                .map(this::mapToVaccineBoxView)
                .collect(Collectors.toList());

        return new ZoneDetailView(
                zone.getZoneId(),
                zone.getZoneName().getName(),
                zone.getStatus(),
                zone.getThreshold().getMinTemp(),
                zone.getThreshold().getMaxTemp(),
                sensors,
                vaccineBoxes
        );
    }

    private SensorView mapToSensorView(TemperatureSensor sensor) {
        Temperature latestReading = sensor.getLatestReading();
        Double latestTemp = latestReading != null ? latestReading.getValue() : null;
        java.time.Instant latestTime = latestReading != null ? latestReading.getRecordedAt() : null;

        return new SensorView(
                sensor.getMacAddress(),
                sensor.getLocation(),
                latestTemp,
                latestTime,
                sensor.getReadingHistory().size()
        );
    }

    private VaccineBoxView mapToVaccineBoxView(VaccineBox box) {
        return new VaccineBoxView(
                box.getSerialNumber(),
                box.getMedicineName(),
                box.isRuined()
        );
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

    private TemperatureReadingView mapToTemperatureReadingView(Temperature reading) {
        return new TemperatureReadingView(
                reading.getValue(),
                reading.getRecordedAt()
        );
    }
}
