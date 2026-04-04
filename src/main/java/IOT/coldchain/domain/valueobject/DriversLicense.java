package IOT.coldchain.domain.valueobject;

import java.time.LocalDate;
import java.util.Objects;

/**
 * VALUE OBJECT — DriversLicense
 *
 * Encapsulates a truck driver's license credential.
 * Has a domain query method isExpired() — this business logic belongs here,
 * not scattered in service classes.
 */
public final class DriversLicense {

    private final String licenseNumber;
    private final LocalDate expiryDate;

    public DriversLicense(String licenseNumber, LocalDate expiryDate) {
        if (licenseNumber == null || licenseNumber.isBlank()) {
            throw new IllegalArgumentException("License number cannot be null or blank.");
        }
        Objects.requireNonNull(expiryDate, "Expiry date cannot be null.");

        this.licenseNumber = licenseNumber.trim().toUpperCase();
        this.expiryDate = expiryDate;
    }

    /**
     * Domain rule: a driver cannot be assigned to a truck if their license is expired.
     */
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    /**
     * Returns days remaining until expiry. Negative if already expired.
     */
    public long daysUntilExpiry() {
        return LocalDate.now().until(expiryDate).getDays();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DriversLicense)) return false;
        DriversLicense that = (DriversLicense) o;
        return Objects.equals(licenseNumber, that.licenseNumber)
                && Objects.equals(expiryDate, that.expiryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(licenseNumber, expiryDate);
    }

    @Override
    public String toString() {
        return "DriversLicense[" + licenseNumber + ", expires=" + expiryDate + "]";
    }

    public String getLicenseNumber() { return licenseNumber; }
    public LocalDate getExpiryDate() { return expiryDate; }
}