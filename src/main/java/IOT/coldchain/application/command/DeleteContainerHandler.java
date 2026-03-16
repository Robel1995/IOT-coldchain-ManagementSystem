package IOT.coldchain.application.command;
import IOT.coldchain.application.port.ContainerRepository;
import IOT.coldchain.domain.entity.Container;
import IOT.coldchain.application.port.in.DeleteContainerUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteContainerHandler implements DeleteContainerUseCase {

    private final ContainerRepository repository;

    public DeleteContainerHandler(ContainerRepository repository){
        this.repository=repository;
    }

    @Transactional
    @Override
    public void handle(DeleteContainerCommand cmd) {
        Container container = repository.findById(cmd.containerId)
                .orElseThrow(() -> new RuntimeException("Container not found"));


        container.assertCanBeDeleted();

        repository.delete(cmd.containerId);
    }


}
