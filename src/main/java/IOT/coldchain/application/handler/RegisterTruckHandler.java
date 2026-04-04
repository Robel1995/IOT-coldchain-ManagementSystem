package IOT.coldchain.application.handler;

import IOT.coldchain.application.dto.command.RegisterMultiZoneTruckCommand;
import IOT.coldchain.application.dto.command.RegisterTruckCommand;
import IOT.coldchain.application.port1.in.command.RegisterTruckUseCase;
import IOT.coldchain.application.port1.out.TruckRepository;
import IOT.coldchain.domain.entity.RefrigeratedTruck;
import IOT.coldchain.domain.entity.RefrigeratedTruckFactory;
import IOT.coldchain.domain.valueobject.TemperatureRange;
import IOT.coldchain.domain.valueobject.ZoneName;

import java.util.ArrayList;
import java.util.List;

/**
 * COMMAND HANDLER — RegisterTruckHandler
 *
 * Handles truck registration operations: single-zone and multi-zone registration.
 *
 * ─── Clean Architecture ───────────────────────────────────────────────────────
 * This handler implements the RegisterTruckUseCase inbound port and depends
 * on the TruckRepository outbound port. It contains NO framework annotations.
 *
 * ─── Factory Pattern ──────────────────────────────────────────────────────────
 * This handler delegates aggregate creation to the RefrigeratedTruckFactory,
 * which encapsulates the complex construction logic and validation rules.
 *
 * ─── Error Handling ───────────────────────────────────────────────────────────
 * All exceptions from the domain are propagated unchanged to the caller.
 */
public class RegisterTruckHandler implements RegisterTruckUseCase {

    private final TruckRepository truckRepository;

    public RegisterTruckHandler(TruckRepository truckRepository) {
        this.truckRepository = truckRepository;
    }

    @Override
    public String handle(RegisterTruckCommand command) {
        // Pass raw values - factory creates VOs internally
        RefrigeratedTruck truck = RefrigeratedTruckFactory.createNew(
                command.initialZoneName(),  // String
                command.minTemp(),          // double
                command.maxTemp()           // double
        );
        RefrigeratedTruck savedTruck = truckRepository.save(truck);
        return savedTruck.getTruckId();
    }

    @Override
    public String handle(RegisterMultiZoneTruckCommand command) {
        List<RefrigeratedTruckFactory.ZoneDefinition> definitions = new ArrayList<>();

        for (RegisterMultiZoneTruckCommand.ZoneDefinitionDto dto : command.zoneDefinitions()) {
            // ZoneDefinition takes raw values, not VOs
            definitions.add(new RefrigeratedTruckFactory.ZoneDefinition(
                    dto.zoneName(),   // String
                    dto.minTemp(),    // double
                    dto.maxTemp()     // double
            ));
        }

        RefrigeratedTruck truck = RefrigeratedTruckFactory.createNewMultiZone(definitions);

        RefrigeratedTruck savedTruck = truckRepository.save(truck);
        return savedTruck.getTruckId();
    }
}
