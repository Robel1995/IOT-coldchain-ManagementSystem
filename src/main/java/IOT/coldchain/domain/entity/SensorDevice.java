package IOT.coldchain.domain.entity;

public class SensorDevice {
    private String macAddress;
    private String location; // e.g., "Door", "Bottom"

    protected SensorDevice(String macAddress, String location) {
        this.macAddress = macAddress;
        this.location = location;
    }
    public static SensorDevice reconstitute(String macAddress, String location) {
        return new SensorDevice(macAddress, location);
    }

    public String getMacAddress() { return macAddress; }
    public String getLocation() { return location; }
}