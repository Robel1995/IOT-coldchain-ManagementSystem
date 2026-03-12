package IOT.coldchain.application.command;

import IOT.coldchain.application.port.ContainerRepository;
import IOT.coldchain.domain.entity.Container;
import org.springframework.stereotype.Service;

@Service
public class UpdateContainerRangeHandler {
    private final ContainerRepository repository;
    public UpdateContainerRangeHandler(ContainerRepository repository)
    {
        this.repository=repository;
    }
    public void handle(UpdateContainerRangeCommand cmd)
    {
        Container container= repository.findById(cmd.containerId).orElseThrow(() -> new RuntimeException("Container not found"));
        container.updateTemperatureRange(cmd.newMinTemp, cmd.newMaxTemp);

        repository.save(container);

    }
}
