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
        jpaEntity.containerId = domainContainer.getContainerId();
        jpaEntity.status = domainContainer.getStatus().name();

        // Extract from Value Object
        jpaEntity.minSafeTemperature = domainContainer.getThreshold().getMinTemp();
        jpaEntity.maxSafeTemperature = domainContainer.getThreshold().getMaxTemp();

        // 2. Map Cargo Items
        jpaEntity.cargoItems = domainContainer.getCargoItems().stream().map(cargo -> {
            CargoItemJpaEntity cJpa = new CargoItemJpaEntity();
            cJpa.serialNumber = cargo.getSerialNumber();
            cJpa.medicineName = cargo.getMedicineName();
            cJpa.isRuined = cargo.isRuined();
            return cJpa;
        }).collect(Collectors.toList());

        // 3. Map Sensors
        jpaEntity.sensors = domainContainer.getSensors().stream().map(sensor -> {
            SensorDeviceJpaEntity sJpa = new SensorDeviceJpaEntity();
            sJpa.macAddress = sensor.getMacAddress();
            sJpa.location = sensor.getLocation();
            return sJpa;
        }).collect(Collectors.toList());

        // 4. Save entire Aggregate together!
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
        // 1. Rebuild Cargo Items
        List<CargoItem> domainCargo = jpa.cargoItems.stream()
                // Because CargoItem constructor is protected, we must use reflection or
                // temporarily make it package-private. But actually, we need a reconstitute method on CargoItem too!
                // *For simplicity here, we assume we added a reconstitute method to CargoItem & SensorDevice*.
                .map(c -> CargoItem.reconstitute(c.serialNumber, c.medicineName, c.isRuined))
                .collect(Collectors.toList());

        // 2. Rebuild Sensors
        List<SensorDevice> domainSensors = jpa.sensors.stream()
                .map(s -> SensorDevice.reconstitute(s.macAddress, s.location))
                .collect(Collectors.toList());

        // 3. Rebuild Aggregate Root
        return Container.reconstitute(
                jpa.containerId,
                jpa.minSafeTemperature,
                jpa.maxSafeTemperature,
                ContainerStatus.valueOf(jpa.status),
                domainCargo,
                domainSensors
        );
    }
}


/*
package IOT.coldchain.infrastructure.adapter;


import IOT.coldchain.application.port.ContainerRepository;
import IOT.coldchain.domain.entity.Container;
import IOT.coldchain.domain.enums.ContainerStatus;
import IOT.coldchain.infrastructure.persistence.ContainerJpaEntity;
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
        // Map pure Domain Entity -> JPA Entity
        ContainerJpaEntity jpaEntity = new ContainerJpaEntity();
        jpaEntity.containerId = domainContainer.getContainerId();
        jpaEntity.minSafeTemperature = domainContainer.getMinSafeTemperature();
        jpaEntity.maxSafeTemperature = domainContainer.getMaxSafeTemperature();
        jpaEntity.status = domainContainer.getStatus().name();

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
    public void delete(String containerId){
        jpaRepo.deleteById(containerId);
    }
    // Map JPA Entity -> pure Domain Entity
    private Container toDomain(ContainerJpaEntity jpa) {
        return  Container.reconstitute(jpa.containerId, jpa.minSafeTemperature, jpa.maxSafeTemperature, ContainerStatus.valueOf(jpa.status));
    }
}*/
