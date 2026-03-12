package IOT.coldchain.application.command;

import IOT.coldchain.application.port.ContainerRepository;
import IOT.coldchain.domain.entity.Container;
import IOT.coldchain.domain.valueobject.TemperatureReading;
import org.springframework.stereotype.Service;

@Service
public class RecordTemperatureHandler {
    private final ContainerRepository repository;

    public RecordTemperatureHandler(ContainerRepository repository) {
        this.repository = repository;
    }

    public void handle(RecordTemperatureCommand cmd) {
        // Orchestration
        Container container = repository.findById(cmd.containerId)
                .orElseThrow(() -> new RuntimeException("Container not found"));

        TemperatureReading reading = new TemperatureReading(cmd.temperature);

        container.recordTemperature(reading.getValue());

        // Call Domain Method (Business Logic)
       // container.recordTemperature(cmd.temperature);

        // Save state
        repository.save(container);
    }
}