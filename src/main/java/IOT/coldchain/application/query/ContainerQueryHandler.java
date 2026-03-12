package IOT.coldchain.application.query;

import IOT.coldchain.application.port.ContainerRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContainerQueryHandler {
    private final ContainerRepository repository;

    public ContainerQueryHandler(ContainerRepository repository) {
        this.repository = repository;
    }

    // Query 1: Get single container status
    public ContainerDto getContainerStatus(String containerId) {
        return repository.findById(containerId)
                .map(c -> new ContainerDto(c.getContainerId(), c.getStatus().name()))
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    // Query 2: Get all spoiled containers
    public List<ContainerDto> getSpoiledContainers() {
        return repository.findAllSpoiled().stream()
                .map(c -> new ContainerDto(c.getContainerId(), c.getStatus().name()))
                .collect(Collectors.toList());
    }
}