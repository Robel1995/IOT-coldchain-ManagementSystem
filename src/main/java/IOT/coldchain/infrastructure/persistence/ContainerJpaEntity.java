package IOT.coldchain.infrastructure.persistence;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "containers")
public class ContainerJpaEntity {
    @Id
    public String containerId;
    public double minSafeTemperature;
    public double maxSafeTemperature;
    public String status;
}