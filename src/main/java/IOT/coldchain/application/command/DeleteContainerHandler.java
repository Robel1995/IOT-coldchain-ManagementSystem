package IOT.coldchain.application.command;
import IOT.coldchain.application.port.ContainerRepository;
import IOT.coldchain.domain.entity.Container;
import org.springframework.stereotype.Service;

@Service
public class DeleteContainerHandler {

    private final ContainerRepository repository;

    public DeleteContainerHandler(ContainerRepository repository){
        this.repository=repository;
    }

    public void handle(DeleteContainerCommand cmd) {
        Container container = repository.findById(cmd.containerId)
                .orElseThrow(() -> new RuntimeException("Container not found"));

        // Enforce Domain Business Rule before deleting
        if (!container.canBeDeleted()) {
            throw new IllegalStateException("Business Rule Violation: Cannot delete a SAFE container.");
        }

        repository.delete(cmd.containerId);
    }


}
