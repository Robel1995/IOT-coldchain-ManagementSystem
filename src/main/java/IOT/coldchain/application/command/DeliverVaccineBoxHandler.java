package IOT.coldchain.application.command;

import IOT.coldchain.application.port.RefrigeratedTruckRepository;
import IOT.coldchain.domain.entity.RefrigeratedTruck;
import IOT.coldchain.application.port.in.DeliverCargoUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliverVaccineBoxHandler implements DeliverCargoUseCase {
    private final RefrigeratedTruckRepository repository;

    public DeliverVaccineBoxHandler(RefrigeratedTruckRepository repository) {
        this.repository = repository;
    }


    @Transactional
    @Override
    public void handle(DeliverVaccineBoxCommand cmd) {
        RefrigeratedTruck refrigeratedTruck = repository.findById(cmd.containerId)
                .orElseThrow(() -> new RuntimeException("Container not found"));

        // Delegate to the Aggregate Root to enforce the strict rules
        refrigeratedTruck.deliverCargo(cmd.serialNumber);

        repository.save(refrigeratedTruck);
    }
}