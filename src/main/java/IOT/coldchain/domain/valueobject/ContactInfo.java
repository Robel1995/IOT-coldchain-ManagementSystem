package IOT.coldchain.domain.valueobject;
//Shared VO for HealthCenter, Dispatcher, and Driver.

import java.util.Objects;

public final class ContactInfo {
    private final String email;
    private final String phoneNumber;

    public ContactInfo(String email, String phoneNumber) {
        if (email != null && !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (phoneNumber != null && !phoneNumber.matches("^\\+?[1-9]\\d{1,14}$")) {
            throw new IllegalArgumentException("Invalid phone number format (E.164 required).");
        }
        if (email == null && phoneNumber == null) {
            throw new IllegalArgumentException("Must provide at least an email or a phone number.");
        }

        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactInfo)) return false;
        ContactInfo that = (ContactInfo) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, phoneNumber);
    }
}
