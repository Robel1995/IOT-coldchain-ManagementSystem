package IOT.coldchain.domain.entity;

import IOT.coldchain.domain.enums.ContainerStatus;
import IOT.coldchain.domain.valueobject.TemperatureThreshold;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AGGREGATE ROOT: Matches the 'Order' in the instructor's slide.
 * It enforces all business rules for itself and its Child Entities.
 */
public class Container {
    private final String containerId; // Aggregate Identity
    private ContainerStatus status;

    // Value Object
    private TemperatureThreshold threshold;

    // Child Entities
    private List<CargoItem> cargoItems;
    private List<SensorDevice> sensors;

    // Protected Constructor: Forces use of Factory or Reconstitute
    protected Container(String containerId, TemperatureThreshold threshold, ContainerStatus status) {
        this.containerId = containerId;
        this.threshold = threshold;
        this.status = status;
        this.cargoItems = new ArrayList<>();
        this.sensors = new ArrayList<>();
    }

    // --- AGGREGATE ROOT BEHAVIORS (Business Rules) ---

    // 1. The Aggregate Root acts as a Factory for its children
    public void addCargo(String serialNumber, String medicineName) {
        if (this.status == ContainerStatus.SPOILED) {
            throw new IllegalStateException("Business Rule Violation: Cannot load cargo into a SPOILED container.");
        }
        // Container instantiates the child because CargoItem constructor is protected!
        CargoItem newCargo = new CargoItem(serialNumber, medicineName);
        this.cargoItems.add(newCargo);
    }

    public void deliverCargo(String serialNumber) {
        if (this.status == ContainerStatus.SPOILED) {
            throw new IllegalStateException("FATAL: Cannot deliver vaccines from a SPOILED container! Lives are at risk.");
        }

        // Find and remove the cargo item
        boolean foundAndRemoved = this.cargoItems.removeIf(item -> item.getSerialNumber().equals(serialNumber));

        if (!foundAndRemoved) {
            throw new IllegalArgumentException("Cargo box " + serialNumber + " is not in this container!");
        }
    }
    public void addSensor(String macAddress, String location) {
        this.sensors.add(new SensorDevice(macAddress, location));
    }

    // 2. Cascading Business Rules
    public void recordTemperature(double currentTemp) {
        if (this.status == ContainerStatus.SPOILED) {
            return; // Once spoiled, always spoiled
        }

        // Use the Value Object to check the physics/business rules
        if (threshold.isViolatedBy(currentTemp)) {
            this.status = ContainerStatus.SPOILED;

            // CASCADE EFFECT: The Root ruins all the Child Entities inside it!
            for (CargoItem item : cargoItems) {
                item.markAsRuined();
            }
        }
    }

    // 3. Updating an Immutable Value Object
    public void updateTemperatureRange(double newMinTemp, double newMaxTemp) {
        if (this.status == ContainerStatus.SPOILED) {
            throw new IllegalStateException("Business Rule Violation: Cannot update rules for a spoiled container.");
        }
        // Value Objects are immutable. We replace the old one with a brand new one!
        this.threshold = new TemperatureThreshold(newMinTemp, newMaxTemp);
    }

    // 4. Deletion Rule
    public boolean canBeDeleted() {
        return this.status == ContainerStatus.SPOILED; // Can only delete ruined containers
    }

    // --- ENCAPSULATED GETTERS ---

    public String getContainerId() { return containerId; }
    public ContainerStatus getStatus() { return status; }
    public TemperatureThreshold getThreshold() { return threshold; }

    // SENIOR ARCHITECT TRICK: Return unmodifiable lists to protect the Aggregate!
    public List<CargoItem> getCargoItems() {
        return Collections.unmodifiableList(cargoItems);
    }
    public List<SensorDevice> getSensors() {
        return Collections.unmodifiableList(sensors);
    }

    // --- RECONSTITUTION (For Database Adapter) ---

    // We update this so the DB can load the Container WITH its children
    public static Container reconstitute(String containerId, double minTemp, double maxTemp, ContainerStatus status, List<CargoItem> loadedCargo, List<SensorDevice> loadedSensors) {
        TemperatureThreshold loadedThreshold = new TemperatureThreshold(minTemp, maxTemp);
        Container container = new Container(containerId, loadedThreshold, status);

        // Load the children from the DB bypasses the "spoiled" check of addCargo()
        container.cargoItems = loadedCargo != null ? loadedCargo : new ArrayList<>();
        container.sensors = loadedSensors != null ? loadedSensors : new ArrayList<>();

        return container;
    }
}

/*
package IOT.coldchain.domain.entity;

import IOT.coldchain.domain.enums.ContainerStatus;

public class Container {
    private String containerId;
    private double minSafeTemperature;
    private double maxSafeTemperature;
    private ContainerStatus status;

    protected Container(String containerId, double minSafeTemperature, double maxSafeTemperature, ContainerStatus status) {
        this.containerId = containerId;
        this.minSafeTemperature = minSafeTemperature;
        this.maxSafeTemperature = maxSafeTemperature;
        this.status = status;
    }

    // Core Business Logic
    public void recordTemperature(double currentTemp) {
        if (this.status == ContainerStatus.SPOILED) {
            return; // Once spoiled, always spoiled
        }
        if (currentTemp < minSafeTemperature || currentTemp > maxSafeTemperature) {
            this.status = ContainerStatus.SPOILED;
        }
    }

    // static method for the Infrastructure layer to load DB data
    public static Container reconstitute(String containerId, double minSafeTemperature, double maxSafeTemperature, ContainerStatus status) {
        return new Container(containerId, minSafeTemperature, maxSafeTemperature, status);
    }


    // Update Rule Can only update if SAFE, and min must be < max
    public void updateTemperatureRange(double newMinTemp, double newMaxTemp) {
        if (this.status == ContainerStatus.SPOILED) {
            throw new IllegalStateException("Business Rule Violation: Cannot update rules for a spoiled container.");
        }
        if (newMinTemp >= newMaxTemp) {
            throw new IllegalArgumentException("Business Rule Violation: New minimum temperature must be strictly less than maximum.");
        }
        this.minSafeTemperature = newMinTemp;
        this.maxSafeTemperature = newMaxTemp;
    }

    // Delete Rule  Can only be removed from the system if it's already spoiled
    public boolean canBeDeleted() {
        return this.status == ContainerStatus.SPOILED;
    }

    // Getters
    public String getContainerId() { return containerId; }
    public double getMinSafeTemperature() { return minSafeTemperature; }
    public double getMaxSafeTemperature() { return maxSafeTemperature; }
    public ContainerStatus getStatus() { return status; }
}
*/
