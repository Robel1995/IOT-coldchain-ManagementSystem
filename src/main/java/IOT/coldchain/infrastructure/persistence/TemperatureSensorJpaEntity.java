package IOT.coldchain.infrastructure.persistence;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sensor_devices")
@Data
@NoArgsConstructor
public class TemperatureSensorJpaEntity {
    @Id
    private String macAddress; // Primary Key
    private String location;

    // Getters
    public String getMacAddress() {
        return macAddress;
    }
    public String getLocation() {
        return location;
    }
    // Setters
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    public void setLocation(String location) {
        this.location = location;
    }
}