package IOT.coldchain.application.command;

import IOT.coldchain.application.port.RefrigeratedTruckRepository;
import IOT.coldchain.domain.entity.RefrigeratedTruck;
import IOT.coldchain.application.port.in.AddVaccineBoxUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public  class AddVaccineBoxHandler implements AddVaccineBoxUseCase {
    private final RefrigeratedTruckRepository repository;

    public AddVaccineBoxHandler(RefrigeratedTruckRepository repository){
        this.repository=repository;
    }

    @Transactional
    @Override
    public void handle(AddVaccineBoxCommand cmd){
            // fetch aggregate root
        RefrigeratedTruck refrigeratedTruck = repository.findById(cmd.containerId).orElseThrow(() -> new RuntimeException("Container not Found"));
        refrigeratedTruck.addCargo(cmd.serialNumber, cmd.medicineName);
        repository.save(refrigeratedTruck);
    }
}