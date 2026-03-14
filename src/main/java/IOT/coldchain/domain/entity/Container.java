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
