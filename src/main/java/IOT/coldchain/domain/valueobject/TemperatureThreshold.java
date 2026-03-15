package IOT.coldchain.domain.valueobject;


public class TemperatureThreshold {
    private final double minTemp;
    private final double maxTemp;
    private final String scale;

    public TemperatureThreshold(double minTemp, double maxTemp) {

        //  Absolute zero is -273.15C. Impossible to be colder
        if (minTemp < -273.15 || maxTemp < -273.15) {
            throw new IllegalArgumentException("Physics Violation: Temperature cannot be below absolute zero (-273.15C).");
        }

        // Min must be strictly less than Max
        if (minTemp >= maxTemp) {
            throw new IllegalArgumentException("Business Rule Violation: Minimum temperature must be less than maximum.");
        }

        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.scale = "Celsius"; // Enforcing the standard measurement scale
    }

    public boolean isViolatedBy(double currentTemp) {
        return currentTemp < minTemp || currentTemp > maxTemp;
    }

    // Getters
    public double getMinTemp() { return minTemp; }
    public double getMaxTemp() { return maxTemp; }
    public String getScale() { return scale; }
}