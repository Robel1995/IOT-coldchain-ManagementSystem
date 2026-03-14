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
}