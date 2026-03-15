package IOT.coldchain.application.command;

import IOT.coldchain.application.port.ContainerRepository;
import IOT.coldchain.domain.entity.Container;
import org.springframework.stereotype.Service;

@Service
public class InstallSensorHandler {
    private final ContainerRepository repository;

    public InstallSensorHandler(ContainerRepository repository) {
        this.repository = repository;
    }

    public void handle(InstallSensorCommand cmd) {
        Container container = repository.findById(cmd.containerId)
                .orElseThrow(() -> new RuntimeException("Container not found"));

        // The container already has the addSensor() method we wrote earlier!
        container.addSensor(cmd.macAddress, cmd.location);

        repository.save(container);
    }
}