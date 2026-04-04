package IOT.coldchain.domain.valueobject;

import java.util.Objects;

public final class MacAddress {
    private final String macAddress;

    public MacAddress(String macAddress) {
        if (macAddress == null || macAddress.isBlank())
            throw new IllegalArgumentException("MAC address cannot be null or blank.");
        // IEEE 802 MAC format: XX:XX:XX:XX:XX:XX (hex pairs separated by colons)
        if (!macAddress
                .trim().toUpperCase().matches("^([0-9A-F]{2}:){5}[0-9A-F]{2}$")) {
            throw new IllegalArgumentException(
                    "Invalid MAC address: '" + macAddress + "'. "
                            + "Expected format: AA:BB:CC:DD:EE:FF"
            );
        }
        this.macAddress = macAddress.toUpperCase();
    }

    public String getValue() { return macAddress; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MacAddress)) return false;
        return macAddress.equals(((MacAddress) o).macAddress);
    }

    @Override
    public int hashCode() { return Objects.hash(macAddress); }
}