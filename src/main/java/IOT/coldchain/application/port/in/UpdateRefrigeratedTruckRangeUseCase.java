package IOT.coldchain.application.port.in;
import IOT.coldchain.application.command.UpdateRefrigeratedTruckRangeCommand;

public interface UpdateRefrigeratedTruckRangeUseCase {
    void handle(UpdateRefrigeratedTruckRangeCommand command);
}