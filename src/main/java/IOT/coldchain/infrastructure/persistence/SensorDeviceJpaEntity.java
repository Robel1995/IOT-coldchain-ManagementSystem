package IOT.coldchain.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sensor_devices")
public class SensorDeviceJpaEntity {
    @Id
    public String macAddress; // Primary Key
    public String location;
}