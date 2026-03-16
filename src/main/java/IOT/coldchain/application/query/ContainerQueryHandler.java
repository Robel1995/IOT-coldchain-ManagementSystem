package IOT.coldchain.application.query;

import IOT.coldchain.application.port.ContainerRepository;
import IOT.coldchain.domain.entity.Container;
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
                .map(this::Dto)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    // Query 2: Get all spoiled containers
    public List<ContainerDto> getSpoiledContainers() {
        return repository.findAllSpoiled().stream()
                .map(this::Dto)
                .collect(Collectors.toList());
    }
    //---Private  Mapper for both queries -----
    private ContainerDto Dto(Container c){
        //map each cargoItem to cargoItemDto
        List<CargoItemDto> cargoDto = c.getCargoItems().stream()
                .map(item -> new CargoItemDto(item.getSerialNumber(), item.getMedicineName(), item.isRuined()))
                .collect(Collectors.toList());
        // Map each SensorDevice → SensorDeviceDto
        List<SensorDeviceDto> sensorDto = c.getSensors().stream()
                .map(s -> new SensorDeviceDto( s.getMacAddress(), s.getLocation()))
                .collect(Collectors.toList());
        // Build the full DTO
        return new ContainerDto(c.getContainerId(), c.getStatus().name(), c.getThreshold().getMinTemp(), c.getThreshold().getMaxTemp(), cargoDto,sensorDto);
    }

}