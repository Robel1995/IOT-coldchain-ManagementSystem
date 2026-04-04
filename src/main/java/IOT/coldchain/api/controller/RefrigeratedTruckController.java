package IOT.coldchain.api.controller;

import IOT.coldchain.application.command.*;
import IOT.coldchain.application.query.*;
import IOT.coldchain.application.port.in.RegisterRefrigeratedTruckUseCase;
import IOT.coldchain.application.port.in.RecordTemperatureUseCase;
import IOT.coldchain.application.port.in.AddVaccineBoxUseCase;
import IOT.coldchain.application.port.in.DeliverCargoUseCase;
import IOT.coldchain.application.port.in.InstallSensorUseCase;
import IOT.coldchain.application.port.in.UpdateRefrigeratedTruckRangeUseCase;
import IOT.coldchain.application.port.in.DeleteRefrigeratedTruckUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/containers")
public class RefrigeratedTruckController {

    // --- ALL COMMAND HANDLERS (CQRS WRITE) ---
    private final RegisterRefrigeratedTruckUseCase registerHandler;
    private final RecordTemperatureUseCase recordTempHandler;
    private final UpdateRefrigeratedTruckRangeUseCase updateHandler;
    private final DeleteRefrigeratedTruckUseCase deleteHandler;
    private final InstallSensorUseCase sensorHandler;
    private final AddVaccineBoxUseCase cargoHandler;
    private final DeliverCargoUseCase deliverHandler;

    // --- ALL QUERY HANDLERS (CQRS READ) ---
    private final RefrigeratedTruckQueryHandler queryHandler;

    public RefrigeratedTruckController(RegisterRefrigeratedTruckUseCase registerHandler,
                                       RecordTemperatureUseCase recordTempHandler,
                                       UpdateRefrigeratedTruckRangeUseCase updateHandler,
                                       DeleteRefrigeratedTruckUseCase deleteHandler,
                                       InstallSensorUseCase sensorHandler,
                                       AddVaccineBoxUseCase cargoHandler,
                                       DeliverCargoUseCase deliverHandler,
                                       RefrigeratedTruckQueryHandler queryHandler) {
        this.registerHandler = registerHandler;
        this.recordTempHandler = recordTempHandler;
        this.updateHandler = updateHandler;
        this.deleteHandler = deleteHandler;
        this.sensorHandler = sensorHandler;
        this.cargoHandler = cargoHandler;
        this.deliverHandler = deliverHandler;
        this.queryHandler = queryHandler;
    }

    // ==========================================
    // 🔴 CQRS: COMMANDS (Mutate State)
    // ==========================================

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRefrigeratedTruckCommand command) {
        registerHandler.handle(command);
        return ResponseEntity.ok("EPSS Truck Registered Successfully");
    }

    @PostMapping("/telemetry")
    public ResponseEntity<String> recordTemperature(@RequestBody RecordTemperatureCommand command) {
        recordTempHandler.handle(command);
        return ResponseEntity.ok("Temperature Recorded by Aggregate Root");
    }

    @PostMapping("/sensors")
    public ResponseEntity<String> installSensor(@RequestBody InstallSensorCommand cmd) {
        sensorHandler.handle(cmd);
        return ResponseEntity.ok("IoT Sensor Installed on Truck");
    }

    @PostMapping("/cargo")
    public ResponseEntity<String> loadCargo(@RequestBody AddVaccineBoxCommand cmd) {
        cargoHandler.handle(cmd);                              // Spring catches any exception
        return ResponseEntity.ok("Vaccine Cargo Loaded");
    }

    @PostMapping("/deliver")
    public ResponseEntity<String> deliverCargo(@RequestBody DeliverVaccineBoxCommand cmd) {
            deliverHandler.handle(cmd);
            return ResponseEntity.ok("Cargo successfully delivered to the Hospital");
    }

    @PutMapping("/range")
    public ResponseEntity<String> updateRange(@RequestBody UpdateRefrigeratedTruckRangeCommand command) {
            updateHandler.handle(command);
            return ResponseEntity.ok("Container safe temperature range updated successfully");

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContainer(@PathVariable String id) {
            deleteHandler.handle(new DeleteRefrigeratedTruckCommand(id));
            return ResponseEntity.ok("Container deleted successfully from the system");
    }

    // ==========================================
    // 🟢 CQRS: QUERIES (Read Data Only)
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<RefrigeratedTruckDto> getStatus(@PathVariable String id) {
        return ResponseEntity.ok(queryHandler.getContainerStatus(id));
    }

    @GetMapping("/spoiled")
    public ResponseEntity<List<RefrigeratedTruckDto>> getSpoiled() {
        return ResponseEntity.ok(queryHandler.getSpoiledContainers());
    }
}