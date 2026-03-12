package IOT.coldchain.infrastructure.persistence;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpringDataJpaRepository extends JpaRepository<ContainerJpaEntity, String> {
    List<ContainerJpaEntity> findByStatus(String status);
}