package IOT.coldchain.application.port.in;
import IOT.coldchain.application.command.DeleteRefrigeratedTruckCommand;

public interface DeleteRefrigeratedTruckUseCase {
    void handle(DeleteRefrigeratedTruckCommand command);
}