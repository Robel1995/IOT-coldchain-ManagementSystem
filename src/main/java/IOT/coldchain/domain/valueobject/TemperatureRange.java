package IOT.coldchain.domain.valueobject;

import java.util.Objects;

/**
 * VALUE OBJECT — TemperatureRange
 *
 * Represents a valid temperature range with minimum and maximum bounds.
 * Self-validating on construction.
 *
 */
public final class TemperatureRange {

    // Default threshold for "approaching limit" detection (in degrees Celsius)
    public static final double DEFAULT_APPROACHING_THRESHOLD = 2.0;

    private final double minTemp;
    private final double maxTemp;
    private final double approachingThreshold;

    /**
     * Creates a TemperatureRange with default approaching threshold (2.0°C).
     */
    public TemperatureRange(double minTemp, double maxTemp) {
        this(minTemp, maxTemp, DEFAULT_APPROACHING_THRESHOLD);
    }

    /**
     * Creates a TemperatureRange with custom approaching threshold.
     *
     * @param minTemp minimum safe temperature
     * @param maxTemp maximum safe temperature
     * @param approachingThreshold how close to boundary before triggering alert
     */
    public TemperatureRange(double minTemp, double maxTemp, double approachingThreshold) {
        if (minTemp < -273.15 || maxTemp < -273.15) {
            throw new IllegalArgumentException("Physics Violation: Below absolute zero.");
        }
        if (minTemp >= maxTemp) {
            throw new IllegalArgumentException("Business Rule: Min temperature must be strictly less than Max.");
        }
        if (approachingThreshold <= 0) {
            throw new IllegalArgumentException("Approaching threshold must be positive.");
        }
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.approachingThreshold = approachingThreshold;
    }

    public boolean isViolatedBy(Temperature reading) {
        return reading.isBelow(minTemp) || reading.isAbove(maxTemp);
    }

    /**
     * Checks if temperature is approaching the limit (within configured threshold).
     */
    public boolean isApproachingLimit(Temperature reading) {
        double value = reading.getValue();
        return (value >= maxTemp - approachingThreshold && value < maxTemp)
                || (value <= minTemp + approachingThreshold && value > minTemp);
    }

    public boolean isAtBoundary(Temperature reading) {
        return reading.isEqualTo(minTemp) || reading.isEqualTo(maxTemp);
    }

    public double getMinTemp() { return minTemp; }
    public double getMaxTemp() { return maxTemp; }
    public double getApproachingThreshold() { return approachingThreshold; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TemperatureRange)) return false;
        TemperatureRange that = (TemperatureRange) o;
        return Double.compare(that.minTemp, minTemp) == 0 &&
                Double.compare(that.maxTemp, maxTemp) == 0 &&
                Double.compare(that.approachingThreshold, approachingThreshold) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minTemp, maxTemp, approachingThreshold);
    }

    @Override
    public String toString() {
        return String.format("Threshold[%.2f°C to %.2f°C]", minTemp, maxTemp);
    }
}