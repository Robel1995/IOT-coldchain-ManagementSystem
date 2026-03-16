package IOT.coldchain.application.command;

import IOT.coldchain.application.port.ContainerRepository;
import IOT.coldchain.domain.entity.Container;
import IOT.coldchain.application.port.in.DeliverCargoUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliverCargoHandler implements DeliverCargoUseCase {
    private final ContainerRepository repository;

    public DeliverCargoHandler(ContainerRepository repository) {
        this.repository = repository;
    }


    @Transactional
    @Override
    public void handle(DeliverCargoCommand cmd) {
        Container container = repository.findById(cmd.containerId)
                .orElseThrow(() -> new RuntimeException("Container not found"));

        // Delegate to the Aggregate Root to enforce the strict rules
        container.deliverCargo(cmd.serialNumber);

        repository.save(container);
    }
}