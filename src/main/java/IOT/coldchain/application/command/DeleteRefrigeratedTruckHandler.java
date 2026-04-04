package IOT.coldchain.application.command;
import IOT.coldchain.application.port.RefrigeratedTruckRepository;
import IOT.coldchain.domain.entity.RefrigeratedTruck;
import IOT.coldchain.application.port.in.DeleteRefrigeratedTruckUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteRefrigeratedTruckHandler implements DeleteRefrigeratedTruckUseCase {

    private final RefrigeratedTruckRepository repository;

    public DeleteRefrigeratedTruckHandler(RefrigeratedTruckRepository repository){
        this.repository=repository;
    }

    @Transactional
    @Override
    public void handle(DeleteRefrigeratedTruckCommand cmd) {
        RefrigeratedTruck refrigeratedTruck = repository.findById(cmd.containerId)
                .orElseThrow(() -> new RuntimeException("Container not found"));


        refrigeratedTruck.assertCanBeDeleted();

        repository.delete(cmd.containerId);
    }


}
