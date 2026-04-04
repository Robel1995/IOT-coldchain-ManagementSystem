package IOT.coldchain.application.command;

import IOT.coldchain.application.port.RefrigeratedTruckRepository;
import IOT.coldchain.domain.entity.RefrigeratedTruck;
import IOT.coldchain.application.port.in.RecordTemperatureUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecordTemperatureHandler implements RecordTemperatureUseCase {
    private final RefrigeratedTruckRepository repository;

    public RecordTemperatureHandler(RefrigeratedTruckRepository repository) {
        this.repository = repository;
    }


    @Transactional
    @Override
    public void handle(RecordTemperatureCommand cmd) {
        // Orchestration
        RefrigeratedTruck refrigeratedTruck = repository.findById(cmd.containerId)
                .orElseThrow(() -> new RuntimeException("Container not found"));
        // Call Domain Method (Business Logic)
        refrigeratedTruck.recordTemperature(cmd.temperature);

        // Save state
        repository.save(refrigeratedTruck);
    }
}