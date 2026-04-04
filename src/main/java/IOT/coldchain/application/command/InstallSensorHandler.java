package IOT.coldchain.application.command;

import IOT.coldchain.application.port.RefrigeratedTruckRepository;
import IOT.coldchain.domain.entity.RefrigeratedTruck;
import IOT.coldchain.application.port.in.InstallSensorUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InstallSensorHandler implements InstallSensorUseCase {
    private final RefrigeratedTruckRepository repository;

    public InstallSensorHandler(RefrigeratedTruckRepository repository) {
        this.repository = repository;
    }


    @Transactional
    @Override
    public void handle(InstallSensorCommand cmd) {
        RefrigeratedTruck refrigeratedTruck = repository.findById(cmd.containerId)
                .orElseThrow(() -> new RuntimeException("Container not found"));

        refrigeratedTruck.addSensor(cmd.macAddress, cmd.location);

        repository.save(refrigeratedTruck);
    }
}