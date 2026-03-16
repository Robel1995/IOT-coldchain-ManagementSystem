package IOT.coldchain.application.command;

import IOT.coldchain.application.port.ContainerRepository;
import IOT.coldchain.domain.entity.Container;
import IOT.coldchain.application.port.in.InstallSensorUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InstallSensorHandler implements InstallSensorUseCase {
    private final ContainerRepository repository;

    public InstallSensorHandler(ContainerRepository repository) {
        this.repository = repository;
    }


    @Transactional
    @Override
    public void handle(InstallSensorCommand cmd) {
        Container container = repository.findById(cmd.containerId)
                .orElseThrow(() -> new RuntimeException("Container not found"));

        container.addSensor(cmd.macAddress, cmd.location);

        repository.save(container);
    }
}