package IOT.coldchain.domain.valueobject;

import java.time.Instant;
import java.util.Objects;

// Temperature.java — standalone immutable Value Object
public final class Temperature {
    private final double value;      // the numeric reading
    private final Instant recordedAt;  // WHEN was it measured

    public Temperature(double value, Instant recordedAt) {
        if (value < -273.15)
            throw new IllegalArgumentException(
                    "Physics Violation: Temperature " + value + " is below absolute zero.");
        Objects.requireNonNull(recordedAt, "recordedAt cannot be null");
        this.value = value;
        this.recordedAt = recordedAt;
    }

    // Convenience factory
    public static Temperature now(double value) {
        return new Temperature(value, Instant.now());
    }

    public boolean isBelow(double boundary) {
        return Double.compare(this.value, boundary) < 0;
    }
    public boolean isAbove(double boundary) {
        return Double.compare(this.value, boundary) > 0;
    }
    public boolean isEqualTo(double boundary) {
        return Double.compare(this.value, boundary) == 0;
    }

    // Value Object equality (value + time)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Temperature)) return false;
        Temperature that = (Temperature) o;
        return Double.compare(that.value, this.value) == 0
                && Objects.equals(this.recordedAt, that.recordedAt);
    }

    @Override public int hashCode() { return Objects.hash(value, recordedAt); }
    @Override public String toString() { return value + "°C @ " + recordedAt; }

    public double getValue() { return value; }
    public Instant getRecordedAt() { return recordedAt; }
}