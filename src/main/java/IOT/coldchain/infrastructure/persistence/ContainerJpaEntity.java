package IOT.coldchain.infrastructure.persistence;



import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "containers")
public class ContainerJpaEntity {
    @Id
    public String containerId;

    // Flattened Value Object properties
    public double minSafeTemperature;
    public double maxSafeTemperature;
    public String status;

    // --- DDD AGGREGATE ROOT MAPPING ---

    // CascadeType.ALL means if we save the Container, it automatically saves the Cargo.
    // orphanRemoval=true means if we remove Cargo from the Container, it deletes it from the DB.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "container_id") // This creates the Foreign Key in the cargo_items table
    public List<CargoItemJpaEntity> cargoItems = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "container_id") // This creates the Foreign Key in the sensor_devices table
    public List<SensorDeviceJpaEntity> sensors = new ArrayList<>();
}

/*

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "containers")
public class ContainerJpaEntity {
    @Id
    public String containerId;
    public double minSafeTemperature;
    public double maxSafeTemperature;
    public String status;
}*/
