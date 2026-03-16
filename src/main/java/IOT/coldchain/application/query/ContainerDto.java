package IOT.coldchain.application.query;

import java.util.List;

public class ContainerDto {
    public String containerId;
    public String status;
    public double minTemp;
    public double maxTemp;
    public int cargoCount;
    public int sensorCount;
    public List<CargoItemDto> cargo;
    public List<SensorDeviceDto> sensors;
    public ContainerDto(String containerId, String status, double minTemp, double maxTemp, List<CargoItemDto> cargo, List<SensorDeviceDto> sensors) {
        this.containerId = containerId;
        this.status = status;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.cargo = cargo;
        this.sensors =sensors;
        this.cargoCount = cargo.size();
        this.sensorCount = sensors.size();
    }
}