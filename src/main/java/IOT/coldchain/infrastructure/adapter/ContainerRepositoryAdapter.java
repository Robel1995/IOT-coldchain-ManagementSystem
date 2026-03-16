package IOT.coldchain.infrastructure.adapter;

import IOT.coldchain.application.port.ContainerRepository;
import IOT.coldchain.domain.entity.CargoItem;
import IOT.coldchain.domain.entity.Container;
import IOT.coldchain.domain.entity.SensorDevice;
import IOT.coldchain.domain.enums.ContainerStatus;
import IOT.coldchain.infrastructure.persistence.CargoItemJpaEntity;
import IOT.coldchain.infrastructure.persistence.ContainerJpaEntity;
import IOT.coldchain.infrastructure.persistence.SensorDeviceJpaEntity;
import IOT.coldchain.infrastructure.persistence.SpringDataJpaRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ContainerRepositoryAdapter implements ContainerRepository {

    private final SpringDataJpaRepository jpaRepo;

    public ContainerRepositoryAdapter(SpringDataJpaRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public void save(Container domainContainer) {
        // 1. Map pure Domain Entity -> JPA Entity
        ContainerJpaEntity jpaEntity = new ContainerJpaEntity();
        jpaEntity.setContainerId(domainContainer.getContainerId());
        jpaEntity.setStatus(domainContainer.getStatus().name());
        // Extract from Value Object
        jpaEntity.setMinSafeTemperature(domainContainer.getThreshold().getMinTemp());
        jpaEntity.setMaxSafeTemperature(domainContainer.getThreshold().getMaxTemp());

        // 2. Map Cargo Items
        jpaEntity.setCargoItems(domainContainer.getCargoItems().stream().map(cargo -> {
    CargoItemJpaEntity cJpa = new CargoItemJpaEntity();
    cJpa.setSerialNumber(cargo.getSerialNumber());
    cJpa.setMedicineName(cargo.getMedicineName());
    cJpa.setRuined(cargo.isRuined()); return cJpa;
    }).collect(Collectors.toList()));
        // 3. Map Sensors
        jpaEntity.setSensors(domainContainer.getSensors().stream().map(sensor -> {
            SensorDeviceJpaEntity sJpa = new SensorDeviceJpaEntity();
            sJpa.setMacAddress(sensor.getMacAddress());
            sJpa.setLocation(sensor.getLocation()); return sJpa;
            }).collect(Collectors.toList()));

        // 4. Save entire Aggregate together
        jpaRepo.save(jpaEntity);
    }

    @Override
    public Optional<Container> findById(String containerId) {
        return jpaRepo.findById(containerId).map(this::toDomain);
    }

    @Override
    public List<Container> findAllSpoiled() {
        return jpaRepo.findByStatus(ContainerStatus.SPOILED.name())
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void delete(String containerId) {
        jpaRepo.deleteById(containerId);
    }

    // --- RECONSTITUTION MAPPER ---
    private Container toDomain(ContainerJpaEntity jpa) {
        // 1. Rebuild Cargo Items by using getters
        List<CargoItem> domainCargo = jpa.getCargoItems().stream()
                // Because CargoItem constructor is protected, we must use reflection or
                // temporarily make it package-private. But actually, we need a reconstitute method on CargoItem
                .map(c -> CargoItem.reconstitute(c.getSerialNumber(), c.getMedicineName(), c.isRuined()))
                .collect(Collectors.toList());

        // 2. Rebuild Sensors
        List<SensorDevice> domainSensors = jpa.getSensors().stream()
                .map(s -> SensorDevice.reconstitute(s.getMacAddress(), s.getLocation()))
                .collect(Collectors.toList());

        // 3. Rebuild Aggregate Root
        return Container.reconstitute(
                jpa.getContainerId(),
                jpa.getMinSafeTemperature(),
                jpa.getMaxSafeTemperature(),
                ContainerStatus.valueOf(jpa.getStatus()),
                domainCargo,
                domainSensors
        );
    }
}

