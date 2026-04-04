package IOT.coldchain.application.command;

import IOT.coldchain.application.port.RefrigeratedTruckRepository;
import IOT.coldchain.domain.entity.RefrigeratedTruck;
import IOT.coldchain.application.port.in.UpdateRefrigeratedTruckRangeUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateRefrigeratedTruckRangeHandler implements UpdateRefrigeratedTruckRangeUseCase {
    private final RefrigeratedTruckRepository repository;
    public UpdateRefrigeratedTruckRangeHandler(RefrigeratedTruckRepository repository)
    {
        this.repository=repository;
    }

    @Transactional
    @Override
    public void handle(UpdateRefrigeratedTruckRangeCommand cmd)
    {
        RefrigeratedTruck refrigeratedTruck = repository.findById(cmd.containerId).orElseThrow(() -> new RuntimeException("Container not found"));
        refrigeratedTruck.updateTemperatureRange(cmd.newMinTemp, cmd.newMaxTemp);

        repository.save(refrigeratedTruck);

    }
}
