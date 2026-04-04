package IOT.coldchain.infrastructure.adapter;

import IOT.coldchain.application.port.RefrigeratedTruckRepository;
import IOT.coldchain.domain.entity.VaccineBox;
import IOT.coldchain.domain.entity.RefrigeratedTruck;
import IOT.coldchain.domain.entity.TemperatureSensor;
import IOT.coldchain.domain.enums.TruckState;
import IOT.coldchain.domain.enums.TruckStatus;
import IOT.coldchain.infrastructure.persistence.VaccineBoxJpaEntity;
import IOT.coldchain.infrastructure.persistence.RefrigeratedTruckJpaEntity;
import IOT.coldchain.infrastructure.persistence.TemperatureSensorJpaEntity;
import IOT.coldchain.infrastructure.persistence.SpringDataJpaRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RefrigeratedTruckRepositoryAdapter implements RefrigeratedTruckRepository {

    private final SpringDataJpaRepository jpaRepo;

    public RefrigeratedTruckRepositoryAdapter(SpringDataJpaRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public void save(RefrigeratedTruck domainRefrigeratedTruck) {
        // 1. Map pure Domain Entity -> JPA Entity
        RefrigeratedTruckJpaEntity jpaEntity = new RefrigeratedTruckJpaEntity();
        jpaEntity.setContainerId(domainRefrigeratedTruck.getContainerId());
        jpaEntity.setStatus(domainRefrigeratedTruck.getStatus().name());
        // Extract from Value Object
        jpaEntity.setMinSafeTemperature(domainRefrigeratedTruck.getThreshold().getMinTemp());
        jpaEntity.setMaxSafeTemperature(domainRefrigeratedTruck.getThreshold().getMaxTemp());

        // 2. Map Cargo Items
        jpaEntity.setCargoItems(domainRefrigeratedTruck.getCargoItems().stream().map(cargo -> {
    VaccineBoxJpaEntity cJpa = new VaccineBoxJpaEntity();
    cJpa.setSerialNumber(cargo.getSerialNumber());
    cJpa.setMedicineName(cargo.getMedicineName());
    cJpa.setRuined(cargo.isRuined()); return cJpa;
    }).collect(Collectors.toList()));
        // 3. Map Sensors
        jpaEntity.setSensors(domainRefrigeratedTruck.getSensors().stream().map(sensor -> {
            TemperatureSensorJpaEntity sJpa = new TemperatureSensorJpaEntity();
            sJpa.setMacAddress(sensor.getMacAddress());
            sJpa.setLocation(sensor.getLocation()); return sJpa;
            }).collect(Collectors.toList()));

        // 4. Save entire Aggregate together
        jpaRepo.save(jpaEntity);
    }

    @Override
    public Optional<RefrigeratedTruck> findById(String containerId) {
        return jpaRepo.findById(containerId).map(this::toDomain);
    }

    @Override
    public List<RefrigeratedTruck> findAllSpoiled() {
        return jpaRepo.findByStatus(TruckState.SPOILED.name())
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void delete(String containerId) {
        jpaRepo.deleteById(containerId);
    }

    // --- RECONSTITUTION MAPPER ---
    private RefrigeratedTruck toDomain(RefrigeratedTruckJpaEntity jpa) {
        // 1. Rebuild Cargo Items by using getters
        List<VaccineBox> domainCargo = jpa.getCargoItems().stream()
                // Because CargoItem constructor is protected, we must use reflection or
                // temporarily make it package-private. But actually, we need a reconstitute method on CargoItem
                .map(c -> VaccineBox.reconstitute(c.getSerialNumber(), c.getMedicineName(), c.isRuined()))
                .collect(Collectors.toList());

        // 2. Rebuild Sensors
        List<TemperatureSensor> domainSensors = jpa.getSensors().stream()
                .map(s -> TemperatureSensor.reconstitute(s.getMacAddress(), s.getLocation()))
                .collect(Collectors.toList());

        // 3. Rebuild Aggregate Root
        return RefrigeratedTruck.reconstitute(
                jpa.getContainerId(),
                jpa.getMinSafeTemperature(),
                jpa.getMaxSafeTemperature(),
                TruckState.valueOf(jpa.getStatus()),
                domainCargo,
                domainSensors
        );
    }
}

