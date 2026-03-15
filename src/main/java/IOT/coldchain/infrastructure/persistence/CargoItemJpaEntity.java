package IOT.coldchain.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cargo_items")
public class CargoItemJpaEntity {
    @Id
    public String serialNumber; // Primary Key
    public String medicineName;
    public boolean isRuined;
}