package IOT.coldchain.application.query;

import IOT.coldchain.application.port.RefrigeratedTruckRepository;
import IOT.coldchain.domain.entity.RefrigeratedTruck;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RefrigeratedTruckQueryHandler {
    private final RefrigeratedTruckRepository repository;

    public RefrigeratedTruckQueryHandler(RefrigeratedTruckRepository repository) {
        this.repository = repository;
    }

    // Query 1: Get single container status
    public RefrigeratedTruckDto getContainerStatus(String containerId) {
        return repository.findById(containerId)
                .map(this::Dto)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    // Query 2: Get all spoiled containers
    public List<RefrigeratedTruckDto> getSpoiledContainers() {
        return repository.findAllSpoiled().stream()
                .map(this::Dto)
                .collect(Collectors.toList());
    }
    //---Private  Mapper for both queries -----
    private RefrigeratedTruckDto Dto(RefrigeratedTruck c){
        //map each cargoItem to cargoItemDto
        List<VaccineBoxDto> cargoDto = c.getCargoItems().stream()
                .map(item -> new VaccineBoxDto(item.getSerialNumber(), item.getMedicineName(), item.isRuined()))
                .collect(Collectors.toList());
        // Map each SensorDevice → SensorDeviceDto
        List<TemperatureSensorDto> sensorDto = c.getSensors().stream()
                .map(s -> new TemperatureSensorDto( s.getMacAddress(), s.getLocation()))
                .collect(Collectors.toList());
        // Build the full DTO
        return new RefrigeratedTruckDto(c.getContainerId(), c.getStatus().name(), c.getThreshold().getMinTemp(), c.getThreshold().getMaxTemp(), cargoDto,sensorDto);
    }

}