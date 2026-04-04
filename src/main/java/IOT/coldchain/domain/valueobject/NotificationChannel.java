package IOT.coldchain.domain.valueobject;
//Encapsulates recipient address + channel type.

import java.util.Objects;

public final class NotificationChannel {

    public enum ChannelType { EMAIL, SMS, TELEGRAM, DASHBOARD }

    private final ChannelType type;
    private final String recipientAddress;

    public NotificationChannel(ChannelType type, String recipientAddress) {
        this.type = Objects.requireNonNull(type, "ChannelType cannot be null.");
        if (recipientAddress == null || recipientAddress.isBlank()) {
            throw new IllegalArgumentException("Recipient address cannot be empty.");
        }
        this.recipientAddress = recipientAddress.trim();
    }

    public ChannelType getType() { return type; }
    public String getRecipientAddress() { return recipientAddress; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationChannel)) return false;
        NotificationChannel that = (NotificationChannel) o;
        return type == that.type && recipientAddress.equals(that.recipientAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, recipientAddress);
    }

    @Override
    public String toString() {
        return type.name() + " -> " + recipientAddress;
    }
}