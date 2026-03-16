package IOT.coldchain.application.command;

import IOT.coldchain.application.port.ContainerRepository;
import IOT.coldchain.domain.entity.Container;
import IOT.coldchain.application.port.in.AddCargoUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public  class AddCargoHandler implements AddCargoUseCase {
    private final ContainerRepository repository;

    public AddCargoHandler (ContainerRepository repository){
        this.repository=repository;
    }

    @Transactional
    @Override
    public void handle(AddCargoCommand cmd){
            // fetch aggregate root
        Container container = repository.findById(cmd.containerId).orElseThrow(() -> new RuntimeException("Container not Found"));
        container.addCargo(cmd.serialNumber, cmd.medicineName);
        repository.save(container);
    }
}