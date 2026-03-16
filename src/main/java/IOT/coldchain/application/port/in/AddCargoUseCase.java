package IOT.coldchain.application.port.in;
import IOT.coldchain.application.command.AddCargoCommand;

public interface AddCargoUseCase {
    void handle(AddCargoCommand command);
}
