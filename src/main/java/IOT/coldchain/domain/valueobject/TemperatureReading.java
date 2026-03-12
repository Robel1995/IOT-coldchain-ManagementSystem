package IOT.coldchain.domain.valueobject;

public class TemperatureReading {
    private final double value;
    private final String scale;

    public TemperatureReading(double value) {
        // Business Rule  Absolute zero is -273.15C. Impossible to be colder.
        if (value < -273.15) {
            throw new IllegalArgumentException("Physics Violation: Temperature cannot be below absolute zero.");
        }
        this.value = value;
        this.scale = "Celsius";
    }

    public double getValue() {
        return value;
    }

    public String getScale() {
        return scale;
    }
}
