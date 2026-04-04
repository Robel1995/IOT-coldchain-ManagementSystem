package IOT.coldchain.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * VALUE OBJECT — ZoneName
 *
 * Represents the name/label of a compartment zone inside a refrigerated truck.
 * A plain String is not enough — we need format validation so callers cannot
 * pass empty strings, whitespace, or nonsense like "zone!!@#" as a zone name.
 *
 * Examples of valid names: "ZONE_A", "FRONT", "COLD_1", "ULTRA_COLD"
 *
 * Value Object contract:
 *  - Immutable
 *  - Equality by VALUE (the name string)
 *  - Self-validating
 */
public final class ZoneName {

    // Only uppercase letters, digits, and underscores. 1–20 characters.
    private static final Pattern VALID_FORMAT = Pattern.compile("^[A-Z0-9_]{1,20}$");

    private final String name;

    public ZoneName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("ZoneName cannot be null or blank.");
        }
        String trimmed = name.trim().toUpperCase();
        if (!VALID_FORMAT.matcher(trimmed).matches()) {
            throw new IllegalArgumentException(
                    "ZoneName '" + name + "' is invalid. Use uppercase letters, digits, "
                            + "and underscores only (1–20 characters). Example: 'ZONE_A'"
            );
        }
        this.name = trimmed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ZoneName)) return false;
        return Objects.equals(name, ((ZoneName) o).name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() { return name; }
}