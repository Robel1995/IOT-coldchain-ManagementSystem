package IOT.coldchain.application.query;

public class TemperatureSensorDto {
    public String macAddress;
    public String location;

    public TemperatureSensorDto(String macAddress, String location){
        this.macAddress = macAddress;
        this.location = location;
    }
}
