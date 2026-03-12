package IOT.coldchain.api.controller;


import IOT.coldchain.application.command.*;
import IOT.coldchain.application.query.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/containers")
public class ContainerController {

    private final RegisterContainerHandler registerHandler;
    private final RecordTemperatureHandler recordTempHandler;
    private final ContainerQueryHandler queryHandler;
    private final UpdateContainerRangeHandler updateHandler;
    private final DeleteContainerHandler deleteHandler;

    public ContainerController(RegisterContainerHandler registerHandler,
                               RecordTemperatureHandler recordTempHandler,
                               ContainerQueryHandler queryHandler,
                               UpdateContainerRangeHandler updateHandler,
                               DeleteContainerHandler deleteHandler) {
        this.registerHandler = registerHandler;
        this.recordTempHandler = recordTempHandler;
        this.queryHandler = queryHandler;
        this.updateHandler = updateHandler;
        this.deleteHandler = deleteHandler;
    }

    // COMMAND 1
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterContainerCommand command) {
        registerHandler.handle(command);
        return ResponseEntity.ok("Container Registered");
    }

    // COMMAND 2
    @PostMapping("/telemetry")
    public ResponseEntity<String> recordTemperature(@RequestBody RecordTemperatureCommand command) {
        recordTempHandler.handle(command);
        return ResponseEntity.ok("Temperature Recorded");
    }
    //  COMMAND 3
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContainer(@PathVariable String id) {
        try {
            deleteHandler.handle(new DeleteContainerCommand(id));
            return ResponseEntity.ok("Container deleted successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // QUERY 1
    @GetMapping("/{id}")
    public ResponseEntity<ContainerDto> getStatus(@PathVariable String id) {
        return ResponseEntity.ok(queryHandler.getContainerStatus(id));
    }

    // QUERY 2
    @GetMapping("/spoiled")
    public ResponseEntity<List<ContainerDto>> getSpoiled() {
        return ResponseEntity.ok(queryHandler.getSpoiledContainers());
    }
    //
    @PutMapping("/range")
    public ResponseEntity<String> updateRange(@RequestBody UpdateContainerRangeCommand command) {
        try {
            updateHandler.handle(command);
            return ResponseEntity.ok("Container range updated successfully");
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}