package IOT.coldchain.application.query;

public class SensorDeviceDto {
    public String macAddress;
    public String location;

    public SensorDeviceDto(String macAddress, String location){
        this.macAddress = macAddress;
        this.location = location;
    }
}
