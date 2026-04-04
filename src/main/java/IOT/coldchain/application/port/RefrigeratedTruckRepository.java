package IOT.coldchain.application.port;

import IOT.coldchain.domain.entity.RefrigeratedTruck;

import java.util.List;
import java.util.Optional;

public interface RefrigeratedTruckRepository {
    void save(RefrigeratedTruck refrigeratedTruck);
    Optional<RefrigeratedTruck> findById(String containerId);
    List<RefrigeratedTruck> findAllSpoiled();
    void delete(String containerId);
}