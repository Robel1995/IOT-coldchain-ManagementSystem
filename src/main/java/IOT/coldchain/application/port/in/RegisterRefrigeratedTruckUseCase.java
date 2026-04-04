package IOT.coldchain.application.port.in;
import IOT.coldchain.application.command.RegisterRefrigeratedTruckCommand;

public interface RegisterRefrigeratedTruckUseCase {
    void handle(RegisterRefrigeratedTruckCommand command);
}