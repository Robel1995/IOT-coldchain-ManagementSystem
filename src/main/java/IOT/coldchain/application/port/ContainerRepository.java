package IOT.coldchain.application.port;

import IOT.coldchain.domain.entity.Container;
import java.util.List;
import java.util.Optional;

public interface ContainerRepository {
    void save(Container container);
    Optional<Container> findById(String containerId);
    List<Container> findAllSpoiled();
    void delete(String containerId);
}