package IOT.coldchain.application.port.in;
import IOT.coldchain.application.command.RecordTemperatureCommand;
public interface RecordTemperatureUseCase {
    void handle(RecordTemperatureCommand command);
}
