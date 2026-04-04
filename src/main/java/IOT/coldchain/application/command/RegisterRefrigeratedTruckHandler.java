package IOT.coldchain.application.command;

import IOT.coldchain.application.port.RefrigeratedTruckRepository;
import IOT.coldchain.domain.entity.RefrigeratedTruck;
import IOT.coldchain.domain.entity.RefrigeratedTruckFactory;
import IOT.coldchain.application.port.in.RegisterRefrigeratedTruckUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterRefrigeratedTruckHandler implements RegisterRefrigeratedTruckUseCase {
    private final RefrigeratedTruckRepository repository;

    public RegisterRefrigeratedTruckHandler(RefrigeratedTruckRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Override
    public void handle(RegisterRefrigeratedTruckCommand cmd) {
        // Factory to create Domain Entity
        RefrigeratedTruck refrigeratedTruck = RefrigeratedTruckFactory.createNew(cmd.containerId, cmd.minTemp, cmd.maxTemp);
        repository.save(refrigeratedTruck);
    }
}