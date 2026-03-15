package IOT.coldchain.application.command;

import IOT.coldchain.application.port.ContainerRepository;
import IOT.coldchain.domain.entity.Container;
import org.springframework.stereotype.Service;

@Service
public class DeliverCargoHandler {
    private final ContainerRepository repository;

    public DeliverCargoHandler(ContainerRepository repository) {
        this.repository = repository;
    }

    public void handle(DeliverCargoCommand cmd) {
        Container container = repository.findById(cmd.containerId)
                .orElseThrow(() -> new RuntimeException("Container not found"));

        // Delegate to the Aggregate Root to enforce the strict life-saving rules!
        container.deliverCargo(cmd.serialNumber);

        repository.save(container);
    }
}