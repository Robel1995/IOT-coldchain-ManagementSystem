package IOT.coldchain.application.command;

import IOT.coldchain.application.port.ContainerRepository;
import IOT.coldchain.domain.entity.Container;
import IOT.coldchain.application.port.in.RecordTemperatureUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecordTemperatureHandler implements RecordTemperatureUseCase {
    private final ContainerRepository repository;

    public RecordTemperatureHandler(ContainerRepository repository) {
        this.repository = repository;
    }


    @Transactional
    @Override
    public void handle(RecordTemperatureCommand cmd) {
        // Orchestration
        Container container = repository.findById(cmd.containerId)
                .orElseThrow(() -> new RuntimeException("Container not found"));
        // Call Domain Method (Business Logic)
        container.recordTemperature(cmd.temperature);

        // Save state
        repository.save(container);
    }
}