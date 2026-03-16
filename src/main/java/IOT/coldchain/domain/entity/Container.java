package IOT.coldchain.domain.entity;

import IOT.coldchain.domain.enums.ContainerStatus;
import IOT.coldchain.domain.valueobject.TemperatureThreshold;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Container {
    private final String containerId; // Aggregate Identity
    private ContainerStatus status;

    // Value Object
    private TemperatureThreshold threshold;

    // Child Entities
    private List<CargoItem> cargoItems;
    private List<SensorDevice> sensors;

     Container(String containerId, TemperatureThreshold threshold) {
        this.containerId = containerId;
        this.threshold = threshold;
        this.status = ContainerStatus.SAFE;
        this.cargoItems = new ArrayList<>();
        this.sensors = new ArrayList<>();
    }

    // Business Rules

    // 1. The Aggregate Root acts as a Factory for its children
    public void addCargo(String serialNumber, String medicineName) {
        if (this.status == ContainerStatus.SPOILED) {
            throw new IllegalStateException("Business Rule Violation: Cannot load cargo into a SPOILED container.");
        }

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
        // Physics guard — sensor reading cannot be below absolute zero
        if (currentTemp < -273.15) {
            throw new IllegalArgumentException( "Physics Violation: (sensor reading)Temperature cannot be below absolute zero. Received: " + currentTemp + "°C");
        }
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
    public void assertCanBeDeleted() {
         if (this.status != ContainerStatus.SPOILED) {
             throw new IllegalStateException( "Business Rule Violation: Cannot delete a SAFE container.");
         } // If SPOILED method returns normally, deletion is allowed
    }

    // --- ENCAPSULATED GETTERS ---

    public String getContainerId() { return containerId; }
    public ContainerStatus getStatus() { return status; }
    public TemperatureThreshold getThreshold() { return threshold; }

    //  Return unmodifiable lists to protect the Aggregate!
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
        Container container = new Container(containerId, loadedThreshold);

        // Load the children from the DB
        container.status = status;
        container.cargoItems = loadedCargo != null ? loadedCargo : new ArrayList<>();
        container.sensors = loadedSensors != null ? loadedSensors : new ArrayList<>();

        return container;
    }
}

