package IOT.coldchain.application.command;

import IOT.coldchain.application.port.ContainerRepository;
import IOT.coldchain.domain.entity.Container;
import IOT.coldchain.domain.factory.ContainerFactory;
import IOT.coldchain.application.port.in.RegisterContainerUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterContainerHandler implements RegisterContainerUseCase {
    private final ContainerRepository repository;

    public RegisterContainerHandler(ContainerRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Override
    public void handle(RegisterContainerCommand cmd) {
        // Factory to create Domain Entity
        Container container = ContainerFactory.createNew(cmd.containerId, cmd.minTemp, cmd.maxTemp);
        repository.save(container);
    }
}