package IOT.coldchain.application.command;

import IOT.coldchain.application.port.ContainerRepository;
import IOT.coldchain.domain.entity.Container;
import org.springframework.stereotype.Service;

@Service
public  class AddCargoHandler {
    private final ContainerRepository repository;

    public AddCargoHandler (ContainerRepository repository){
        this.repository=repository;
    }
    public void Handler(AddCargoCommand cmd){
            // fetch aggregate root
        Container container = repository.findById(cmd.containerId).orElseThrow(() -> new RuntimeException("Container not Found"));
        container.addCargo(cmd.serialNumber, cmd.medicineName);
        repository.save(container);
    }
}