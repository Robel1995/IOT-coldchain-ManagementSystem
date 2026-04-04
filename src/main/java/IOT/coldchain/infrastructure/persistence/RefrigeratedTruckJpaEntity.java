package IOT.coldchain.infrastructure.persistence;

import lombok.Data;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "containers")
@Data
public class RefrigeratedTruckJpaEntity {
    @Id
    private String containerId;
    private double minSafeTemperature;
    private double maxSafeTemperature;
    private String status;

    // --- DDD AGGREGATE ROOT MAPPING ---

    // CascadeType.ALL ->  if we save the Container, it automatically saves the Cargo.
    // orphanRemoval=true -> if we remove Cargo from the Container, it deletes it from the DB.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "truck_id") // This creates the Foreign Key in the cargo_items table
    private List<VaccineBoxJpaEntity> cargoItems = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "truck_id") // This creates the Foreign Key in the sensor_devices table
    private List<TemperatureSensorJpaEntity> sensors = new ArrayList<>();
    public RefrigeratedTruckJpaEntity() {}

    //Getters
    public String getContainerId() {
        return containerId;
    }
    public double getMinSafeTemperature() {
        return minSafeTemperature;
    }
    public double getMaxSafeTemperature() {
        return maxSafeTemperature;
    }
    public String getStatus() {
        return status;
    }
    public List<VaccineBoxJpaEntity> getCargoItems() {
        return cargoItems;
    }
    public List<TemperatureSensorJpaEntity> getSensors() {
        return sensors;
    }
    //Setter
    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public void setMinSafeTemperature(double minSafeTemperature) {
        this.minSafeTemperature = minSafeTemperature;
    }
    public void setMaxSafeTemperature(double maxSafeTemperature) {
        this.maxSafeTemperature = maxSafeTemperature;
    }
    public void setCargoItems(List<VaccineBoxJpaEntity> cargoItems) {
        this.cargoItems = cargoItems;
    }
    public void setSensors(List<TemperatureSensorJpaEntity> sensors) {
        this.sensors = sensors;
    }



}


