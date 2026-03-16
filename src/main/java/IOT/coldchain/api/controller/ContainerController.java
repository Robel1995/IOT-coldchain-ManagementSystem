package IOT.coldchain.api.controller;

import IOT.coldchain.application.command.*;
import IOT.coldchain.application.query.*;
import IOT.coldchain.application.port.in.RegisterContainerUseCase;
import IOT.coldchain.application.port.in.RecordTemperatureUseCase;
import IOT.coldchain.application.port.in.AddCargoUseCase;
import IOT.coldchain.application.port.in.DeliverCargoUseCase;
import IOT.coldchain.application.port.in.InstallSensorUseCase;
import IOT.coldchain.application.port.in.UpdateContainerRangeUseCase;
import IOT.coldchain.application.port.in.DeleteContainerUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/containers")
public class ContainerController {

    // --- ALL COMMAND HANDLERS (CQRS WRITE) ---
    private final RegisterContainerUseCase registerHandler;
    private final RecordTemperatureUseCase recordTempHandler;
    private final UpdateContainerRangeUseCase updateHandler;
    private final DeleteContainerUseCase deleteHandler;
    private final InstallSensorUseCase sensorHandler;
    private final AddCargoUseCase cargoHandler;
    private final DeliverCargoUseCase deliverHandler;

    // --- ALL QUERY HANDLERS (CQRS READ) ---
    private final ContainerQueryHandler queryHandler;

    public ContainerController( RegisterContainerUseCase registerHandler,
                                RecordTemperatureUseCase recordTempHandler,
                                UpdateContainerRangeUseCase updateHandler,
                                DeleteContainerUseCase deleteHandler,
                                InstallSensorUseCase sensorHandler,
                                AddCargoUseCase cargoHandler,
                                DeliverCargoUseCase deliverHandler,
                               ContainerQueryHandler queryHandler) {
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
    public ResponseEntity<String> register(@RequestBody RegisterContainerCommand command) {
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
    public ResponseEntity<String> loadCargo(@RequestBody AddCargoCommand cmd) {
        cargoHandler.handle(cmd);                              // Spring catches any exception
        return ResponseEntity.ok("Vaccine Cargo Loaded");
    }

    @PostMapping("/deliver")
    public ResponseEntity<String> deliverCargo(@RequestBody DeliverCargoCommand cmd) {
            deliverHandler.handle(cmd);
            return ResponseEntity.ok("Cargo successfully delivered to the Hospital");
    }

    @PutMapping("/range")
    public ResponseEntity<String> updateRange(@RequestBody UpdateContainerRangeCommand command) {
            updateHandler.handle(command);
            return ResponseEntity.ok("Container safe temperature range updated successfully");

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContainer(@PathVariable String id) {
            deleteHandler.handle(new DeleteContainerCommand(id));
            return ResponseEntity.ok("Container deleted successfully from the system");
    }

    // ==========================================
    // 🟢 CQRS: QUERIES (Read Data Only)
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<ContainerDto> getStatus(@PathVariable String id) {
        return ResponseEntity.ok(queryHandler.getContainerStatus(id));
    }

    @GetMapping("/spoiled")
    public ResponseEntity<List<ContainerDto>> getSpoiled() {
        return ResponseEntity.ok(queryHandler.getSpoiledContainers());
    }
}