package IOT.coldchain.application.command;

import IOT.coldchain.application.port.ContainerRepository;
import IOT.coldchain.domain.entity.Container;
import IOT.coldchain.application.port.in.UpdateContainerRangeUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateContainerRangeHandler implements UpdateContainerRangeUseCase {
    private final ContainerRepository repository;
    public UpdateContainerRangeHandler(ContainerRepository repository)
    {
        this.repository=repository;
    }

    @Transactional
    @Override
    public void handle(UpdateContainerRangeCommand cmd)
    {
        Container container= repository.findById(cmd.containerId).orElseThrow(() -> new RuntimeException("Container not found"));
        container.updateTemperatureRange(cmd.newMinTemp, cmd.newMaxTemp);

        repository.save(container);

    }
}
