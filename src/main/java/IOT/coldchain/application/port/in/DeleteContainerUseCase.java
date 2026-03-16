package IOT.coldchain.application.port.in;
import IOT.coldchain.application.command.DeleteContainerCommand;

public interface DeleteContainerUseCase {
    void handle(DeleteContainerCommand command);
}