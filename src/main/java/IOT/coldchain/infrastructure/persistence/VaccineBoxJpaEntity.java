package IOT.coldchain.infrastructure.persistence;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cargo_items")

@Data
@NoArgsConstructor
public class VaccineBoxJpaEntity {
    @Id
    private String serialNumber; // Primary Key
    private String medicineName;
    private boolean isRuined;

    // Getters
    public String getSerialNumber() {
        return serialNumber;
    }
    public String getMedicineName() {
        return medicineName;
    }
    public boolean isRuined() {
        return isRuined;
    }
    // Setters
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }
    public void setRuined(boolean isRuined) {
        this.isRuined = isRuined;
    }
}