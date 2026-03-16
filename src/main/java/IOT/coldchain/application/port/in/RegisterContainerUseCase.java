package IOT.coldchain.application.port.in;
import IOT.coldchain.application.command.RegisterContainerCommand;

public interface RegisterContainerUseCase {
    void handle(RegisterContainerCommand command);
}